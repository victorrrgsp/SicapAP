package com.example.sicapweb.repository.remessa;

import br.gov.to.tce.model.UnidadeGestora;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RemessaRepository extends DefaultRepository<Integer, Integer> {

    @PersistenceContext
    EntityManager entityManager;

    public RemessaRepository(EntityManager em) {
        super(em);
    }

    public List<Integer> findAllRemessasByExercicio(Integer exercicio) {
        return entityManager.createNativeQuery("select distinct numeroRemessa as remessa from Cadun..PeriodoRemessa  "+" where idSistema=29 AND exercicio="+exercicio+" ").getResultList();
    }

}
