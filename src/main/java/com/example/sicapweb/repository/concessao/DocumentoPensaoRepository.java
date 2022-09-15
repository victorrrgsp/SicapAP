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

    public DocumentoPensao buscarDocumentoPensao(String coluna, BigInteger idPensao) {
        return getEntityManager().createQuery(
                        "select o from DocumentoPensao o where o.revisao = 'N' and o.inciso = '"
                                + coluna + "' and o.pensao.id = " + idPensao, DocumentoPensao.class)
                .setMaxResults(1).getSingleResult();
    }

    public DocumentoPensao buscarDocumentoPensaoRevisao(String coluna, BigInteger idPensao) {
        return getEntityManager().createQuery(
                        "select o from DocumentoPensao o where o.revisao = 'S' and o.inciso = '"
                                + coluna + "' and o.pensao.id = " + idPensao, DocumentoPensao.class)
                .setMaxResults(1).getSingleResult();
    }

    public List<Object> buscarDocumentos(BigInteger idEnvio) {
        try {
            Query query = getEntityManager().createNativeQuery("" +
                    "select CONCAT(dp.inciso, '-', dp.descricao) as arquivo, dp.idCastorFile " +
                    "from AdmEnvio ae " +
                    "         join DocumentoPensao dp on ae.idMovimentacao = dp.idPensao " +
                    "where ae.id = :idEnvio");
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
                    "         join DocumentoPensao dp on ae.idMovimentacao = dp.idPensao " +
                    "where ae.id = :idEnvio and dp.revisao = 'S'");
            query.setParameter("idEnvio", idEnvio.intValue());
            List<Object> result = buscarSQL(query, "arquivo, idCastorFile");
            return result;
        } catch (ApplicationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
