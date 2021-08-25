package com.example.sicapweb.repository;

import br.gov.to.tce.model.CastorFile;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;

@Repository
public class CastorFileRepository extends DefaultRepository<CastorFile, BigInteger> {

    public CastorFileRepository(EntityManager em) {
        super(em);
    }
}
