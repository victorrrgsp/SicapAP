package com.example.sicapweb.repository;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RemessaRepository extends DefaultRepository<Integer, Integer> {

    @PersistenceContext
    EntityManager entityManager;
    public List<Integer> findAllRemessasByExercicio(Integer exercicio) {
        return entityManager.createNativeQuery("select distinct numeroRemessa as remessa from Cadun..PeriodoRemessa  "+" where idSistema=29 AND exercicio="+exercicio+" ").getResultList();
    }

}
