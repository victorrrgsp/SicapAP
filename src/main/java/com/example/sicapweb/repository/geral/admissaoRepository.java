package com.example.sicapweb.repository.geral;


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
import java.util.List;

@Repository
public class admissaoRepository extends DefaultRepository<Admissao, BigInteger> {
    public admissaoRepository(EntityManager em) {
        super(em);
    }

    public String getSearch(String searchParams, Integer tipoParams) {
        String search = "";
        //monta pesquisa search
        if (searchParams.length() > 3) {
            if (tipoParams == 0) { //entra para tratar a string
                String arrayOfStrings[] = searchParams.split("=");
                if (arrayOfStrings[0].equals("numeroEdital"))
                    search = " and a." + arrayOfStrings[0] + " LIKE '%" + arrayOfStrings[1] + "%'  ";
                else if (arrayOfStrings[0].equals("veiculoPublicacao"))
                    search = " and a." + arrayOfStrings[0] + " LIKE '%" + arrayOfStrings[1] + "%'  ";
                else if (arrayOfStrings[0].equals("dataPublicacao"))
                    search = " and a." + arrayOfStrings[0] + " LIKE '%" + arrayOfStrings[1] + "%'  ";
                else
                    search = " and " + arrayOfStrings[0] + " LIKE '%" + arrayOfStrings[1] + "%'  ";
            } else {
                search = " and " + searchParams + "   ";
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
                .createNativeQuery("select a.* from Admissao a " +
                        "join InfoRemessa i on a.chave = i.chave " +
                        "where a.tipoAdmissao =1 and  i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "' " + search + " ORDER BY " + campo, Admissao.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();


        long totalRegistros = countAdmissoes();
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
                    .createNativeQuery("select a.* from EditalAprovado a " +
                            "join InfoRemessa i on a.chave = i.chave " +

                            " where    cpf='" + nc.getCpf()  +  "'" + " and a.numeroInscricao='"+ list.get(i).getNumeroInscricao() +"' " + " and  i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId()+"'", EditalAprovado.class)
                    .getResultList();
            if (ea.size()>0 ){
                nc.setEditalAprovado((EditalAprovado) ea.get(0) );

                nc.setClassificacao(ea.get(0).getClassificacao());
                nc.setSitCadAprovado("Aprovado Cadastrado");
                nc.setVaga(ea.get(0).getEditalVaga().getCargo().getCargoNome().getNome());
                Integer enviado = (Integer) getEntityManager().createNativeQuery("select count(*) from DocumentoAdmissao a " +
                        "where  a.idAdmissao = "+list.get(i).getId()+ "").getSingleResult();
                if (enviado > 0 ){

                    nc.setSituacaoNomeacao("Aprovado anexado");
                }
                else
                {
                    nc.setSituacaoNomeacao("Apto para envio!");
                }
            }
            else
            {
                nc.setSitCadAprovado("Aprovado não Cadastrado!");
                nc.setSituacaoNomeacao("inapto para envio!");
            }

          listc.add(nc);
        }
        return new PaginacaoUtil<NomeacaoConcurso>(tamanho, pagina, totalPaginas, totalRegistros, listc);
    }

    public Integer countAdmissoes() {
        Query query = getEntityManager().createNativeQuery("select count(*) from Admissao a " +
                "join InfoRemessa i on a.chave = i.chave " +
                "where  a.tipoAdmissao =1 and i.idUnidadeGestora= '"+ User.getUser(super.request).getUnidadeGestora().getId()+ "'");
        return (Integer) query.getSingleResult();
    }
}
