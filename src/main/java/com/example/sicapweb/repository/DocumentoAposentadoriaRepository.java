package com.example.sicapweb.repository;

import br.gov.to.tce.model.ap.concessoes.DocumentoAposentadoria;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;

@Repository
public class DocumentoAposentadoriaRepository extends DefaultRepository<DocumentoAposentadoria, BigInteger> {
    public DocumentoAposentadoriaRepository(EntityManager em) {
        super(em);
    }

    public List<DocumentoAposentadoria> buscarDocumentoAposentadoria(String coluna, BigInteger idAposentadoria) {
        return getEntityManager().createNativeQuery(
                "select * from DocumentoAposentadoria where inciso = '"
                        + coluna + "' and idAposentadoria = " + idAposentadoria, DocumentoAposentadoria.class)
                .getResultList();
    }

    public Integer findSituacao(String entidade, String pk ,BigInteger id, String incisos, String reserva, String reforma, String reversao, String revisao) {
        return (Integer) getEntityManager().createNativeQuery("select count(*) \n" +
                " Situacao from "+ entidade +
                " where "+ pk +" = "+ id +" and inciso in ("+ incisos + ") and reserva = '"+ reserva +"' and reforma = '"+ reforma +"' and reversao = '"+ reversao +"' and revisao = '"+ revisao+"'" ).getSingleResult();

    }

    public List<DocumentoAposentadoria> buscarDocumentoAposentadoriaReserva(String coluna, BigInteger idAposentadoria) {
        return getEntityManager().createNativeQuery(
                "select * from DocumentoAposentadoria where reserva = 'S' and inciso = '"
                        + coluna + "' and idAposentadoria = " + idAposentadoria, DocumentoAposentadoria.class)
                .getResultList();
    }

    public List<DocumentoAposentadoria> buscarDocumentoAposentadoriaReforma(String coluna, BigInteger idAposentadoria) {
        return getEntityManager().createNativeQuery(
                "select * from DocumentoAposentadoria where reforma = 'S' and inciso = '"
                        + coluna + "' and idAposentadoria = " + idAposentadoria, DocumentoAposentadoria.class)
                .getResultList();
    }

    public List<DocumentoAposentadoria> buscarDocumentoAposentadoriaRevisao(String coluna, BigInteger idAposentadoria) {
        return getEntityManager().createNativeQuery(
                "select * from DocumentoAposentadoria where revisao = 'S' and inciso = '"
                        + coluna + "' and idAposentadoria = " + idAposentadoria, DocumentoAposentadoria.class)
                .getResultList();
    }

    public List<DocumentoAposentadoria> buscarDocumentoRevisaoReserva(String coluna, BigInteger idAposentadoria) {
        return getEntityManager().createNativeQuery(
                "select * from DocumentoAposentadoria where revisao = 'S' and reserva = 'S' and inciso = '"
                        + coluna + "' and idAposentadoria = " + idAposentadoria, DocumentoAposentadoria.class)
                .getResultList();
    }

    public List<DocumentoAposentadoria> buscarDocumentoRevisaoReforma(String coluna, BigInteger idAposentadoria) {
        return getEntityManager().createNativeQuery(
                "select * from DocumentoAposentadoria where revisao = 'S' and reforma = 'S' and inciso = '"
                        + coluna + "' and idAposentadoria = " + idAposentadoria, DocumentoAposentadoria.class)
                .getResultList();
    }

    public List<DocumentoAposentadoria> buscarDocumentoAposentadoriaReversao(String coluna, BigInteger idAposentadoria) {
        return getEntityManager().createNativeQuery(
                "select * from DocumentoAposentadoria where reversao = 'S' and inciso = '"
                        + coluna + "' and idAposentadoria = " + idAposentadoria, DocumentoAposentadoria.class)
                .getResultList();
    }
}
