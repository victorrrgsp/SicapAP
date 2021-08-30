package com.example.sicapweb.repository;

import br.gov.to.tce.model.ap.concessoes.DocumentoAposentadoria;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;

@Repository
public class DocumentoAposentadoriaRepository extends DefaultRepository<DocumentoAposentadoria, BigInteger> {
    public DocumentoAposentadoriaRepository(EntityManager em) {
        super(em);
    }
}
