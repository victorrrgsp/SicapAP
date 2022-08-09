package com.example.sicapweb.repository.concessao;

import br.gov.to.tce.application.ApplicationException;
import br.gov.to.tce.model.ap.concessoes.DocumentoReconducao;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;

@Repository
public class DocumentoReconducaoRepository extends DefaultRepository<DocumentoReconducao, BigInteger> {
    public DocumentoReconducaoRepository(EntityManager em) {
        super(em);
    }

    public List<DocumentoReconducao> buscarDocumentooReconducao(String coluna, BigInteger idReconducao) {
        return getEntityManager().createNativeQuery(
                "select * from DocumentoReconducao where inciso = '"
                        + coluna + "' and idReconducao = " + idReconducao, DocumentoReconducao.class)
                .getResultList();
    }

    public List<Object> buscarDocumentos(BigInteger idEnvio) {
        try {
            Query query = getEntityManager().createNativeQuery("" +
                    "select CONCAT(dp.inciso, '-', dp.descricao) as arquivo, dp.idCastorFile " +
                    "from AdmEnvio ae " +
                    "         join DocumentoReconducao dp on ae.idMovimentacao = dp.idReconducao " +
                    "where ae.id = :idEnvio");
            query.setParameter("idEnvio", idEnvio.intValue());
            List<Object> result = buscarSQL(query, "arquivo, idCastorFile");
            return result;
        } catch (ApplicationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
