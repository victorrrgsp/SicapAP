package com.example.sicapweb.repository;

import br.gov.to.tce.model.ap.concessoes.DocumentoAproveitamento;
import br.gov.to.tce.model.ap.folha.documento.Gfip;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;

@Repository
public class GfipRepository extends DefaultRepository<Gfip, BigInteger> {
    public GfipRepository(EntityManager em) {
        super(em);
    }

    public List<Gfip> buscarDocumentoGfip(String chave, String tipo) {
        return getEntityManager().createNativeQuery(
                "select l.id, l.idInfoRemessa, l.data, l.idCastorFile, l.tipo from (select doc.*, ROW_NUMBER() OVER (PARTITION BY idInfoRemessa\n" +
                        "                              ORDER BY data desc) as linha from DocumentoGfip doc) l where l.linha = 1 and " +
                        "l.idInfoRemessa = '" + chave + "' and l.tipo = '"+ tipo +"'", Gfip.class)
                .getResultList();
    }
}
