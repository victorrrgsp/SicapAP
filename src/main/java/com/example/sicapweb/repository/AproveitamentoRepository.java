package com.example.sicapweb.repository;

import br.gov.to.tce.model.ap.pessoal.Aproveitamento;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;

@Repository
public class AproveitamentoRepository extends DefaultRepository<Aproveitamento, BigInteger> {
    public AproveitamentoRepository(EntityManager em) {
        super(em);
    }
}
