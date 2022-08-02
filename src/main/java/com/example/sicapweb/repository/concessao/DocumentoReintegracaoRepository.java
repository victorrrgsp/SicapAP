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

    public List<DocumentoReintegracao> buscarDocumentoReintegracao(String coluna, BigInteger idReintegracao) {
        return getEntityManager().createNativeQuery(
                "select * from DocumentoReintegracao where inciso = '"
                        + coluna + "' and idReintegracao = " + idReintegracao, DocumentoReintegracao.class)
                .getResultList();
    }

    public List<Object> buscarDocumentos(BigInteger idMovimentacao) {
        try {
            Query query = getEntityManager().createNativeQuery("" +
                    "select CONCAT(dp.inciso, '-', dp.descricao) as arquivo, dp.idCastorFile " +
                    "from SICAPAP21..AdmEnvio ae " +
                    "         join SICAPAP21..DocumentoReintegracao dp on ae.idMovimentacao = dp.idReintegracao " +
                    "where ae.id = :idMovimentacao");
            query.setParameter("idMovimentacao", idMovimentacao);
            List<Object> result = buscarSQL(query, "arquivo, idCastorFile");
            return result;
        } catch (ApplicationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
