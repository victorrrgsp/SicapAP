package com.example.sicapweb.repository.concessao;

import br.gov.to.tce.application.ApplicationException;
import br.gov.to.tce.model.ap.concessoes.DocumentoAposentadoria;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;

@Repository
public class DocumentoAposentadoriaRepository extends DefaultRepository<DocumentoAposentadoria, BigInteger> {
    public DocumentoAposentadoriaRepository(EntityManager em) {
        super(em);
    }

    public DocumentoAposentadoria buscarDocumentoAposentadoria(String coluna, BigInteger idAposentadoria) {
        return getEntityManager().createQuery(
                        "select o from DocumentoAposentadoria o where o.inciso = '"
                                + coluna + "' and o.aposentadoria.id = " + idAposentadoria, DocumentoAposentadoria.class)
                .setMaxResults(1).getSingleResult();
    }

    public Integer findSituacao(String entidade, String pk, BigInteger id, String incisos, String reserva, String reforma, String reversao, String revisao) {
        return (Integer) getEntityManager().createNativeQuery("select count(*)  " +
                " Situacao from " + entidade +
                " where " + pk + " = " + id + " and inciso in (" + incisos + ") and reserva = '" + reserva + "' and reforma = '" + reforma + "' and reversao = '" + reversao + "' and revisao = '" + revisao + "'").getSingleResult();

    }

    public DocumentoAposentadoria buscarDocumentoAposentadoriaReserva(String coluna, BigInteger idAposentadoria) {
        return getEntityManager().createQuery(
                        "select o from DocumentoAposentadoria o where o.reserva = 'S' and o.inciso = '"
                                + coluna + "' and o.aposentadoria.id = " + idAposentadoria, DocumentoAposentadoria.class)
                .setMaxResults(1).getSingleResult();
    }

    public DocumentoAposentadoria buscarDocumentoAposentadoriaReforma(String coluna, BigInteger idAposentadoria) {
        return getEntityManager().createQuery(
                        "select o from DocumentoAposentadoria o where o.reserva = 'S' and o.inciso = '"
                                + coluna + "' and o.aposentadoria.id = " + idAposentadoria, DocumentoAposentadoria.class)
                .setMaxResults(1).getSingleResult();
    }

    public DocumentoAposentadoria buscarDocumentoAposentadoriaRevisao(String coluna, BigInteger idAposentadoria) {
        return getEntityManager().createQuery(
                        "select o from DocumentoAposentadoria o where o.reserva = 'S' and o.inciso = '"
                                + coluna + "' and o.aposentadoria.id = " + idAposentadoria, DocumentoAposentadoria.class)
                .setMaxResults(1).getSingleResult();
    }

    public DocumentoAposentadoria buscarDocumentoRevisaoReforma(String coluna, BigInteger idAposentadoria) {
        return getEntityManager().createQuery(
                        "select o from DocumentoAposentadoria o where o.revisao = 'S' and " +
                                "o.reforma = 'S' and o.inciso = '"
                                + coluna + "' and o.aposentadoria.id = " + idAposentadoria, DocumentoAposentadoria.class)
                .setMaxResults(1).getSingleResult();
    }

    public DocumentoAposentadoria buscarDocumentoRevisaoReserva(String coluna, BigInteger idAposentadoria) {
        return getEntityManager().createQuery(
                        "select o from DocumentoAposentadoria o where o.revisao = 'S' and o.reserva = 'S' and o.inciso = '"
                                + coluna + "' and o.aposentadoria.id = " + idAposentadoria, DocumentoAposentadoria.class)
                .setMaxResults(1).getSingleResult();
    }

    public DocumentoAposentadoria buscarDocumentoAposentadoriaReversao(String coluna, BigInteger idAposentadoria) {
        return getEntityManager().createQuery(
                    "select o from DocumentoAposentadoria o where o.reversao = 'S' and o.inciso = '"
                            + coluna + "' and o.aposentadoria.id = " + idAposentadoria, DocumentoAposentadoria.class)
            .setMaxResults(1).getSingleResult();
    }

