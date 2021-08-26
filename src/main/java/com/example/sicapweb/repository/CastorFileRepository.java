package com.example.sicapweb.repository;

import br.gov.to.tce.model.CastorFile;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class CastorFileRepository extends DefaultRepository<CastorFile, String> {

    public CastorFileRepository(EntityManager em) {
        super(em);
    }
}
