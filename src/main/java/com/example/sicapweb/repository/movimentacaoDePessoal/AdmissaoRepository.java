package com.example.sicapweb.repository.movimentacaoDePessoal;

import br.gov.to.tce.model.ap.concurso.EditalAprovado;
import br.gov.to.tce.model.ap.pessoal.Admissao;
import com.example.sicapweb.model.NomeacaoConcurso;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class AdmissaoRepository extends DefaultRepository<Admissao, BigInteger> {

    public AdmissaoRepository(EntityManager em) {
        super(em);
    }

    public String getSearch(String searchParams, Integer tipoParams) {
        String search = "";
        //monta pesquisa search
        if (searchParams.length() > 3) {
            List<String> lparam =  new ArrayList(Arrays.asList(searchParams.split("&")));
            for (String param : lparam){
                if (tipoParams == 0) { //entra para tratar a string
                    String arrayOfStrings[] = param.split("=");
                    if (arrayOfStrings[0].equals("cpfServidor")){
                        search = search + " AND s." + arrayOfStrings[0]  + " = '" + arrayOfStrings[1] + "'  ";
                    }
                    else if (arrayOfStrings[0].equals("numeroEdital")) {
                        search = search + " AND c." + arrayOfStrings[0]  + " = '" + arrayOfStrings[1] + "'  ";
                    }
                    else if (arrayOfStrings[0].equals("idAto")) {
                        search = search + " AND c." + arrayOfStrings[0]  + " = " + arrayOfStrings[1] + "  ";
                    }

                } else {
                    search = " AND " + searchParams + "   ";
                }
            }
        }
        return search;
    }

    public PaginacaoUtil<NomeacaoConcurso> buscarAdmissoes(Pageable pageable, String searchParams, Integer tipoParams) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";
        search = getSearch(searchParams, tipoParams);
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<Admissao> list = getEntityManager()
                .createNativeQuery("with admissao1  as " +
                        "( select a.* from Admissao a  join InfoRemessa i on a.chave = i.chave and i.idUnidadeGestora = :ug and a.tipoAdmissao=1  ), " +
                        " servidor1 as ( select d.* from Servidor d  join InfoRemessa i on d.chave = i.chave and i.idUnidadeGestora = :ug  ) " +
                        "select c.* from admissao1 c  join  servidor1  s on   c.idServidor = s.id " + " " + search + " ORDER BY " + campo, Admissao.class)
                .setParameter("ug",User.getUser(super.request).getUnidadeGestora().getId())
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();


             long totalRegistros = countAdmissoes(search);
            long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
            List<NomeacaoConcurso> listc= new ArrayList<NomeacaoConcurso>() ;
            for(Integer i= 0; i < list.size(); i++){
                NomeacaoConcurso nc =new NomeacaoConcurso();
                nc.setNome(list.get(i).getServidor().getNome());
                nc.setCpf(list.get(i).getServidor().getCpfServidor());
                nc.setAto(list.get(i).getAto());
                nc.setNumeroEdital(list.get(i).getNumeroEdital());
                nc.setAdmissao(list.get(i));
                List<EditalAprovado> ea =  getEntityManager()
                        .createNativeQuery(" with vaga as ( " +
                                "   select v.* from  EditalVaga v   join InfoRemessa i  on v.chave=i.chave and i.idUnidadeGestora = :ug  join Cargo c  on v.idCargo=c.id  and   c.codigoCargo=:codigoCargo    " +
                                "), " +
                                "Aprovado as (" +
                                "  select v.* from  EditalAprovado v join InfoRemessa i  on v.chave=i.chave and i.idUnidadeGestora = :ug and cpf= :cpf and cast( v.numeroInscricao as int ) = cast (:numeroInscricao as int)  " +
                                " ) " +
                                "select a.* from Aprovado a " +
                                "join vaga b on a.idEditalVaga= b.id "  , EditalAprovado.class)
                        .setParameter("ug",User.getUser(super.request).getUnidadeGestora().getId())
                        .setParameter("codigoCargo",list.get(i).getCargo().getCodigoCargo())
                        .setParameter("numeroInscricao",list.get(i).getNumeroInscricao())
                        .setParameter("cpf",nc.getCpf())
                        .getResultList();

            if (ea.size()>0 ){
                nc.setEditalAprovado((EditalAprovado) ea.get(0) );
                nc.setClassificacao(ea.get(0).getClassificacao());
                nc.setSitCadAprovado("Aprovado Cadastrado");
                nc.setVaga(ea.get(0).getEditalVaga().getCargo().getCargoNome().getNome());
                Integer enviado = (Integer) getEntityManager().createNativeQuery("select count(*) from DocumentoAdmissao a " +
                        "where  status >0 and  a.idAdmissao = "+list.get(i).getId()+ "").getSingleResult();
                    nc.setSituacaoNomeacao((enviado > 0)?"Aprovado anexado" : "Apto para envio!");
            } else
            {
                nc.setSitCadAprovado("Aprovado não Cadastrado!");
                nc.setSituacaoNomeacao("inapto para envio!");
            }

            listc.add(nc);
        }
        return new PaginacaoUtil<NomeacaoConcurso>(tamanho, pagina, totalPaginas, totalRegistros, listc);
    }

    public Integer countAdmissoes(String search) {
        Query query = getEntityManager().createNativeQuery("with admissao1  as " +
                        "( select a.* from Admissao a  join InfoRemessa i on a.chave = i.chave and i.idUnidadeGestora = :ug and a.tipoAdmissao=1  ), " +
                        " servidor1 as ( select d.* from Servidor d  join InfoRemessa i on d.chave = i.chave and i.idUnidadeGestora = :ug  ) " +
                        "select count(1) from admissao1 c  join  servidor  s on   c.idServidor = s.id " + " " + search)
                .setParameter("ug",User.getUser(super.request).getUnidadeGestora().getId())
                ;
        return (Integer) query.getSingleResult();
    }

}
