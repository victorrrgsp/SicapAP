package com.example.sicapweb.repository;
import br.gov.to.tce.model.UnidadeGestora;
import br.gov.to.tce.model.adm.AdmAutenticacao;
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

       return (Boolean) entityManager.createNativeQuery("select status from AdmAutenticacao  " +
               "where idunidadegestora= '"+Cnpj+"' " +
               "AND exercicio="+Exercicio+"" +
               "AND remessa="+Remessa+" " ).getSingleResult();
    }



}
