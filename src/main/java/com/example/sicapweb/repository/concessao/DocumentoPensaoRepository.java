package com.example.sicapweb.repository.concessao;

import br.gov.to.tce.model.ap.concessoes.DocumentoPensao;
import com.example.sicapweb.repository.DefaultRepository;
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
                "select * from DocumentoPensao where revisao = 'N' and inciso = '"
                        + coluna + "' and idPensao = " + idPensao, DocumentoPensao.class)
                .getResultList();
    }

    public List<DocumentoPensao> buscarDocumentoPensaoRevisao(String coluna, BigInteger idPensao) {
        return getEntityManager().createNativeQuery(
                        "select * from DocumentoPensao where revisao = 'S' and inciso = '"
                                + coluna + "' and idPensao = " + idPensao, DocumentoPensao.class)
                .getResultList();
    }
}
