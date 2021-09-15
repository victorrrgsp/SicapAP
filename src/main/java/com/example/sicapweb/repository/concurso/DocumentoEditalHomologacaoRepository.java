package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.ap.concurso.documento.DocumentoEditalHomologacao;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;

@Repository
public class DocumentoEditalHomologacaoRepository extends DefaultRepository<DocumentoEditalHomologacao, BigInteger> {
    public DocumentoEditalHomologacaoRepository(EntityManager em) {
        super(em);
    }

    public List<DocumentoEditalHomologacao> buscarDocumentoEditalHomologacao(String coluna, BigInteger idEditalHomologacao) {
        return getEntityManager().createNativeQuery(
                "select * from DocumentoEditalHomologacao where inciso = '"
                        + coluna + "' and idEditalHomologacao = " + idEditalHomologacao, DocumentoEditalHomologacao.class)
                .getResultList();
    }
}
