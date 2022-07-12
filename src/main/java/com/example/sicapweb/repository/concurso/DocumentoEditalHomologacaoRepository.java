package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.ap.concurso.documento.DocumentoEdital;
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

    public List<DocumentoEditalHomologacao> buscarDocumentosEditalHomologacao(String coluna, BigInteger idEdital) {
        return  getEntityManager().createNativeQuery(
                        "select * from DocumentoEditalHomologacao h join EditalHomologacao EH on H.idEditalHomologacao= EH.id   where H.inciso in ("
                                + coluna + ") and EH.idEdital = " + idEdital, DocumentoEditalHomologacao.class)
                .getResultList();
    }

    public Integer findSituacao(String entidade, String pk ,BigInteger id, String incisos) {
        return (Integer) getEntityManager().createNativeQuery("select count(*) \n" +
                " Situacao from "+ entidade +
                " where "+ pk +" = "+ id +" and inciso in ("+ incisos + ") " ).getSingleResult();

    }
}
