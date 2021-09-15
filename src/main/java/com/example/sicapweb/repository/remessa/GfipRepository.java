package com.example.sicapweb.repository.remessa;

import br.gov.to.tce.model.ap.folha.documento.Gfip;
import com.example.sicapweb.repository.DefaultRepository;
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
                        "                              ORDER BY data desc) as linha from DocumentoGfip doc where doc.idInfoRemessa = '"+ chave +"' and doc.tipo = '"+tipo+"') l where l.linha = 1 and " +
                        "l.idInfoRemessa = '" + chave + "' and l.tipo = '"+ tipo +"'", Gfip.class)
                .getResultList();
    }

    public String findSituacao(String chave, String tipo) {
        return (String) getEntityManager().createNativeQuery("select case when (count(*) > 0) then 'Informado'\n" +
                "        else 'NaoInformado' end tipo \n" +
                " from documentoGfip doc"+
                " where idInfoRemessa = '"+ chave +"' and tipo = '"+ tipo + "'" ).getSingleResult();

    }
}
