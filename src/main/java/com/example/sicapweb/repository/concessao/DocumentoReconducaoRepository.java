package com.example.sicapweb.repository.concessao;

import br.gov.to.tce.model.ap.concessoes.DocumentoReconducao;
import com.example.sicapweb.repository.DefaultRepository;
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
