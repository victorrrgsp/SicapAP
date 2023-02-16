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
        if(coluna == null||coluna.replace(" ", "") == "" ){
            coluna = "";
        }else{
            coluna = " and inciso = '"+ coluna+"'"; 
        }
        return getEntityManager().createNativeQuery(
                "select * from DocumentoEditalHomologacao where status = 2 "
                        + coluna + " and idEditalHomologacao = " + idEditalHomologacao, DocumentoEditalHomologacao.class)
                .getResultList();
    }

    public List<DocumentoEditalHomologacao> buscarDocumentosEditalHomologacao(BigInteger idEdital) {
        return  getEntityManager().createNativeQuery(
                        "select * from DocumentoEditalHomologacao h join EditalHomologacao EH on H.idEditalHomologacao= EH.id   where h.status = 2 and EH.idEdital = " + idEdital, DocumentoEditalHomologacao.class)
                .getResultList();
    }

    public Integer findSituacao(String entidade, String pk ,BigInteger id, String incisos) {
        return (Integer) getEntityManager().createNativeQuery("select count(*) \n" +
                " Situacao from "+ entidade +
                " where status = 2  and "+ pk +" = "+ id +" and inciso in ("+ incisos + ") " ).getSingleResult();

    }

    public Integer findSituacaobyIdEdital(BigInteger id, String incisos) {
        return (Integer) getEntityManager().createNativeQuery(
                "select count(1) from DocumentoEditalHomologacao h " +
                        "join EditalHomologacao EH on H.idEditalHomologacao= EH.id  " +
                        " where h.status = 2 and   h.inciso in (" + incisos + ") and EH.idEdital = " + id).getSingleResult();

    }


    public Integer findAllInciso(String entidade, String pk, BigInteger id, String inciso) {
        return (Integer) getEntityManager().createNativeQuery("select count(*) from " + entidade +
                " where status = 2 and " + pk + " = " + id + " and inciso = '" + inciso + "'").getSingleResult();

    }

}
