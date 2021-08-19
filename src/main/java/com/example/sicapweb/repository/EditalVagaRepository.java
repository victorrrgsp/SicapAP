package com.example.sicapweb.repository;

import br.gov.to.tce.model.ap.concurso.EditalVaga;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public class EditalVagaRepository extends DefaultRepository<EditalVaga, BigInteger> {

    public List<EditalVaga> buscarVagasPorEdital(Integer idEdital) {
        return getEntityManager().createNativeQuery(
                "select * from EditalVaga where idEdital = "
                        + idEdital, EditalVaga.class)
                .getResultList();
    }

    public EditalVaga buscarVagasPorCodigo(String codigo) {
        List<EditalVaga> list = getEntityManager().createNativeQuery("select * from EditalVaga ed" +
                " where codigoVaga = '" + codigo + "'    ", EditalVaga.class).getResultList();
        return list.get(0);
    }
}
