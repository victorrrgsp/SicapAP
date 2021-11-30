package com.example.sicapweb.repository.externo;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.UnidadeGestora;
import br.gov.to.tce.model.ap.relacional.Cargo;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.*;

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
                            "         join Cadun.dbo.PessoaJuridica pj on upc.CodigoPessoaJuridica = pj.Codigo" +
                            "         where upc.CodigoCargo in (:tipo)" +
                            "           and (dataInicio <= :date and (datafim is null or datafim >= :date))" +
                            "           and pj.CNPJ = :unidade and ua.Aplicacao = 29" +
                            "         union" +
                            "         select distinct pf.nome, pf.cpf, b.DataAssinatura, b.DataAssinatura, upc.CodigoCargo" +
                            "         from cadun.dbo.UnidadePessoaCargo upc" +
                            "                  join cadun.dbo.vwpessoa pf on pf.id = upc.CodigoPessoaFisica" +
                            "                  join AutenticacaoAssinatura..UsuarioAplicacao ua on ua.Usuario = pf.cpf" +
                            "                  join AutenticacaoAssinatura..Assinatura b on ua.Usuario = b.Usuario" +
                            "                  join AutenticacaoAssinatura..InfoAssinatura c on c.Assinatura = b.OID and ua.Aplicacao = c.Aplicacao" +
                            "         join Cadun.dbo.PessoaJuridica pj on upc.CodigoPessoaJuridica = pj.Codigo" +
                            "         where upc.CodigoCargo in (:tipo)" +
                            "           and (dataInicio <= :date and (datafim is null or datafim >= :date))" +
                            "           and pj.CNPJ = :unidade" +
                            "           and c.Exercicio = :exercicio" +
                            "           and c.Bimestre = :remessa" +
                            "           and ua.Aplicacao = 29" +
                            "     ) as v " +
                            "group by nome, cpf, CodigoCargo;");
            query.setParameter("tipo", tipoCargo);
            query.setParameter("date", new Date());
            query.setParameter("unidade", infoRemessa.getIdUnidadeGestora());
            query.setParameter("exercicio", infoRemessa.getExercicio());
            query.setParameter("remessa", infoRemessa.getRemessa());
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }


//    public List<Object> buscarRemessaFechada_old() {
//        try {
//            return entityManager.createNativeQuery("" +
//                    "SELECT  COUNT(DISTINCT idCargo) AS contAssinaturas, IDUNIDADEGESTORA , i.nomeUnidade ," +
//                    " i.EXERCICIO, i.remessa, max(i.relatoria) relatoria, " +
//                    "max(f.dataEnvio) dataEntrega, max(dataAssinatura) dataAssinatura " +
//                    "FROM inforemessa i  left join " +
//                    "admassinatura  a on a.chave = i.CHAVE  left join " +
//                    "AutenticacaoAssinatura..Assinatura  ass on ass.oid = a.idAssinatura left join " +
//                    "admfilarecebimento f on f.id = i.idfilarecebimento " +
//                    "group by IDUNIDADEGESTORA,  i.nomeUnidade ,i.EXERCICIO, i.REMESSA").getResultList();
//        } catch (Exception e) {
//            return null;
//        }
//    }