    public List<Object> buscarDocumentos(BigInteger idEnvio) {
        try {
            Query query = getEntityManager().createNativeQuery("" +
                    "select CONCAT(dp.inciso, '-', dp.descricao) as arquivo, dp.idCastorFile " +
                    "from AdmEnvio ae " +
                    "         join DocumentoAposentadoria dp on ae.idMovimentacao = dp.idAposentadoria " +
                    "where ae.id = :idEnvio");
            query.setParameter("idEnvio", idEnvio.intValue());
            List<Object> result = buscarSQL(query, "arquivo, idCastorFile");
            return result;
        } catch (ApplicationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Object> buscarDocumentosReserva(BigInteger idEnvio) {
        try {
            Query query = getEntityManager().createNativeQuery("" +
                    "select CONCAT(dp.inciso, '-', dp.descricao) as arquivo, dp.idCastorFile " +
                    "from AdmEnvio ae " +
                    "         join DocumentoAposentadoria dp on ae.idMovimentacao = dp.idAposentadoria " +
                    "where ae.id = :idEnvio and dp.reserva = 'S'");
            query.setParameter("idEnvio", idEnvio.intValue());
            List<Object> result = buscarSQL(query, "arquivo, idCastorFile");
            return result;
        } catch (ApplicationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Object> buscarDocumentosReforma(BigInteger idEnvio) {
        try {
            Query query = getEntityManager().createNativeQuery("" +
                    "select CONCAT(dp.inciso, '-', dp.descricao) as arquivo, dp.idCastorFile " +
                    "from AdmEnvio ae " +
                    "         join DocumentoAposentadoria dp on ae.idMovimentacao = dp.idAposentadoria " +
                    "where ae.id = :idEnvio and dp.reforma = 'S'");
            query.setParameter("idEnvio", idEnvio.intValue());
            List<Object> result = buscarSQL(query, "arquivo, idCastorFile");
            return result;
        } catch (ApplicationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Object> buscarDocumentosRevisao(BigInteger idEnvio) {
        try {
            Query query = getEntityManager().createNativeQuery("" +
                    "select CONCAT(dp.inciso, '-', dp.descricao) as arquivo, dp.idCastorFile " +
                    "from AdmEnvio ae " +
                    "         join DocumentoAposentadoria dp on ae.idMovimentacao = dp.idAposentadoria " +
                    "where ae.id = :idEnvio and dp.revisao = 'S'");
            query.setParameter("idEnvio", idEnvio.intValue());
            List<Object> result = buscarSQL(query, "arquivo, idCastorFile");
            return result;
        } catch (ApplicationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Object> buscarDocumentosRevisaoReserva(BigInteger idEnvio) {
        try {
            Query query = getEntityManager().createNativeQuery("" +
                    "select CONCAT(dp.inciso, '-', dp.descricao) as arquivo, dp.idCastorFile " +
                    "from AdmEnvio ae " +
                    "         join DocumentoAposentadoria dp on ae.idMovimentacao = dp.idAposentadoria " +
                    "where ae.id = :idEnvio and dp.revisao = 'S' and dp.reserva = 'S'");
            query.setParameter("idEnvio", idEnvio.intValue());
            List<Object> result = buscarSQL(query, "arquivo, idCastorFile");
            return result;
        } catch (ApplicationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Object> buscarDocumentosRevisaoReforma(BigInteger idEnvio) {
        try {
            Query query = getEntityManager().createNativeQuery("" +
                    "select CONCAT(dp.inciso, '-', dp.descricao) as arquivo, dp.idCastorFile " +
                    "from AdmEnvio ae " +
                    "         join DocumentoAposentadoria dp on ae.idMovimentacao = dp.idAposentadoria " +
                    "where ae.id = :idEnvio and dp.revisao = 'S' and dp.reforma = 'S'");
            query.setParameter("idEnvio", idEnvio.intValue());
            List<Object> result = buscarSQL(query, "arquivo, idCastorFile");
            return result;
        } catch (ApplicationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Object> buscarDocumentosReversao(BigInteger idEnvio) {
        try {
            Query query = getEntityManager().createNativeQuery("" +
                    "select CONCAT(dp.inciso, '-', dp.descricao) as arquivo, dp.idCastorFile " +
                    "from AdmEnvio ae " +
                    "         join DocumentoAposentadoria dp on ae.idMovimentacao = dp.idAposentadoria " +
                    "where ae.id = :idEnvio and dp.reversao = 'S'");
            query.setParameter("idEnvio", idEnvio.intValue());
            List<Object> result = buscarSQL(query, "arquivo, idCastorFile");
            return result;
        } catch (ApplicationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
