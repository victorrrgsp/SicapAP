package com.example.sicapweb.repository;

import br.gov.to.tce.model.ap.pessoal.Reintegracao;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;

@Repository
public class ReintegracaoRepository extends DefaultRepository<Reintegracao, BigInteger> {
    public ReintegracaoRepository(EntityManager em) {
        super(em);
    }
}
