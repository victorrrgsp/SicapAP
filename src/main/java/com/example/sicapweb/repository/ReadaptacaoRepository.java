package com.example.sicapweb.repository;

import br.gov.to.tce.model.ap.pessoal.Readaptacao;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;

@Repository
public class ReadaptacaoRepository extends DefaultRepository<Readaptacao, BigInteger> {
    public ReadaptacaoRepository(EntityManager em) {
        super(em);
    }
}
