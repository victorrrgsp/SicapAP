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

    public List<DocumentoAposentadoria> buscarDocumentoAposentadoria(String coluna, BigInteger idAposentadoria) {
        return getEntityManager().createNativeQuery(
                        "select * from DocumentoAposentadoria where inciso = '"
                                + coluna + "' and idAposentadoria = " + idAposentadoria, DocumentoAposentadoria.class)
                .getResultList();
    }

    public Integer findSituacao(String entidade, String pk, BigInteger id, String incisos, String reserva, String reforma, String reversao, String revisao) {
        return (Integer) getEntityManager().createNativeQuery("select count(*)  " +
                " Situacao from " + entidade +
                " where " + pk + " = " + id + " and inciso in (" + incisos + ") and reserva = '" + reserva + "' and reforma = '" + reforma + "' and reversao = '" + reversao + "' and revisao = '" + revisao + "'").getSingleResult();

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

    public List<Object> buscarDocumentos(BigInteger idMovimentacao) {
        try {
            Query query = getEntityManager().createNativeQuery("" +
                    "select CONCAT(dp.inciso, '-', dp.descricao) as arquivo, dp.idCastorFile " +
                    "from SICAPAP21..AdmEnvio ae " +
                    "         join SICAPAP21..DocumentoAposentadoria dp on ae.idMovimentacao = dp.idAposentadoria " +
                    "where ae.id = :idMovimentacao");
            query.setParameter("idMovimentacao", idMovimentacao);
            List<Object> result = buscarSQL(query, "arquivo, idCastorFile");
            return result;
        } catch (ApplicationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Object> buscarDocumentosReserva(BigInteger idMovimentacao) {
        try {
            Query query = getEntityManager().createNativeQuery("" +
                    "select CONCAT(dp.inciso, '-', dp.descricao) as arquivo, dp.idCastorFile " +
                    "from SICAPAP21..AdmEnvio ae " +
                    "         join SICAPAP21..DocumentoAposentadoria dp on ae.idMovimentacao = dp.idAposentadoria " +
                    "where ae.id = :idMovimentacao and dp.reserva = 'S'");
            query.setParameter("idMovimentacao", idMovimentacao);
            List<Object> result = buscarSQL(query, "arquivo, idCastorFile");
            return result;
        } catch (ApplicationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Object> buscarDocumentosReforma(BigInteger idMovimentacao) {
        try {
            Query query = getEntityManager().createNativeQuery("" +
                    "select CONCAT(dp.inciso, '-', dp.descricao) as arquivo, dp.idCastorFile " +
                    "from SICAPAP21..AdmEnvio ae " +
                    "         join SICAPAP21..DocumentoAposentadoria dp on ae.idMovimentacao = dp.idAposentadoria " +
                    "where ae.id = :idMovimentacao and dp.reforma = 'S'");
            query.setParameter("idMovimentacao", idMovimentacao);
            List<Object> result = buscarSQL(query, "arquivo, idCastorFile");
            return result;
        } catch (ApplicationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Object> buscarDocumentosRevisao(BigInteger idMovimentacao) {
        try {
            Query query = getEntityManager().createNativeQuery("" +
                    "select CONCAT(dp.inciso, '-', dp.descricao) as arquivo, dp.idCastorFile " +
                    "from SICAPAP21..AdmEnvio ae " +
                    "         join SICAPAP21..DocumentoAposentadoria dp on ae.idMovimentacao = dp.idAposentadoria " +
                    "where ae.id = :idMovimentacao and dp.revisao = 'S'");
            query.setParameter("idMovimentacao", idMovimentacao);
            List<Object> result = buscarSQL(query, "arquivo, idCastorFile");
            return result;
        } catch (ApplicationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Object> buscarDocumentosRevisaoReserva(BigInteger idMovimentacao) {
        try {
            Query query = getEntityManager().createNativeQuery("" +
                    "select CONCAT(dp.inciso, '-', dp.descricao) as arquivo, dp.idCastorFile " +
                    "from SICAPAP21..AdmEnvio ae " +
                    "         join SICAPAP21..DocumentoAposentadoria dp on ae.idMovimentacao = dp.idAposentadoria " +
                    "where ae.id = :idMovimentacao and dp.revisao = 'S' and dp.reserva = 'S'");
            query.setParameter("idMovimentacao", idMovimentacao);
            List<Object> result = buscarSQL(query, "arquivo, idCastorFile");
            return result;
        } catch (ApplicationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Object> buscarDocumentosRevisaoReforma(BigInteger idMovimentacao) {
        try {
            Query query = getEntityManager().createNativeQuery("" +
                    "select CONCAT(dp.inciso, '-', dp.descricao) as arquivo, dp.idCastorFile " +
                    "from SICAPAP21..AdmEnvio ae " +
                    "         join SICAPAP21..DocumentoAposentadoria dp on ae.idMovimentacao = dp.idAposentadoria " +
                    "where ae.id = :idMovimentacao and dp.revisao = 'S' and dp.reforma = 'S'");
            query.setParameter("idMovimentacao", idMovimentacao);
            List<Object> result = buscarSQL(query, "arquivo, idCastorFile");
            return result;
        } catch (ApplicationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Object> buscarDocumentosReversao(BigInteger idMovimentacao) {
        try {
            Query query = getEntityManager().createNativeQuery("" +
                    "select CONCAT(dp.inciso, '-', dp.descricao) as arquivo, dp.idCastorFile " +
                    "from SICAPAP21..AdmEnvio ae " +
                    "         join SICAPAP21..DocumentoAposentadoria dp on ae.idMovimentacao = dp.idAposentadoria " +
                    "where ae.id = :idMovimentacao and dp.reversao = 'S'");
            query.setParameter("idMovimentacao", idMovimentacao);
            List<Object> result = buscarSQL(query, "arquivo, idCastorFile");
            return result;
        } catch (ApplicationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
