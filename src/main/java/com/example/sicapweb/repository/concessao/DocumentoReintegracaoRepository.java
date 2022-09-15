package com.example.sicapweb.repository.concessao;

import br.gov.to.tce.application.ApplicationException;
import br.gov.to.tce.model.ap.concessoes.DocumentoReintegracao;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;

@Repository
public class DocumentoReintegracaoRepository extends DefaultRepository<DocumentoReintegracao, BigInteger> {
    public DocumentoReintegracaoRepository(EntityManager em) {
        super(em);
    }

    public DocumentoReintegracao buscarDocumentoReintegracao(String coluna, BigInteger idReintegracao) {
        return getEntityManager().createQuery(
                        "select o from DocumentoReintegracao o where o.inciso = '"
                                + coluna + "' and o.reintegracao.id = " + idReintegracao, DocumentoReintegracao.class)
                .setMaxResults(1).getSingleResult();
    }

    public List<Object> buscarDocumentos(BigInteger idEnvio) {
        try {
            Query query = getEntityManager().createNativeQuery("" +
                    "select CONCAT(dp.inciso, '-', dp.descricao) as arquivo, dp.idCastorFile " +
                    "from AdmEnvio ae " +
                    "         join DocumentoReintegracao dp on ae.idMovimentacao = dp.idReintegracao " +
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
