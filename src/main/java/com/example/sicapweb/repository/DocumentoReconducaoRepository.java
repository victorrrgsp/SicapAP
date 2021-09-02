package com.example.sicapweb.repository;

import br.gov.to.tce.model.ap.concessoes.DocumentoReconducao;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;

@Repository
public class DocumentoReconducaoRepository extends DefaultRepository<DocumentoReconducao, BigInteger> {
    public DocumentoReconducaoRepository(EntityManager em) {
        super(em);
    }

    public List<DocumentoReconducao> buscarDocumentooReconducao(String coluna, BigInteger idReconducao) {
        return getEntityManager().createNativeQuery(
                "select * from DocumentoReconducao where inciso = '"
                        + coluna + "' and idReconducao = " + idReconducao, DocumentoReconducao.class)
                .getResultList();
    }
}
