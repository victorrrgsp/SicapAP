package com.example.sicapweb.repository;

import br.gov.to.tce.model.ap.relacional.Ato;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;

@Repository
public class AtoRepository extends DefaultRepository<Ato, BigInteger> {
    public AtoRepository(EntityManager em) {
        super(em);
    }
}
