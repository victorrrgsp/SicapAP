package com.example.sicapweb.repository;

import br.gov.to.tce.model.UnidadeGestora;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class UnidadeGestoraRepository extends DefaultRepository<UnidadeGestora, String> {
    @PersistenceContext
    EntityManager entityManager;
    public UnidadeGestora buscaUnidadeGestoraPorCnpj(String Cnpj) {

        List<UnidadeGestora> list = entityManager.createNativeQuery("select * from UnidadeGestora " +
                " where id = '" + Cnpj + "'    ", UnidadeGestora.class).getResultList();
        return list.get(0);


    }

}
