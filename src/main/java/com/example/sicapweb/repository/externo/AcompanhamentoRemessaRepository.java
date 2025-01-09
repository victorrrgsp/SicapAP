package com.example.sicapweb.repository.externo;

import br.gov.to.tce.model.InfoRemessa;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AcompanhamentoRemessaRepository extends DefaultRepository<String, String> {
    @PersistenceContext
    private EntityManager entityManager;

    public AcompanhamentoRemessaRepository(EntityManager em) {
        super(em);
    }

    public AcompanhamentoRemessaRepository() {
    }

    public Object buscarResponsavelAssinatura(Integer tipoCargo, InfoRemessa infoRemessa) {
        try {
            Query query = entityManager.createNativeQuery(
                    "select  nome, cpf, max(status) status, max(data) data, CodigoCargo " +
                            "from (" +
                            "         select distinct pf.nome, pf.cpf, null status, null data, upc.CodigoCargo" +
                            "         from cadun.dbo.UnidadePessoaCargo upc" +
                            "                  join cadun.dbo.vwpessoa pf on pf.id = upc.CodigoPessoaFisica" +
                            "                  join AutenticacaoAssinatura..UsuarioAplicacao ua on ua.Usuario = pf.cpf" +
                            "                  join Cadun.dbo.PessoaJuridica pj on upc.CodigoPessoaJuridica = pj.Codigo" +
                            "         where upc.CodigoCargo in (:tipo)" +
                            "           and (:date is null or (dataInicio <= :date AND (datafim IS NULL OR YEAR(datafim) * 100 +MONTH(datafim) >= YEAR(:date) * 100 +MONTH(:date))))" +
                            "           and pj.CNPJ = :unidade" +
                            "           and ua.Aplicacao = 29" +
                            "         union" +
                            "         select distinct pf.nome, pf.cpf, b.DataAssinatura, b.DataAssinatura, upc.CodigoCargo" +
                            "         from cadun.dbo.UnidadePessoaCargo upc" +
                            "                  join cadun.dbo.vwpessoa pf on pf.id = upc.CodigoPessoaFisica" +
                            "                  join AutenticacaoAssinatura..UsuarioAplicacao ua on ua.Usuario = pf.cpf" +
                            "                  join AutenticacaoAssinatura..Assinatura b on ua.Usuario = b.Usuario" +
                            "                  join AutenticacaoAssinatura..InfoAssinatura c on c.Assinatura = b.OID and ua.Aplicacao = c.Aplicacao" +
                            "                  join Cadun.dbo.PessoaJuridica pj on upc.CodigoPessoaJuridica = pj.Codigo" +
                            "                  join SICAPAP21..AdmAssinatura ad on ad.idAssinatura = b.OID " +
                            "                  join SICAPAP21..InfoRemessa i on i.chave = ad.chave" +
                            "         where upc.CodigoCargo in (:tipo)" +
                            "           and (:date is null or (dataInicio <= :date AND (datafim IS NULL OR YEAR(datafim) * 100 +MONTH(datafim) >= YEAR(:date) * 100 +MONTH(:date))))" +
                            "           and i.idUnidadeGestora = :unidade" +
                            "           and pj.CNPJ  = :unidade" +
                            "           and c.Exercicio = :exercicio" +
                            "           and c.Bimestre = :remessa" +
                            "           and ua.Aplicacao = 29" +
                            "     ) as v " +
                            "group by nome, cpf, CodigoCargo; ");
            query.setParameter("tipo", tipoCargo);
            query.setParameter("date", infoRemessa.getData(), TemporalType.DATE);
            query.setParameter("unidade", infoRemessa.getIdUnidadeGestora());
            query.setParameter("exercicio", infoRemessa.getExercicio());
            query.setParameter("remessa", infoRemessa.getRemessa());
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }


    public List<Map<String, Object>> filtroAcompanhamentoRemessa(Integer exercicio, Integer remessa) {


        List<Map<String, Object>> retorno = new ArrayList<Map<String, Object>>();
        try {
            List<Object[]> list = getEntityManager().createNativeQuery("with vigentes as (\n" +
                    "\t\n" +
                    "\tSELECT DISTINCT pj.CNPJ\n" +
                    "                        FROM Cadun.dbo.VigenciaUnidadeGestora AS vug INNER JOIN\n" +
                    "                            Cadun.dbo.vwUnidadeGestora AS pj ON vug.CodigoPessoaJuridica = pj.idPessoa\n" +
                    "\t  WHERE ( (vug.exercicioini = " + exercicio + ") AND (vug.remessaini <= " + remessa + ") AND (vug.exerciciofim IS NULL) AND (vug.idSistema = 29) OR\n" +
                    "                        (vug.exercicioini = " + exercicio + ") AND (vug.remessaini <= " + remessa + ") AND (vug.exerciciofim = " + exercicio + ") AND (vug.idSistema = 29) AND (vug.remessafim >= " + remessa + ") OR\n" +
                    "                        (vug.exercicioini = " + exercicio + ") AND (vug.remessaini <= " + remessa + ") AND (vug.exerciciofim > " + exercicio + ") AND (vug.idSistema = 29) OR\n" +
                    "                        (vug.exercicioini < " + exercicio + ") AND (vug.exerciciofim IS NULL) AND (vug.idSistema = 29) OR\n" +
                    "                        (vug.exercicioini < " + exercicio + ") AND (vug.exerciciofim = " + exercicio + ") AND (vug.idSistema = 29) AND (vug.remessafim >= " + remessa + ") OR\n" +
                    "                        (vug.exercicioini < " + exercicio + ") AND (vug.exerciciofim > " + exercicio + ") AND (vug.idSistema = 29))\n" +
                    "),\n" +
                    "\n" +
                    "ugsAptas as (\n" +
                    "\tselect a.*\n" +
                    "    from cadun.dbo.vwUnidadeGestora a join \n" +
                    "\t\tvigentes v on v.CNPJ = a.cnpj \n" +
                    ")," +
                    " ugsAssinantes as (" +
                    " SELECT COUNT(DISTINCT idCargo) AS contAssinaturas, IDUNIDADEGESTORA , i.EXERCICIO, i.remessa, i.chave, max(i.relatoria) relatoria, max(f.dataEnvio) dataEntrega, max(dataAssinatura) dataAssinatura" +
                    " FROM ugsAptas ug left join" +
                    " inforemessa i on i.IDUNIDADEGESTORA = ug.CNPJ and i.EXERCICIO = " + exercicio + " and i.REMESSA = " + remessa + " and i.status = 1 left join" +
                    " admassinatura  a on a.chave = i.CHAVE  left join" +
                    " AutenticacaoAssinatura..Assinatura  ass on ass.oid = a.idAssinatura left join" +
                    " admfilarecebimento f on f.id = i.idfilarecebimento" +
                    " group by IDUNIDADEGESTORA, i.EXERCICIO, i.REMESSA, i.CHAVE" +
                    ")" +
                    " select  cnpj, NomeMunicipio + ' - '+ nomeEntidade nomeEntidade, case when b.relatoria is null then a.numeroRelatoria else b.relatoria end relatoria," +
                    " dataEntrega, dataAssinatura, contAssinaturas, case when  contAssinaturas >= 3 then 1 else 0 end mostraRecibo, exercicio, remessa, chave" +
                    " from ugsAptas a left join" +
                    " ugsAssinantes b on a.cnpj = idUnidadeGestora" +
                    " where CNPJ <> '00000000000000'" +
                    " order by NomeMunicipio, nomeEntidade, remessa desc").getResultList();

            //list.stream().forEach((record) -> {

            for (Object[] obj : list) {

                Map<String, Object> mapa = new HashMap<String, Object>();

                mapa.put("cnpj", (String) obj[0]);
                mapa.put("nomeEntidade", (String) obj[1]);
                mapa.put("relatoria", (Integer) obj[2]);
                mapa.put("dataEntrega", (String) obj[3]);
                mapa.put("dataAssinatura", (String) obj[4]);
                mapa.put("contAssinaturas", (Integer) obj[5]);
                mapa.put("mostraRecibo", (Integer) obj[6]);
                mapa.put("exercicio", (Integer) obj[7]);
                mapa.put("remessa"  , (Integer) obj[8]);
                mapa.put("chave"    , (String) obj[9]);
                retorno.add(mapa);
            }
            return retorno;
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> buscarTodosAcompanhamentoRemessa() {

        List<Map<String, Object>> retorno = new ArrayList<Map<String, Object>>();

        try {

            List<Object[]> list = entityManager.createNativeQuery(
                    "with ugsAptas as ( " +
                            " SELECT  COUNT(DISTINCT idCargo) AS contAssinaturas, IDUNIDADEGESTORA , i.EXERCICIO, i.remessa, i.chave, max(i.relatoria) relatoria, max(f.dataEnvio) dataEntrega, max(dataAssinatura) dataAssinatura" +
                            " FROM inforemessa i left join " +
                            " admassinatura  a on a.chave = i.CHAVE  left join " +
                            " AutenticacaoAssinatura..Assinatura  ass on ass.oid = a.idAssinatura left join " +
                            " admfilarecebimento f on f.id = i.idfilarecebimento " +
                            "group by IDUNIDADEGESTORA, i.EXERCICIO, i.REMESSA,  i.CHAVE " +
                            ") " +
                            "select IDUNIDADEGESTORA, c.nomeEntidade , case when b.relatoria is null then c.numeroRelatoria else b.relatoria end relatoria, " +
                            "dataEntrega, dataAssinatura, contAssinaturas, case when  contAssinaturas >= 3 then 1 else 0 end mostraRecibo, exercicio, remessa, chave " +
                            "from" +
                            " ugsAptas b inner join  cadun.dbo.vwUnidadeGestora c on IDUNIDADEGESTORA = c.cnpj " +
                            " where CNPJ <> '00000000000000' " +
                            "order by nomeEntidade asc, exercicio desc, remessa desc;").getResultList();

            //list.stream().forEach((record) -> {

            for (Object[] obj : list) {

                Map<String, Object> mapa = new HashMap<String, Object>();

                mapa.put("cnpj", (String) obj[0]);
                mapa.put("nomeEntidade", (String) obj[1]);
                mapa.put("relatoria", (Integer) obj[2]);
                mapa.put("dataEntrega", (String) obj[3]);
                mapa.put("dataAssinatura", (String) obj[4]);
                mapa.put("contAssinaturas", (Integer) obj[5]);
                mapa.put("mostraRecibo", (Integer) obj[6]);
                mapa.put("exercicio", (Integer) obj[7]);
                mapa.put("remessa", (Integer) obj[8]);
                mapa.put("chave", (String) obj[9]);
                retorno.add(mapa);

            }

            return retorno;

        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> buscarExercicioAcompanhamentoRemessa(Integer exercicio, Integer remessa) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List<Map<String, Object>> retorno = new ArrayList<>();

        try {
            if (remessa == 0) {
                remessa = null;
            }
            if(exercicio == 0){
                exercicio = null;
            }

            var query = entityManager.createNativeQuery(
                    " with ugsAptas as (\r\n" + //
                            "    SELECT  COUNT(DISTINCT idCargo) AS contAssinaturas,\r\n" + //
                            "           COUNT(distinct idCastorFile) as situacaoGFIP,\r\n" + //
                            "           IDUNIDADEGESTORA,\r\n" + //
                            "           ir.EXERCICIO,\r\n" + //
                            "           ir.remessa,\r\n" + //
                            "           ir.chave,\r\n" + //
                            "           max(ir.relatoria) relatoria,\r\n" + //
                            "           max(f.dataEnvio) dataEntrega,\r\n" + //
                            "           max(ass.dataAssinatura) dataAssinatura\r\n" + //
                            "    FROM inforemessa ir\r\n" + //
                            "        left join  SICAPAP21.dbo.DocumentoGfip dg on ir.CHAVE = dg.idInfoRemessa\r\n" + //
                            "        left join  admassinatura  a on a.chave = ir.CHAVE\r\n" + //
                            "        left join  UnidadeGestora u on ir.idUnidadeGestora = u.id\r\n" + //
                            "        left join  AutenticacaoAssinatura.dbo.Assinatura  ass on ass.oid = a.idAssinatura\r\n" + //
                            "        left join  admfilarecebimento f on f.id = ir.idfilarecebimento\r\n" + //
                             "    where  ir.exercicio = :exercicio and (:remessa is null or ir.REMESSA = :remessa)\r\n" + //
                             "    group by IDUNIDADEGESTORA, ir.EXERCICIO, ir.REMESSA,  ir.CHAVE )\r\n" + //
                             "select IDUNIDADEGESTORA,\r\n" + //
                             "       u.nome as nomeEntidade,\r\n" + //
                             "       case\r\n" + //
                             "           when b.relatoria is null then c.numeroRelatoria\r\n" + //
                             "           else b.relatoria\r\n" + //
                             "           end relatoria,\r\n" + //
                             "       dataEntrega,\r\n" + //
                             "       dataAssinatura,\r\n" + //
                             "       contAssinaturas,\r\n" + //
//                             "       situacaoGFIP,\r\n" + //
                             "       case\r\n" + //
                             "           when situacaoGFIP <= 1 then 'Pendente'\r\n" + //
                             "           else 'OK'\r\n" + //
                             "           end situacaoGFIP,\r\n" + //
                             "       case\r\n" + //
                             "           when  contAssinaturas >= 3 then 1\r\n" + //
                             "           else 0\r\n" + //
                             "           end mostraRecibo,\r\n" + //
//                             "       case\r\n" + //
//                             "           when  contAssinaturas > 2 then 'Todos Assinados'\r\n" + //
//                             "           when  contAssinaturas >= 0 and contAssinaturas <= 2 then 'Pendente de Assinatura'\r\n" + //
//                             "           when  dataAssinatura > dataFinalEnvio then 'Assinado Intempestivamente'\r\n" + //
//                             "           when  dataEntrega = null then 'Aguardando Envio'\r\n" + //
//                             "           when  dataEntrega != null then 'Todos Enviados'\r\n" + //
//                             "           else 'Todos'\r\n" + //
//                             "           end Status,\r\n" + //
                             "       b.exercicio,\r\n" + //
                             "       b.remessa,\r\n" + //
                             "       chave,\r\n" + //
                             "       pr.dataInicialEnvio,\r\n" + //
                             "       pr.dataFinalEnvio \r\n"+ //
                             "from ugsAptas b\r\n" + //
                             "    inner join  cadun.dbo.vwUnidadeGestora c on IDUNIDADEGESTORA = c.cnpj\r\n" + //
                             "    inner join  UnidadeGestora u on u.id = c.cnpj\r\n" + //
                             "    join cadun.dbo.PeriodoRemessa pr on idSistema = 29 and pr.exercicio = b.exercicio and pr.numeroRemessa = b.remessa\r\n" + //
                             "where CNPJ <> '00000000000000' and\r\n" + //
                             "      (CNPJ = :ug or :ug ='todos')\r\n" + //
                             "union all\r\n" + //
                             "select  ug.CNPJ,\r\n" + //
                             "        ug.nomeEntidade,\r\n" + //
                             "        ug.numeroRelatoria as relatoria,\r\n" + //
                             "        null as dataEntrega,\r\n" + //
                             "        null as dataAssinatura,\r\n" + //
                             "        null as contAssinaturas,\r\n" + //
                             "        null as situacaoGFIP,\r\n" + //
                             "        0 as mostraRecibo,\r\n" + ////
//                             "        'pendente de envio' as status,\r\n" + //
                             "        remessas.exercicio as exercicio ,\r\n" + //
                             "        remessas.remessa as remessa,\r\n" + //
                             "        null as chave,\r\n" + //
                             "       pr.dataInicialEnvio,\r\n" + //
                             "       pr.dataFinalEnvio \r\n" + //
                             "from cadun.dbo.vwUnidadeGestora ug,\r\n" + //
                             "     (select DISTINCT exercicio, remessa from  InfoRemessa) remessas\r\n" + //
                             "join cadun.dbo.PeriodoRemessa pr on idSistema = 29 and pr.exercicio = remessas.exercicio and pr.numeroRemessa = remessas.remessa\r\n" + //
                             "where not exists(\r\n" + //
                             "    select *\r\n" + //
                             "    from InfoRemessa ir\r\n" + //
                             "    where ug.CNPJ = ir.idUnidadeGestora\r\n" + //
                             "    and ir.remessa = remessas.remessa\r\n" + //
                             "    and ir.exercicio= remessas.exercicio\r\n" + //
                             "    )\r\n" + //
                            "  and ( :remessa is null or remessas.remessa = :remessa )\r\n" + //
                            "  and  remessas.exercicio = :exercicio \r\n" + //
                            "  and CNPJ <> '00000000000000'\r\n" + //
                            "  and (CNPJ = :ug or :ug ='todos') and ug.cnpj in (select distinct id from sicapap21..UnidadeGestora) order by dataEntrega desc;");

                query.setParameter("exercicio", exercicio)
                    .setParameter("remessa", remessa)
                    .setParameter("ug", "todos");
                List<Object[]> list = query.getResultList();

            //list.stream().forEach((record) -> {

            for (Object[] obj : list) {

                Map<String, Object> mapa = new HashMap<String, Object>();

                mapa.put("cnpj", (String) obj[0]);
                mapa.put("nomeEntidade", (String) obj[1]);
                mapa.put("relatoria", (Integer) obj[2]);
                mapa.put("dataEntrega", (String) obj[3]);
                mapa.put("dataAssinatura", (String) obj[4]);
                mapa.put("contAssinaturas", (Integer) obj[5]);
                mapa.put("situacaoGFIP", (String) obj[6]);
                mapa.put("mostraRecibo", (Integer) obj[7]);
                mapa.put("exercicio", (Integer) obj[8]);
                mapa.put("remessa", (Integer) obj[9]);
                mapa.put("chave", (String) obj[10]);
                mapa.put("dataInicialEnvio", obj[11] != null ? sdf.format((Timestamp) obj[11]) : null);
                mapa.put("dataFinalEnvio", obj[12] != null ? sdf.format((Timestamp) obj[12]) : null);

                retorno.add(mapa);

            }

            return retorno;

        } catch (Exception e) {
            return null;
        }
    }

//    public List<Integer> quantidadeRegistros(Integer exercicio, Integer remessa) {
//        try {
//
//            if (remessa == 0) {
//                remessa = null;
//            }
//            if(exercicio == 0){
//                exercicio = null;
//            }
//
//            String ug = "";
//
//            return getEntityManager().createNativeQuery(
//                    "SELECT COUNT(*) AS totalRecords\n" +
//                            "FROM (\n" +
//                            "    SELECT count(distinct IDUNIDADEGESTORA)\n" +
//                            "    FROM ugsAptas b\n" +
//                            "        INNER JOIN cadun.dbo.vwUnidadeGestora c ON IDUNIDADEGESTORA = c.cnpj\n" +
//                            "        JOIN cadun.dbo.PeriodoRemessa pr ON idSistema = 29 AND pr.exercicio = b.exercicio AND pr.numeroRemessa = b.remessa\n" +
//                            "    WHERE CNPJ <> '00000000000000'\n" +
//                            "      AND (CNPJ = " + ug + " OR " + ug + " = 'todos')\n" +
//                            "    UNION ALL\n" +
//                            "    SELECT count(distinct ug.CNPJ)\n" +
//                            "    FROM cadun.dbo.vwUnidadeGestora ug\n" +
//                            "         JOIN (SELECT DISTINCT exercicio, remessa FROM InfoRemessa) remessas\n" +
//                            "         JOIN cadun.dbo.PeriodoRemessa pr ON idSistema = 29 AND pr.exercicio = remessas.exercicio AND pr.numeroRemessa = remessas.remessa\n" +
//                            "    WHERE NOT EXISTS (\n" +
//                            "        SELECT *\n" +
//                            "        FROM InfoRemessa ir\n" +
//                            "        WHERE ug.CNPJ = ir.idUnidadeGestora\n" +
//                            "          AND ir.remessa = remessas.remessa\n" +
//                            "          AND ir.exercicio = remessas.exercicio\n" +
//                            "    )\n" +
//                            "      AND ( " + remessa + " IS NULL OR remessas.remessa = " + remessa + ")\n" +
//                            "      AND remessas.exercicio = " + exercicio + "\n" +
//                            "      AND CNPJ <> '00000000000000'\n" +
//                            "      AND (CNPJ = " + ug + " OR " + ug + " = 'todos')\n" +
//                            "      AND ug.cnpj IN (SELECT DISTINCT id FROM sicapap21..UnidadeGestora)\n" +
//                            ") AS countQuery;").getResultList();
//        } catch (Exception e) {
//            return null;
//        }
//    }
}