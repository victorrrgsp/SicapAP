package com.example.sicapweb.repository;

import br.gov.to.tce.model.ap.concessoes.DocumentoPensao;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;

@Repository
public class DocumentoPensaoRepository extends DefaultRepository<DocumentoPensao, BigInteger> {
    public DocumentoPensaoRepository(EntityManager em) {
        super(em);
    }

    public List<DocumentoPensao> buscarDocumentoPensao(String coluna, BigInteger idPensao) {
        return getEntityManager().createNativeQuery(
                "select * from DocumentoPensao where inciso = '"
                        + coluna + "' and idPensao = " + idPensao, DocumentoPensao.class)
                .getResultList();
    }
}
