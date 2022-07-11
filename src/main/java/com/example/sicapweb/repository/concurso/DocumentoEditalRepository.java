package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.ap.concessoes.DocumentoAposentadoria;
import br.gov.to.tce.model.ap.concurso.documento.DocumentoEdital;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;

@Repository
public class DocumentoEditalRepository extends DefaultRepository<DocumentoEdital, BigInteger> {
    public DocumentoEditalRepository(EntityManager em) {
        super(em);
    }

    public List<DocumentoEdital> buscarDocumentoEdital(String coluna, BigInteger idEdital) {
        return  getEntityManager().createNativeQuery(
                "select * from DocumentoEdital where inciso = '"
                        + coluna + "' and idEdital = " + idEdital, DocumentoEdital.class)
                .getResultList();
    }

    public List<DocumentoEdital> buscarDocumentosEdital(String coluna, BigInteger idEdital) {
        return  getEntityManager().createNativeQuery(
                        "select * from DocumentoEdital where inciso in ("
                                + coluna + ") and idEdital = " + idEdital, DocumentoEdital.class)
                .getResultList();
    }

    public Integer findSituacao(String entidade, String pk ,BigInteger id, String incisos) {
        return (Integer) getEntityManager().createNativeQuery("select count(*) \n" +
                " Situacao from "+ entidade +
                " where "+ pk +" = "+ id +" and inciso in ("+ incisos + ") " ).getSingleResult();

    }



}
