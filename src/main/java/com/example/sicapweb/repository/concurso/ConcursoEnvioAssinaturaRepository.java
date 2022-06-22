package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.ap.concurso.ConcursoEnvio;
import br.gov.to.tce.model.ap.concurso.ConcursoEnvioAssinatura;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import java.math.BigInteger;

@Repository
public class ConcursoEnvioAssinaturaRepository  extends DefaultRepository<ConcursoEnvioAssinatura, BigInteger>  {

    public ConcursoEnvioAssinaturaRepository(EntityManager em) {
        super(em);
    }

}
