package com.example.sicapweb.repository.concessao;

import br.gov.to.tce.application.ApplicationException;
import br.gov.to.tce.model.ap.concessoes.DocumentoPensao;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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

    public List<Object> buscarDocumentos(BigInteger idMovimentacao) {
        try {
            Query query = getEntityManager().createNativeQuery("" +
                    "select CONCAT(dp.inciso, '-', dp.descricao) as arquivo, dp.idCastorFile " +
                    "from SICAPAP21..AdmEnvio ae " +
                    "         join SICAPAP21..DocumentoPensao dp on ae.idMovimentacao = dp.idPensao " +
                    "where ae.id = :idMovimentacao");
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
                    "         join SICAPAP21..DocumentoPensao dp on ae.idMovimentacao = dp.idPensao " +
                    "where ae.id = :idMovimentacao and dp.revisao = 'S'");
            query.setParameter("idMovimentacao", idMovimentacao);
            List<Object> result = buscarSQL(query, "arquivo, idCastorFile");
            return result;
        } catch (ApplicationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
