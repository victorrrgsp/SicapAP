package com.example.sicapweb.repository;

import br.gov.to.tce.model.ap.relacional.Lei;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;

@Repository
public class LeiRepository extends DefaultRepository<Lei, BigInteger> {
    public LeiRepository(EntityManager em) {
        super(em);
    }
}
