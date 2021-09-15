package com.example.sicapweb.repository.geral;

import br.gov.to.tce.model.ap.relacional.Ato;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;

@Repository
public class AtoRepository extends DefaultRepository<Ato, BigInteger> {
    public AtoRepository(EntityManager em) {
        super(em);
    }
}
