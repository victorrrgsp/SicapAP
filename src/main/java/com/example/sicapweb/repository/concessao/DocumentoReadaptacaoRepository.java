package com.example.sicapweb.repository.concessao;

import br.gov.to.tce.model.ap.concessoes.DocumentoReadaptacao;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;

@Repository
public class DocumentoReadaptacaoRepository extends DefaultRepository<DocumentoReadaptacao, BigInteger> {
    public DocumentoReadaptacaoRepository(EntityManager em) {
        super(em);
    }

    public List<DocumentoReadaptacao> buscarDocumentoReadaptacao(String coluna, BigInteger idReadaptacao) {
        return getEntityManager().createNativeQuery(
                "select * from DocumentoReadaptacao where inciso = '"
                        + coluna + "' and idReadaptacao = " + idReadaptacao, DocumentoReadaptacao.class)
                .getResultList();
    }
}
