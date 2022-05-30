package com.example.sicapweb.repository.remessa;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.folha.documento.Gfip;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Repository
public class GfipRepository extends DefaultRepository<Gfip, BigInteger> {
    public GfipRepository(EntityManager em) {
        super(em);
    }

    public List<Gfip> buscarDocumentoGfip(String chave, String tipo) {
        return getEntityManager().createNativeQuery(
                "select l.id, l.idInfoRemessa, l.data, l.idCastorFile, l.tipo from (select doc.*, ROW_NUMBER() OVER (PARTITION BY idInfoRemessa\n" +
                        "                              ORDER BY data desc) as linha from DocumentoGfip doc where doc.idInfoRemessa = '" + chave + "' and doc.tipo = '" + tipo + "') l where l.linha = 1 and " +
                        "l.idInfoRemessa = '" + chave + "' and l.tipo = '" + tipo + "'", Gfip.class)
                .getResultList();
    }
    public List<Gfip> buscarDocumentoAllGfip(String UG){
        return getEntityManager().createNativeQuery(
                "select l.id,\n" +
                        "       l.idInfoRemessa,\n" +
                        "       l.data,\n" +
                        "       l.idCastorFile,\n" +
                        "       l.tipo,\n" +
                        "       info.*\n" +
                        "from (select doc.*, ROW_NUMBER() OVER (PARTITION BY idInfoRemessa,tipo  ORDER BY data desc) as linha\n" +
                        "                            from DocumentoGfip doc\n" +
                        "                            ) l\n" +
                        "join SICAPAP21.dbo.InfoRemessa info on l.idInfoRemessa = info.chave\n" +
                        "join SICAPAP21.dbo.UnidadeGestora UG on UG.id = info.idUnidadeGestora\n" +
                        "where l.linha = 1 and\n" +
                        "      Ug.id = '"+UG+"'"
                        //"and\n" +
                        //"      info.exercicio = "+ano+" and\n" +
                        //"      info.remessa = "+ mes
                        , Gfip.class)
                .getResultList();
    }

    public String findSituacao(String chave, String tipo) {
        return (String) getEntityManager().createNativeQuery("select case when (count(*) > 0) then 'Informado'\n" +
                "        else 'NaoInformado' end tipo \n" +
                " from documentoGfip doc" +
                " where idInfoRemessa = '" + chave + "' and tipo = '" + tipo + "'").getSingleResult();

    }

    public List<Integer> findDocumentos(String chave) {
        return getEntityManager().createNativeQuery(
                "select count(*) " +
                        "from SICAPAP21..DocumentoGfip " +
                        "where tipo in ('GFIP', 'boletoGFIP', 'comprovanteGFIP') " +
                        "  and idInfoRemessa = '" + chave + "' " +
                        "group by tipo").getResultList();
    }



}
