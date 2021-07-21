package com.example.sicapweb.repository;

import br.gov.to.tce.model.ap.concurso.EditalVaga;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public class EditalVagaRepository extends DefaultRepository<EditalVaga, BigInteger> {

    public List<EditalVaga> buscarVagas(Integer idEdital) {
        return getEntityManager().createNativeQuery(
                "select * from EditalVaga where idEdital = "
                        + idEdital, EditalVaga.class)
                .getResultList();
    }
}
