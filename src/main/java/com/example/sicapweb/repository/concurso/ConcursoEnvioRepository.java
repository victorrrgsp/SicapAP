package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.ap.concurso.ConcursoEnvio;
import com.example.sicapweb.exception.InvalitInsert;
import com.example.sicapweb.model.ConcursoEnvioAssRetorno;
import com.example.sicapweb.repository.DefaultRepository;
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
                                " where fase=2 and  idEdital = " + idEdital, ConcursoEnvio.class)
                .getResultList();
    }

    public ConcursoEnvio buscarEnvioFAse1PorEditalassinado(BigInteger idEdital) {
        List<ConcursoEnvio> lc=  getEntityManager().createNativeQuery(
                        "select  ev.* from ConcursoEnvio ev "+
                                " where fase=1 and status=3 and  idEdital = " + idEdital+ " order by dataEnvio desc ", ConcursoEnvio.class)
                .getResultList();
        if (lc.size() ==1 ) {return lc.get(0);
        } else if (lc.size() > 1 ) {
            throw new InvalitInsert("Encontrou mais de um processo pai para o envio da homologação!! ");
        }

        return null;
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


    public PaginacaoUtil<ConcursoEnvioAssRetorno> buscarEnviosAguardandoAss(Pageable pageable, String searchParams, Integer tipoParams) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";
        search = getSearch(searchParams, tipoParams);
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<ConcursoEnvio> list = getEntityManager()
                .createNativeQuery("select a.* from ConcursoEnvio a " +
                        " where not exists(select 1 from ConcursoEnvioAssinatura ass  where  ass.idEnvio=a.id) " + search + " ORDER BY " + campo, ConcursoEnvio.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();


        long totalRegistros = countEnviosAguardandoAss();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;

        List<ConcursoEnvioAssRetorno> listc= new ArrayList<ConcursoEnvioAssRetorno>() ;
        for(Integer i= 0; i < list.size(); i++){
            ConcursoEnvioAssRetorno pac =new ConcursoEnvioAssRetorno();
            pac.setConcursoEnvio(list.get(i));
            Integer qt = (Integer)  getEntityManager().createNativeQuery("select count(*) from ConcursoEnvioAssinatura a " +
                    "where   a.idEnvio = "+ list.get(i).getId()+ "").getSingleResult();
            if (qt ==0 ){
                pac.setStatusAssinatura(1);
            }else  pac.setStatusAssinatura(2);
          listc.add(pac);
        }

        return new PaginacaoUtil<ConcursoEnvioAssRetorno>(tamanho, pagina, totalPaginas, totalRegistros, listc);
    }



    public Integer countEnviosAguardandoAss() {
        Query query = getEntityManager().createNativeQuery("select count(*) from ConcursoEnvio a " +
                " where not exists(select 1 from ConcursoEnvioAssinatura ass  where  ass.idEnvio=a.id) ");
        return (Integer) query.getSingleResult();
    }


}
