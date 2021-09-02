package com.example.sicapweb.repository;

import br.gov.to.tce.model.ap.concessoes.DocumentoAposentadoria;
import br.gov.to.tce.model.ap.relacional.Lei;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;

@Repository
public class LeiRepository extends DefaultRepository<Lei, BigInteger> {
    public LeiRepository(EntityManager em) {
        super(em);
    }

    public List<Lei> buscarDocumentoLei(BigInteger id) {
        return getEntityManager().createNativeQuery(
                        "select * from lei l where l.id = " + id, Lei.class)
                .getResultList();
    }
}
