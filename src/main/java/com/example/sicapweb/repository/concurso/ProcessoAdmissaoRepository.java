package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.ap.concurso.ProcessoAdmissao;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;

@Repository
public class ProcessoAdmissaoRepository  extends DefaultRepository<ProcessoAdmissao, BigInteger>  {

    public ProcessoAdmissaoRepository(EntityManager em) {
        super(em);
    }




}
