package com.example.sicapweb.repository.concessao;

import br.gov.to.tce.application.ApplicationException;
import br.gov.to.tce.model.ap.concessoes.DocumentoReadaptacao;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;

@Repository
public class DocumentoReadaptacaoRepository extends DefaultRepository<DocumentoReadaptacao, BigInteger> {
    public DocumentoReadaptacaoRepository(EntityManager em) {
        super(em);
    }


    public DocumentoReadaptacao buscarDocumentoReadaptacao(String coluna, BigInteger idReadaptacao) {
        return getEntityManager().createQuery(
                        "select o from DocumentoReadaptacao o where o.inciso = '"
                                + coluna + "' and o.readaptacao.id = " + idReadaptacao, DocumentoReadaptacao.class)
                .setMaxResults(1).getSingleResult();
    }

    public List<Object> buscarDocumentos(BigInteger idEnvio) {
        try {
            Query query = getEntityManager().createNativeQuery("" +
                    "select CONCAT(dp.inciso, '-', dp.descricao) as arquivo, dp.idCastorFile " +
                    "from AdmEnvio ae " +
                    "         join DocumentoReadaptacao dp on ae.idMovimentacao = dp.idReadaptacao " +
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
