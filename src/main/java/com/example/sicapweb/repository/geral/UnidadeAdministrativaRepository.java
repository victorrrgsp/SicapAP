package com.example.sicapweb.repository.geral;

import br.gov.to.tce.model.ap.relacional.UnidadeAdministrativa;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class UnidadeAdministrativaRepository extends DefaultRepository<UnidadeAdministrativa, BigInteger> {

    public UnidadeAdministrativaRepository(EntityManager em) {
        super(em);
    }

    public UnidadeAdministrativa buscarUnidadePorcodigo(String codigo) {
        List<UnidadeAdministrativa> list = getEntityManager()
                .createNativeQuery(" with ids_UnidadeAdministrativa as " +
                        "(select t.codigoUnidadeAdministrativa,i.idUnidadeGestora, max(t.id) max_id_por_chave from SICAPAP21.dbo.UnidadeAdministrativa t  join SICAPAP21.dbo.InfoRemessa  i on  t.chave = i.chave and i.idUnidadeGestora='"+User.getUser(super.request).getUnidadeGestora().getId()+"'  group by t.codigoUnidadeAdministrativa,i.idUnidadeGestora )" +
                        "select t.*  from SICAPAP21.dbo.UnidadeAdministrativa t  join SICAPAP21.dbo.InfoRemessa  i  on t.chave =  i.chave join ids_UnidadeAdministrativa ie on t.id = ie.max_id_por_chave and i.idUnidadeGestora=ie.idUnidadeGestora" +
                        " where t.codigoUnidadeAdministrativa = '" + codigo + "'  ", UnidadeAdministrativa.class)
                .getResultList();
        return list.get(0);
    }

    public UnidadeAdministrativa buscarUnidadePorCnpj(String cnpj) {
        List<UnidadeAdministrativa> list = getEntityManager()
                .createNativeQuery("select * from UnidadeAdministrativa ed" +
                        " where cnpfEnpresaOrganizadora = '" + cnpj + "'    ", UnidadeAdministrativa.class)
                .getResultList();
        return list.get(0);
    }

    public List<Object[]> buscarremessa(int ano, int mes) {
        List<Object[]> list = getEntityManager().createNativeQuery(
                " select 'TODOS' cnpj, " +
                        "'1 - TODOS' nomeentidade " +
                        " union all " +
                        " select qr.cnpj, pj.nomeentidade from " +
                        " (	select " +
                        " distinct idUnidadeGestora cnpj " +
                        " from SICAPAP21..vwFolhaPagamento f " +
                        " where f.exercicio = " + ano +
                        " and f.remessa = " + mes +
                        ")qr " +
                        "left join cadun..vwPessoaSimples pj on pj.CNPJ = rtrim(qr.cnpj) " +
                        "order by nomeentidade asc")
                .getResultList();
        return list;
    }

    public List<UnidadeAdministrativa> findbyUg() {
        List<UnidadeAdministrativa> list = getEntityManager().createNativeQuery(

                "  with ids_UnidadeAdministrativa as " +
                        "(select t.codigoUnidadeAdministrativa,i.idUnidadeGestora, max(t.id) max_id_por_chave from SICAPAP21.dbo.UnidadeAdministrativa t  join SICAPAP21.dbo.InfoRemessa  i on  t.chave = i.chave and i.idUnidadeGestora='"+User.getUser(super.request).getUnidadeGestora().getId()+"'  group by t.codigoUnidadeAdministrativa,i.idUnidadeGestora ) " +
                        "select t.*  from SICAPAP21.dbo.UnidadeAdministrativa t  join SICAPAP21.dbo.InfoRemessa  i  on t.chave =  i.chave join ids_UnidadeAdministrativa ie on t.id = ie.max_id_por_chave and i.idUnidadeGestora=ie.idUnidadeGestora ",
                UnidadeAdministrativa.class).getResultList();

        return list;

    }

    public List<HashMap<String, Object>> pesquisaPorUg(String ug) {
        List<Object[]> list = getEntityManager().createNativeQuery(
                "with uds as(select distinct\n" +
                        "\n" +
                        "    ug.cnpj as ugCnpj,\n" +
                        "    ud.cnpj as udCnpj,\n" +
                        "    ud.id,\n" +
                        "    ud.codigoUnidadeAdministrativa ,\n" +
                        "    ug.descricao as UnidadeGestora ,\n" +
                        "    ud.nome as unidadeAdministrativa,\n" +
                        "    RANK() OVER (PARTITION BY ud.codigoUnidadeAdministrativa ORDER BY ud.id desc ) as rank\n" +
                        "\n" +
                        "from SicapAP21.dbo.LOTACAO l\n" +
                        "    join UnidadeAdministrativa  ud on l.idUnidadeAdministrativa = ud.id\n" +
                        "\tjoin InfoRemessa info on l.chave = info.chave\n" +
                        "    join SicapAP.dbo.unidadegestora Ug on info.idUnidadeGestora = ug.cnpj\n" +
                        "where (ug.cnpj = :UnidadeGestora or :UnidadeGestora = 'todos' )\n" +
                        ")\n" +
                        "select * from uds where uds.rank = 1")
                .setParameter("UnidadeGestora", ug)
                .getResultList();
        List<HashMap<String, Object>> retorno = new ArrayList<HashMap<String, Object>>();

        list.forEach(envio -> {
            var aux = new HashMap<String, Object>();
            // aux.put("id", envio[0] );
            aux.put("ugCnpj"               , envio[0]);
            aux.put("udCnpj"               , envio[1]);
            aux.put("udId"                 , envio[2]);
            aux.put("codigoUnidadeAdministrativa"                 , envio[3]);
            aux.put("UnidadeGestora"       , envio[4]);
            aux.put("unidadeAdministrativa", envio[5]);

            retorno.add(aux);
        });

        return retorno;
    }

    public List<Object> pesquisaLotacoesPorUg(String ug) {
        List<Object> list = getEntityManager().createNativeQuery(
                "select distinct\n" +
                        "    l.nome as lotacao --,\n" +
                        "    --count(distinct  l.id),\n" +
                        "    --l.codigoLotacao,\n" +
                        "    -- ug.descricao as UnidadeGestora ,\n" +
                        "    -- UD.nome as unidadeAdministrativa\n" +
                        "from SicapAP21.dbo.LOTACAO l\n" +
                        "    join UnidadeAdministrativa  Ud on l.idUnidadeAdministrativa = Ud.id\n" +
                        "    join InfoRemessa info on l.chave = info.chave\n" +
                        "    join SicapAP.dbo.unidadegestora Ug on info.idUnidadeGestora = Ug.cnpj\n" +
                        "where (ug.cnpj = :UnidadeGestora or :UnidadeGestora = 'todos' )")
                .setParameter("UnidadeGestora", ug)
                .getResultList();
      

        return list;
    }

}
