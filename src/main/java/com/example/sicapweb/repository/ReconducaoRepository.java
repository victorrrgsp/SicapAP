package com.example.sicapweb.repository;

import br.gov.to.tce.model.ap.pessoal.Reconducao;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;

@Repository
public class ReconducaoRepository extends DefaultRepository<Reconducao, BigInteger> {
    public ReconducaoRepository(EntityManager em) {
        super(em);
    }
}
