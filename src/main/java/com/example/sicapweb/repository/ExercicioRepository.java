package com.example.sicapweb.repository;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ExercicioRepository extends DefaultRepository<Integer, Integer> {

    @PersistenceContext
    private EntityManager entityManager;

    public ExercicioRepository(EntityManager em) {
        super(em);
    }

    public List<Integer> findAll() {
        return entityManager.createNativeQuery("select distinct exercicio from Cadun..PeriodoRemessa "+" where idSistema=29 ").getResultList();
    }

}
