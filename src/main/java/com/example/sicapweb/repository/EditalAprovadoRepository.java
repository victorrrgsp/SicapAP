package com.example.sicapweb.repository;

import br.gov.to.tce.model.ap.concurso.EditalAprovado;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;

@Repository
public class EditalAprovadoRepository extends DefaultRepository<EditalAprovado, BigInteger> {
    public EditalAprovadoRepository(EntityManager em) {
        super(em);
    }
}
