package com.example.sicapweb.repository;

import br.gov.to.tce.model.ap.concessoes.DocumentoReintegracao;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;

@Repository
public class DocumentoReintegracaoRepository extends DefaultRepository<DocumentoReintegracao, BigInteger> {
    public DocumentoReintegracaoRepository(EntityManager em) {
        super(em);
    }

    public List<DocumentoReintegracao> buscarDocumentoReintegracao(String coluna, BigInteger idReintegracao) {
        return getEntityManager().createNativeQuery(
                "select * from DocumentoReintegracao where inciso = '"
                        + coluna + "' and idReintegracao = " + idReintegracao, DocumentoReintegracao.class)
                .getResultList();
    }
}
