package com.example.sicapweb.repository.remessa;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.folha.documento.Gfip;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
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
    public List<Object[]> buscarDocumentoAnteriorGfip(String chave, String tipo) {
        return getEntityManager().createNativeQuery(
                "select l.id, l.idInfoRemessa, l.data, l.idCastorFile, l.tipo from (select doc.*, ROW_NUMBER() OVER (PARTITION BY idInfoRemessa\n" +
                        "                              ORDER BY data desc) as linha from SICAPAP21W.dbo.DocumentoGfip doc where doc.idInfoRemessa = '" + chave + "' and doc.tipo = '" + tipo + "') l where l.linha = 1 and " +
                        "l.idInfoRemessa = '" + chave + "' and l.tipo = '" + tipo + "'")
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
    public List<Gfip> buscarDocumentoByUgUsuario(){
        var query = getEntityManager().createNativeQuery(
                "select l.id,\n" +
                        "       l.idInfoRemessa,\n" +
                        "       l.data as datadocumento,\n" +
                        "       l.idCastorFile,\n" +
                        "       l.tipo,\n" +
                        "       info.chave,\n" +
                        "       info.data,\n" +
                        "       info.exercicio,\n" +
                        "       info.idUnidadeGestora,\n" +
                        "       info.nomeUnidade,\n" +
                        "       info.relatoria,\n" +
                        "       info.opcao,\n" +
                        "       info.remessa,\n" +
                        "       info.status " +

                        "from (select doc.*, ROW_NUMBER() OVER (PARTITION BY idInfoRemessa,tipo  ORDER BY data desc) as linha\n" +
                        "                            from SICAPAP21W.dbo.DocumentoGfip doc\n" +
                        "                            ) l\n" +
                        "join SICAPAP21W.dbo.InfoRemessa info on l.idInfoRemessa = info.chave\n" +
                        "join SICAPAP21W.dbo.UnidadeGestora UG on UG.id = info.idUnidadeGestora\n" +
                        "where l.linha = 1 and\n" +
                        "      Ug.id = :UG");

        var ug =redisConnect.getUser(request).getUnidadeGestora().getId().toString();
        query.setParameter("UG",ug);
        List<Object[]> resultList = query.getResultList();
        List<Gfip> retorno = new ArrayList<Gfip>();
        for (Object[] result :resultList) {
            InfoRemessa remesa = new InfoRemessa();
            remesa.setChaveEstatica((String) result[5]);
            remesa.setData(new Date(((Timestamp)result[6]).getTime()));
            remesa.setExercicio((Integer) result[7]);
            remesa.setIdUnidadeGestora((String) result[8]);
            remesa.setNomeUnidade((String) result[9]);
            remesa.setRelatoria((Integer) result[10]);
            remesa.setOpcao((Character) result[11]);
            remesa.setRemessa((Integer) result[12]);
            remesa.setStatus((Boolean) result[13]);
            var aux = new Gfip();
            aux.setId(((BigDecimal) result[0]).toBigInteger());
            aux.setInfoRemessa(remesa);
            aux.setData(new Date(((Timestamp)result[2]).getTime()));
            aux.setIdCastorFile((String) result[3]);
            aux.setTipo((String) result[4]);
            retorno.add(aux);
        }

        return retorno;
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
                        "where tipo in ('boletoGFIP', 'comprovanteGFIP') " +
                        "  and idInfoRemessa = '" + chave + "' " +
                        "group by tipo").getResultList();
    }



}
