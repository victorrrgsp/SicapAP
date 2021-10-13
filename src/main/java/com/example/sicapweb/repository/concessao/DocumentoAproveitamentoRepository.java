package com.example.sicapweb.repository.concessao;

import br.gov.to.tce.model.ap.concessoes.DocumentoAproveitamento;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;

@Repository
public class DocumentoAproveitamentoRepository extends DefaultRepository<DocumentoAproveitamento, BigInteger> {
    public DocumentoAproveitamentoRepository(EntityManager em) {
        super(em);
    }

    public List<DocumentoAproveitamento> buscarDocumentoAproveitamento(String coluna, BigInteger idAproveitamento) {
        return getEntityManager().createNativeQuery(
                "select * from DocumentoAproveitamento where inciso = '"
                        + coluna + "' and idAproveitamento = " + idAproveitamento, DocumentoAproveitamento.class)
                .getResultList();
    }

    public Integer findSituacao(String entidade, String pk ,BigInteger id, String incisos) {
        return (Integer) getEntityManager().createNativeQuery("select count(*) \n" +
                " Situacao from "+ entidade +
                " where "+ pk +" = "+ id +" and inciso in ("+ incisos +")" ).getSingleResult();
    }
}
