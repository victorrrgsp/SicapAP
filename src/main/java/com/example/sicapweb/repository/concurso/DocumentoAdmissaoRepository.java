package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.ap.concurso.documento.DocumentoAdmissao;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;

@Repository
public class DocumentoAdmissaoRepository extends DefaultRepository<DocumentoAdmissao, BigInteger>  {

    public DocumentoAdmissaoRepository(EntityManager em) {
        super(em);
    }


}
