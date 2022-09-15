package com.example.sicapweb.repository.concessao;

import br.gov.to.tce.application.ApplicationException;
import br.gov.to.tce.model.ap.concessoes.DocumentoAproveitamento;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;

@Repository
public class DocumentoAproveitamentoRepository extends DefaultRepository<DocumentoAproveitamento, BigInteger> {
    public DocumentoAproveitamentoRepository(EntityManager em) {
        super(em);
    }

    public DocumentoAproveitamento buscarDocumentoAproveitamento(String coluna, BigInteger idAproveitamento) {
        return getEntityManager().createQuery(
                "select o from DocumentoAproveitamento o where o.inciso = '"
                        + coluna + "' and o.aproveitamento.id = " + idAproveitamento, DocumentoAproveitamento.class)
                .setMaxResults(1).getSingleResult();
    }

    public Integer findSituacao(String entidade, String pk ,BigInteger id, String incisos) {
        return (Integer) getEntityManager().createNativeQuery("select count(*) \n" +
                " Situacao from "+ entidade +
                " where "+ pk +" = "+ id +" and inciso in ("+ incisos +")" ).getSingleResult();
    }

    public List<Object> buscarDocumentos(BigInteger idEnvio) {
        try {
            Query query = getEntityManager().createNativeQuery("" +
                    "select CONCAT(dp.inciso, '-', dp.descricao) as arquivo, dp.idCastorFile " +
                    "from AdmEnvio ae " +
                    "         join DocumentoAproveitamento dp on ae.idMovimentacao = dp.idAproveitamento " +
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
