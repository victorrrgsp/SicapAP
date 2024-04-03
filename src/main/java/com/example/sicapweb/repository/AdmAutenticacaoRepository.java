package com.example.sicapweb.repository;
import br.gov.to.tce.model.UnidadeGestora;
import br.gov.to.tce.model.adm.AdmAutenticacao;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;


@Repository
public class AdmAutenticacaoRepository extends DefaultRepository<AdmAutenticacao, BigInteger> {

    public AdmAutenticacaoRepository(EntityManager em) {
        super(em);
    }

    public UnidadeGestora buscaUnidadeGestoraPorCnpj(String Cnpj){
        List<UnidadeGestora> list = entityManager.createNativeQuery("select * from UnidadeGestora " +
                " where id = '" + Cnpj + "' ", UnidadeGestora.class).getResultList();
        return list.get(0);
    }

    public Boolean getStatusChave(String Cnpj, Integer Exercicio, Integer Remessa){

       return (Boolean) entityManager.createNativeQuery("select CAST(CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END AS BIT) from SICAPAP21.dbo.AdmAutenticacao  " +
               "where idunidadegestora= '"+Cnpj+"' " +
               "AND exercicio="+Exercicio+" " + " AND remessa="+Remessa+" " ).getSingleResult();
    }





    public Integer getQtdAssinaturas(String Cnpj, Integer Exercicio, Integer Remessa){

        var retorno =  entityManager.createNativeQuery("select COUNT(*) from SICAPAP21..AdmAssinatura ass " +
                "inner join SICAPAP21..Inforemessa inf ON ass.chave=inf.chave " +
                "where idunidadegestora= '"+Cnpj+"' " +
                "AND exercicio="+Exercicio+" " + " AND remessa="+Remessa+" " ).getResultList();
        return (Integer)retorno.get(0);
    }


    public PaginacaoUtil<AdmAutenticacao> buscaPaginadaChaves(Pageable pageable, String searchParams, Integer tipoParams) {

        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search= "";

        //monta pesquisa search
        if(searchParams.length() > 3){/// entra

            if(tipoParams==0){ //entra para tratar a string
                String arrayOfStrings[]  = searchParams.split("=");
                search = "" +arrayOfStrings[0] + " LIKE  '%"+arrayOfStrings[1]+"%' AND "  ;
            }
            else{//entra caso for um Integer
                search = "" + searchParams + " AND   " ;
            }
        }

        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<AdmAutenticacao> list = null;
        list = getEntityManager()
                .createNativeQuery("select * from AdmAutenticacao " +
                        "WHERE " +search+" idUnidadeGestora= '"+ user.getUser(super.request).getUnidadeGestora().getId()+ "' " +
                        "ORDER BY " + campo, AdmAutenticacao.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();

        long totalRegistros = countChaves();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;


        return new PaginacaoUtil<AdmAutenticacao>(tamanho, pagina, totalPaginas, totalRegistros, list);
    }

    public long countChaves() {
        return getEntityManager().createQuery("select count(*) from AdmAutenticacao WHERE idUnidadeGestora= '"+ user.getUser(super.request).getUnidadeGestora().getId()+ "'", Long.class).getSingleResult();
    }


}
