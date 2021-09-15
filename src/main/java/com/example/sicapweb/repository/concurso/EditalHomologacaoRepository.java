package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.ap.concurso.EditalHomologacao;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;

@Repository
public class EditalHomologacaoRepository extends DefaultRepository<EditalHomologacao, BigInteger> {
    public EditalHomologacaoRepository(EntityManager em) {
        super(em);
    }
}
