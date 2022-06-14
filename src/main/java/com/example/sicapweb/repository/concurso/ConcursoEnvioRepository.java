package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.ap.concurso.ConcursoEnvio;
import br.gov.to.tce.model.ap.concurso.ProcessoAdmissao;
import br.gov.to.tce.model.ap.concurso.documento.DocumentoEdital;
import com.example.sicapweb.model.ProcessoAdmissaoConcurso;
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
public class ConcursoEnvioRepository extends DefaultRepository<ConcursoEnvio, BigInteger> {

    public ConcursoEnvioRepository(EntityManager em) {
        super(em);
    }

    public List<ConcursoEnvio> buscarEnvioFAse1PorEdital(BigInteger idEdital) {
        return  getEntityManager().createNativeQuery(
                        "select ev.* from ConcursoEnvio ev "+
                               " where fase=1 and  idEdital = " + idEdital, ConcursoEnvio.class)
                .getResultList();
    }

    public List<ConcursoEnvio> buscarEnvioFAse2PorEdital(BigInteger idEdital) {
        return  getEntityManager().createNativeQuery(
                        "select ev.* from ConcursoEnvio ev "+
                                " where fase=1 and  idEdital = " + idEdital, ConcursoEnvio.class)
                .getResultList();
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


    public PaginacaoUtil<ConcursoEnvio> buscarEnviosAguardandoAss(Pageable pageable, String searchParams, Integer tipoParams) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";
        search = getSearch(searchParams, tipoParams);
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<ConcursoEnvio> list = getEntityManager()
                .createNativeQuery("select a.* from ConcursoEnvio a " +
                        "where a.status=1 " + search + " ORDER BY " + campo, ConcursoEnvio.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();


        long totalRegistros = countEnviosAguardandoAss();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;

//        List<ProcessoAdmissaoConcurso> listc= new ArrayList<ProcessoAdmissaoConcurso>() ;
//        for(Integer i= 0; i < list.size(); i++){
//            ProcessoAdmissaoConcurso pac =new ProcessoAdmissaoConcurso();
//            pac.setNumeroEdital(list.get(i).getEdital().getNumeroEdital());
//            pac.setId(list.get(i).getId());
//            pac.setDtcriacao(list.get(i).getDataCriacao());
//            pac.setStatus(list.get(i).getStatus());
//            pac.setEdital(list.get(i).getEdital());
//            Integer qt = (Integer)  getEntityManager().createNativeQuery("select count(*) from DocumentoAdmissao a " +
//                    "where   a.idProcessoAdmissao = "+ pac.getId()+ "").getSingleResult();
//            pac.setQuantidade(qt);
//            listc.add(pac);
//        }

        return new PaginacaoUtil<ConcursoEnvio>(tamanho, pagina, totalPaginas, totalRegistros, list);
    }



    public Integer countEnviosAguardandoAss() {
        Query query = getEntityManager().createNativeQuery("select count(*) from ConcursoEnvio a " +
                "where a.status=1 ");
        return (Integer) query.getSingleResult();
    }


}
