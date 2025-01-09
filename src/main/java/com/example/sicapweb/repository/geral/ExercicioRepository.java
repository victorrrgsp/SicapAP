package com.example.sicapweb.repository.geral;

import com.example.sicapweb.repository.DefaultRepository;
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
        return entityManager.createNativeQuery("select distinct exercicio from Cadun..PeriodoRemessa " + " where idSistema=29 and exercicio >= 2020 order by exercicio desc ").getResultList();
    }

    public List<Integer> findExercicio() {
        return entityManager.createNativeQuery("select distinct exercicio from Cadun..PeriodoRemessa " + " where idSistema=29 order by exercicio desc ").getResultList();
    }

    public List<Integer> buscarExercicioVigente() {
        return entityManager.createNativeQuery("select top 1 exercicio from Cadun..PeriodoRemessa pr where idSistema = 29 ORDER BY id DESC").getResultList();
    }
}
