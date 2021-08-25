package com.example.sicapweb.repository;

import br.gov.to.tce.model.ap.relacional.UnidadeAdministrativa;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;

@Repository
public class UnidadeAdministrativaRepository extends DefaultRepository<UnidadeAdministrativa, BigInteger> {

    public UnidadeAdministrativaRepository(EntityManager em) {
        super(em);
    }

    public UnidadeAdministrativa buscarUnidadePorcodigo(String codigo) {
        List<UnidadeAdministrativa> list = getEntityManager().createNativeQuery("select * from UnidadeAdministrativa ed" +
                " where codigoUnidadeAdministrativa = '" + codigo + "'    ", UnidadeAdministrativa.class).getResultList();
        return list.get(0);
    }
}
