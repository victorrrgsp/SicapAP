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

        var querylistAdmissoes =  getEntityManager()
                .createNativeQuery("with admissao1 as (select a.* from (select a.*,RANK() OVER(PARTITION BY a.matriculaServidor,a.numeroEdital,ato.numeroAto,ato.tipoAto,Servidor.cpfServidor,a.tipoAdmissao ORDER BY a.id DESC) as rank\n" +
                        "                   from Admissao a\n" +
                        "                            join InfoRemessa i\n" +
                        "                                 on a.chave = i.chave and i.idUnidadeGestora = :ug and a.tipoAdmissao = 1\n" +
                        "                            join ato on a.idAto = Ato.id\n" +
                        "                            join Servidor on a.idServidor = Servidor.id\n" +
                        "                   ) a where rank = 1 ),\n" +
                        "     servidor1 as (select d.*\n" +
                        "                   from Servidor d\n" +
                        "                            join InfoRemessa i on d.chave = i.chave and i.idUnidadeGestora = :ug)\n" +
                        "select c.*\n" +
                        "from admissao1 c\n" +
                        "         join servidor1 s on c.idServidor = s.id\n" +
                        "where 1 = 1 " +
                        search + " ORDER BY " + campo, Admissao.class)
                .setParameter("ug",redisConnect.getUser(super.request).getUnidadeGestora().getId());
        List<Admissao> listAdmissoes = querylistAdmissoes.setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();

            long totalRegistros = countAdmissoes(search);
            long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
            List<NomeacaoConcurso> nomeacaoConcursoList= new ArrayList<NomeacaoConcurso>();
            for(Integer i= 0; i < listAdmissoes.size(); i++){
                NomeacaoConcurso nomeacaoConcurso =new NomeacaoConcurso();
                nomeacaoConcurso.setNome(listAdmissoes.get(i).getServidor().getNome());
                nomeacaoConcurso.setCpf(listAdmissoes.get(i).getServidor().getCpfServidor());
                nomeacaoConcurso.setAto(listAdmissoes.get(i).getAto());
                nomeacaoConcurso.setNumeroEdital(listAdmissoes.get(i).getNumeroEdital());
                nomeacaoConcurso.setAdmissao(listAdmissoes.get(i));
                var queryEditalAprovado =  getEntityManager()
                        .createNativeQuery(
                                " with vaga as ( " +
                                "   select v.* from  EditalVaga v   join InfoRemessa i  on v.chave=i.chave and i.idUnidadeGestora = :ug  join Cargo c  on v.idCargo=c.id  and   c.codigoCargo=:codigoCargo    " +
                                "), " +
                                "Aprovado as (" +
                                "  select v.* from  EditalAprovado v join InfoRemessa i  on v.chave=i.chave and i.idUnidadeGestora = :ug and cpf= :cpf and cast( v.numeroInscricao as int ) = cast (:numeroInscricao as int)  " +
                                " ) " +
                                "select a.* from Aprovado a " +
                                "join vaga b on a.idEditalVaga= b.id " , EditalAprovado.class)
                        .setParameter("ug",redisConnect.getUser(super.request).getUnidadeGestora().getId())
                        .setParameter("codigoCargo",listAdmissoes.get(i).getCargo().getCodigoCargo())
                        .setParameter("numeroInscricao",listAdmissoes.get(i).getNumeroInscricao())
                        .setParameter("cpf",nomeacaoConcurso.getCpf());
                List<EditalAprovado> editalAprovadoList = queryEditalAprovado.getResultList();

            if (editalAprovadoList.size()>0 ){
                nomeacaoConcurso.setEditalAprovado((EditalAprovado) editalAprovadoList.get(0) );
                nomeacaoConcurso.setClassificacao(editalAprovadoList.get(0).getClassificacao());
                nomeacaoConcurso.setSitCadAprovado("Aprovado Cadastrado");
                nomeacaoConcurso.setVaga(editalAprovadoList.get(0).getEditalVaga().getCargo().getCargoNome().getNome());
                Integer enviado = (Integer) getEntityManager().createNativeQuery("select count(*) from DocumentoAdmissao a " +
                        "where  status >0 and  a.idAdmissao = "+listAdmissoes.get(i).getId()+ "").getSingleResult();
                    nomeacaoConcurso.setSituacaoNomeacao((enviado > 0)?"Aprovado anexado" : "Apto para envio!");
            } else
            {
                nomeacaoConcurso.setSitCadAprovado("Aprovado não Cadastrado!");
                nomeacaoConcurso.setSituacaoNomeacao("inapto para envio!");
            }

            nomeacaoConcursoList.add(nomeacaoConcurso);
        }
        return new PaginacaoUtil<NomeacaoConcurso>(tamanho, pagina, totalPaginas, totalRegistros, nomeacaoConcursoList);
    }

    public Integer countAdmissoes(String search) {
        Query query = getEntityManager().createNativeQuery("with admissao1  as " +
                        "( select a.* from Admissao a  join InfoRemessa i on a.chave = i.chave and i.idUnidadeGestora = :ug and a.tipoAdmissao=1  ), " +
                        " servidor1 as ( select d.* from Servidor d  join InfoRemessa i on d.chave = i.chave and i.idUnidadeGestora = :ug  ) " +
                        "select count(1) from admissao1 c  join  servidor  s on   c.idServidor = s.id " + " " + search)
                .setParameter("ug",redisConnect.getUser(super.request).getUnidadeGestora().getId());
        return (Integer) query.getSingleResult();
    }

}