//    public List<Cargo> buscaTodosCargos() {
//        List<Cargo> list = entityManager.createNativeQuery("select * from Cargo "
//                , Cargo.class).getResultList();
//        return list;
//    }

    public List<Map<String,Object>> buscarAcompanhamentoRemessa(Integer exercicio, Integer remessa) {

        List<Map<String,Object>> retorno = new ArrayList<Map<String,Object>>();

        try {

            List<Object[]> list = entityManager.createNativeQuery("with ugsAptas as (" +
                    "select *" +
                    " from cadun.dbo.vwUnidadeGestora" +
                    " where  cadun.dbo.EhVigente("+exercicio+", "+remessa+",CNPJ,  29) = 1" +
                    ")," +
                    " ugsAssinantes as (" +
                    " SELECT COUNT(DISTINCT idCargo) AS contAssinaturas, IDUNIDADEGESTORA , i.EXERCICIO, i.remessa, i.chave, max(i.relatoria) relatoria, max(f.dataEnvio) dataEntrega, max(dataAssinatura) dataAssinatura" +
                    " FROM ugsAptas ug left join" +
                    " inforemessa i on i.IDUNIDADEGESTORA = ug.CNPJ and i.EXERCICIO = "+exercicio+" and i.REMESSA = "+remessa+" and i.status = 1 left join" +
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
                    " order by NomeMunicipio, nomeEntidade remessa asc").getResultList();

        //list.stream().forEach((record) -> {

          for(Object[] obj : list){

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

        };

        return retorno;


       } catch (Exception e) {
            return null;
        }


    }

    public List<Map<String,Object>> buscarTodosAcompanhamentoRemessa() {

        List<Map<String,Object>> retorno = new ArrayList<Map<String,Object>>();

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
                    " SELECT IDUNIDADEGESTORA, c.NomeMunicipio + ' - '+ c.nomeEntidade nomeEntidade, case when b.relatoria is null then c.numeroRelatoria else b.relatoria end relatoria, " +
                    " dataEntrega, dataAssinatura, contAssinaturas, case when  contAssinaturas >= 3 then 1 else 0 end mostraRecibo, exercicio, remessa, chave ," +
                    " (SELECT COUNT(DISTINCT tipo ) "+
                    " FROM SICAPAP21.dbo.DocumentoGfip t "+
                    " WHERE idInfoRemessa = chave) as qntDocumentoGFIP "+
                    "from" +
                    " ugsAptas b inner join  cadun.dbo.vwUnidadeGestora c on IDUNIDADEGESTORA = c.cnpj " +
                    " where CNPJ <> '00000000000000' " +
                    "order by NomeMunicipio, nomeEntidade, remessa asc;").getResultList();

            //list.stream().forEach((record) -> {

            for(Object[] obj : list){

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

            };

            return retorno;


        } catch (Exception e) {
            return null;
        }


    }

    public List<Map<String,Object>> buscarExercicioAcompanhamentoRemessa(Integer exercicio, Integer remessa) {

        List<Map<String,Object>> retorno = new ArrayList<Map<String,Object>>();

        try {
            String existeRemessa= " ";
            if (remessa != 0 && exercicio == 0 ){
                existeRemessa = " where i.REMESSA = "+remessa+" ";
            }

            if (remessa != 0 && exercicio != 0){
                existeRemessa = " and i.REMESSA = "+remessa+" ";
            }

                String existeExercicio= " ";
            if (exercicio != 0 ){
                existeExercicio= " where i.exercicio = "+exercicio+"";
            }

            List<Object[]> list = entityManager.createNativeQuery(
                    "with ugsAptas as ( " +
                            " SELECT  COUNT(DISTINCT idCargo) AS contAssinaturas, IDUNIDADEGESTORA , i.EXERCICIO, i.remessa, i.chave, max(i.relatoria) relatoria, max(f.dataEnvio) dataEntrega, max(dataAssinatura) dataAssinatura" +
                            " FROM inforemessa i left join " +
                            " admassinatura  a on a.chave = i.CHAVE  left join " +
                            " AutenticacaoAssinatura..Assinatura  ass on ass.oid = a.idAssinatura left join " +
                            " admfilarecebimento f on f.id = i.idfilarecebimento " +
                            ""+existeExercicio+ existeRemessa + " " +
                            "group by IDUNIDADEGESTORA, i.EXERCICIO, i.REMESSA,  i.CHAVE " +
                            ") " +
                            "select IDUNIDADEGESTORA, c.NomeMunicipio + ' - '+ c.nomeEntidade nomeEntidade, case when b.relatoria is null then c.numeroRelatoria else b.relatoria end relatoria, " +
                            "dataEntrega, dataAssinatura, contAssinaturas, case when  contAssinaturas >= 3 then 1 else 0 end mostraRecibo, exercicio, remessa, chave " +
                            "from" +
                            " ugsAptas b inner join  cadun.dbo.vwUnidadeGestora c on IDUNIDADEGESTORA = c.cnpj " +
                            " where CNPJ <> '00000000000000' " +
                            "order by NomeMunicipio, nomeEntidade, remessa asc;").getResultList();

            //list.stream().forEach((record) -> {

            for(Object[] obj : list){

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
                mapa.put("qntDocumentoGFIP", obj[10]);
                retorno.add(mapa);

            };

            return retorno;


        } catch (Exception e) {
            return null;
        }


    }




}
