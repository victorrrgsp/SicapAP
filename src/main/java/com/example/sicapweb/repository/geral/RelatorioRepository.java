package com.example.sicapweb.repository.geral;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Repository;

import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.StaticMethods;

import br.gov.to.tce.model.ap.relacional.Lei;

@Repository
public class RelatorioRepository extends DefaultRepository<Lei, BigInteger> {
    /**
     *
     */
    private static final int LIMITE_ACUMULO = 10;

    public RelatorioRepository(EntityManager em) {
        super(em);
    }

    public List<HashMap<String, Object>> buscarfolhaAnalitica( String cpf, String nome, String Natureza, List<String> Vinculo, int ano, int mes, List<String> lotacao, List<String> UnidadeAdministrativa, String folhaItem,String cargo) {
        var query = getEntityManager().createNativeQuery(
                        "with principal as(\n" +
                                "    select distinct NaturezaRubrica , codigoFolhaItem,ud.codigoUnidadeAdministrativa,\n" +
                                "       wfp.idUnidadeGestora                                         as CNPJ,\n" +
                                "       wfp.unidadeGestora                                            as Entidade,\n" +
                                "       wfp.Competencia,\n" +
                                "       YEAR(wfp.Competencia)                                         as Ano,\n" +
                                "       MONTH(wfp.Competencia)                                        as Mes,\n" +
                                "       wfp.nome                                                      as Nome,\n" +
                                "       wfp.cpfServidor                                               as CPF,\n" +
                                "       wfp.cargo                                                     as Cargo,\n" +
                                "       wfp.nomeCargoOrigem                                           as cargoUg,\n" +
                                "       wfp.TipoAdmissao                                              as FolhaVinculo,\n" +
                                "       wfp.TipoFolha                                                 as FolhaTipo,\n" +
                                "       wfp.classe                                                    as regime,\n" +
                                "       wfp.JornadaFolha                                              as jornada,\n" +
                                "       wfp.folhaItemUnidadeGestora                                   as FolhaItem,\n" +
                                "       wfp.natureza                                          as FolhaEsocial,\n" +
                                "       wfp.matriculaServidor as matriculaServidor,\n" +
                                "       wfp.TipoAto,\n" +
                                "wfp.CodigoFolhaItemESocial,\n" +
                                "       wfp.NumeroAtoAdmissao,\n" +
                                "       wfp.nomeLotacao,\n" +
                                "       ud.nome as unidadeAdministrativa ,\n" +
                                "       (case wfp.NaturezaRubrica when 'Vantagem' then wfp.valor end) as Vantagem,\n" +
                                "       (case wfp.NaturezaRubrica when 'Desconto' then wfp.valor end) as Descontos,\n" +
                                "       (case wfp.NaturezaRubrica\n" +
                                "            when 'Vantagem' then wfp.valor\n" +
                                "            when 'Desconto' then (wfp.valor * -1) end)  as Valor\n" +
                                "from vwFolhaPagamento wfp\n" +
                                "join SICAPAP21.dbo.Lotacao l on wfp.idLotacao = l.id\n" +
                                "join UnidadeAdministrativa ud on l.idUnidadeAdministrativa = ud.id\n" +
                                "where (wfp.idUnidadeGestora = :UnidadeGestora )\n" +
                                (UnidadeAdministrativa != null?"  and ud.codigoUnidadeAdministrativa  in :UnidadeAdministrativa\n":"") +
                                "\n" +
                                "  and wfp.exercicio = :Ano\n" +
                                "  and (wfp.remessa = :Mes or :Mes = null )\n" +
                                (lotacao != null?"  and wfp.nomeLotacao in :lotacao\n":"") +
                                (Vinculo != null?"  and wfp.TipoAdmissao in :Vinculo\n":"") +
                                (Natureza != null?"  and wfp.NaturezaRubrica in :Natureza\n":"") +
                                "  and wfp.folhaItemUnidadeGestora not like 'Base%'\n" +
                                "  and (:nome is null or upper(wfp.nome) like'%'+upper(:nome)+'%')\n" +
                                "  and (\n" +
                                "      (:cpf is null or\n" +
                                "            (:cargo is null or\n" +
                                "             wfp.nomeCargoOrigem = :cargo)\n" +
                                "            )\n" +
                                "    or wfp.cpfServidor like concat('%', :cpf, '%'))\n" +
                                (folhaItem != null?"  and wfp.FolhaItemUnidadeGestora like '%'+ :folhaItem +'%'\n":"") +
                                ")\n" +
                                "\n" +
                                "select distinct * from principal\n" +
                                "order by NaturezaRubrica desc, codigoFolhaItem;")
                                .setParameter("cpf", cpf )
                                .setParameter("nome", nome)
                                .setParameter("Ano", ano)
                                .setParameter("Mes", mes)
                                .setParameter("cargo", cargo)
                                .setParameter("UnidadeGestora", User.getUser(super.request).getUnidadeGestora().getId());
                                
        if(UnidadeAdministrativa != null){
            query.setParameter("UnidadeAdministrativa", UnidadeAdministrativa);
        }if(Natureza != null){
            query.setParameter("Natureza", Natureza);
        }if(folhaItem != null){
            query.setParameter("folhaItem", folhaItem);
        }if(Vinculo != null){
            query.setParameter("Vinculo", Vinculo);
        }if(lotacao != null){
            query.setParameter("lotacao", lotacao);
        }

        return StaticMethods.getHashmapFromQuery(query);
    }

    public List<HashMap<String, Object>> buscarFolhaPesoas( String matriculaServidor , String Natureza, int ano, int mes, String folhaItem ) {
        var query = getEntityManager().createNativeQuery(
                                "with principal as(\n" +
                                "    select distinct NaturezaRubrica,\r\n" + //
                                "       codigoFolhaItem,\n" +
                                "       wfp.TipoFolha                                                 as FolhaTipo,\n" +
                                "       wfp.folhaItemUnidadeGestora                                   as FolhaItem,\n" +
                                "       wfp.natureza                                                  as FolhaEsocial,\n" +
                                "wfp.CodigoFolhaItemESocial,\n" +
                                "       (case wfp.NaturezaRubrica when 'Vantagem' then wfp.valor end) as Vantagem,\n" +
                                "       (case wfp.NaturezaRubrica when 'Desconto' then wfp.valor end) as Descontos,\n" +
                                "       (case wfp.NaturezaRubrica\n" +
                                "            when 'Vantagem' then wfp.valor\n" +
                                "            when 'Desconto' then (wfp.valor * -1) end)  as Valor\n" +
                                "from vwFolhaPagamento wfp\n" +
                                "where exercicio= :Ano\n" +
                                "  and (remessa = :Mes or :Mes = null )\n" +
                                "  and wfp.idUnidadeGestora = :UnidadeGestora \n" +
                                (Natureza != null?"  and wfp.NaturezaRubrica in :Natureza\n":"") +
                                "  and wfp.folhaItemUnidadeGestora not like 'Base%'\n" +
                                "  and wfp.matriculaServidor = :matriculaServidor \n" +
                                (folhaItem != null?"  and wfp.FolhaItemUnidadeGestora like '%'+ :folhaItem +'%'\n":"") +
                                ")\n" +
                                "\n" +
                                "select distinct * from principal\n" +
                                "order by NaturezaRubrica desc, codigoFolhaItem;")
                                .setParameter("Ano", ano)
                                .setParameter("Mes", mes)
                                .setParameter("matriculaServidor", matriculaServidor)
                                .setParameter("UnidadeGestora", User.getUser(super.request).getUnidadeGestora().getId());
                                
        return StaticMethods.getHashmapFromQuery(query);
    }

    public List<HashMap<String, Object>> buscarPesoasfolha( String cpf, String nome, int ano,Integer mes) {
        return buscarPesoasfolha(cpf, nome, null, null, ano,mes,null, null, null,  null);
    }

    public List<HashMap<String, Object>> buscarPesoasfolha( String cpf, String nome, String Natureza, List<String> Vinculo, int ano, Integer mes, List<String> lotacao, List<String> UnidadeAdministrativa, String folhaItem,String cargo) {
        List<String> cpfsServidor ;
        if (cpf == null && nome == null) {
            cpfsServidor =null;
        }else{
            cpfsServidor = getCpfsServidor(cpf, nome);
        }
        // retorna um array de string com o cpf [coluna 1] da lista de array de objetos de getQueryinfoServidor(PesoaParam).getResultList()
        
        var query = getQueryServidoresFolha(Natureza, Vinculo, ano, mes, lotacao, UnidadeAdministrativa,User.getUser(super.request).getUnidadeGestora().getId(), folhaItem,
                cargo, cpfsServidor);

        return StaticMethods.getHashmapFromQuery(query);
    }

    private List<String> getCpfsServidor(String cpf, String nome) {
        var PesoaParam = cpf != null?cpf:nome;
        List<String> cpfsServidor = new ArrayList<>();

        if (PesoaParam == null || PesoaParam.length()>4) {
             cpfsServidor = (List) getQueryinfoServidor(PesoaParam==null?"":PesoaParam)
             .getResultList()
             .stream()
                                                .map(x -> {
                                                    return ((Object[]) x)[1];
                                                }).collect(Collectors.toList());
        }
        return cpfsServidor;
    }

    private Query getQueryServidoresFolha(String Natureza, List<String> Vinculo, int ano, Integer mes, List<String> lotacao,
            List<String> UnidadeAdministrativa, String UnidadeGestora, String folhaItem, String cargo,
            List<String> cpfsServidor) {
        var filtro = ""; 
        if(cpfsServidor!= null && cpfsServidor.size() > 0){
            var parms = "";
            for (String string : cpfsServidor) {
                parms += "'"+string+"',";
            }
            parms = parms.substring(0, parms.length()-1);
            filtro +=" and wfp.cpfServidor in ("+parms+")\n" ;
        }else{
            filtro +=
            (Vinculo != null?"  and wfp.TipoAdmissao in :Vinculo\n":"")+
            (UnidadeAdministrativa != null?"  and ud.codigoUnidadeAdministrativa  in :UnidadeAdministrativa\n":"") +
            (lotacao != null?"  and wfp.nomeLotacao in :lotacao\n":"")+
            "  and (\n" +
            "           :cargo is null or\n" +
            "           wfp.nomeCargoOrigem = :cargo\n" +
            "       )\n" ;
        }

        filtro += "  and YEAR(wfp.Competencia) = :Ano\n" +
        (mes != null?"  and MONTH(wfp.Competencia) = :Mes \n":"") +
        (Natureza != null?"  and wfp.NaturezaRubrica in :Natureza\n":"") +
        (folhaItem != null?"  and wfp.FolhaItemUnidadeGestora like '%'+ :folhaItem +'%'\n":"") +
        (UnidadeGestora != null?"  and wfp.idUnidadeGestora = :UnidadeGestora\n":"") ;
        var sql = "with principal as(\n" +
                                "    select distinct ud.codigoUnidadeAdministrativa,\n" +
                                "       wfp.idUnidadeGestora                                          ,\n" +
                                "       wfp.unidadeGestora                                            as Entidade,\n" +
                                "       YEAR(wfp.Competencia)                                         as Ano,\n" +
                                "       MONTH(wfp.Competencia)                                        as Mes,\n" +
                                "       wfp.Competencia                                        ,\n" +
                                "       wfp.nome                                                      as Nome,\n" +
                                "       wfp.cpfServidor                                               ,\n" +
                                "       wfp.classe                                                    as regime,\n" +
                                "       wfp.cargo                                                     as Cargo,\n" +
                                "       wfp.nomeCargoOrigem                                           as cargoUg,\n" +
                                "       wfp.TipoAdmissao                                              as FolhaVinculo,\n" +
                                "       wfp.JornadaFolha                                              as jornada,\n" +
                                "       wfp.matriculaServidor as matriculaServidor,\n" +
                                "       wfp.TipoAto,\n" +
                                "       wfp.NumeroAtoAdmissao,\n" +
                                "       wfp.nomeLotacao,\r\n" + //
                                "       wfp.RegimePrevidenciario,\r\n" + //
                                "       wfp.DataExercicio,\n" +
                                "       ud.nome as unidadeAdministrativa ,\n" +
                                ((cpfsServidor!=null && cpfsServidor.size()<LIMITE_ACUMULO)? "       count(wfp.matriculaServidor) over(PARTITION BY wfp.cpfServidor,wfp.Competencia) acumuloDeVinculos,\n" :"")+
                                "       sum((case wfp.NaturezaRubrica when 'Vantagem' then wfp.valor end)) as Vantagem,\n" +
                                "       sum((case wfp.NaturezaRubrica when 'Desconto' then wfp.valor end)) as Descontos,\n" +
                                "       sum((case wfp.NaturezaRubrica\n" +
                                "            when 'Vantagem' then wfp.valor\n" +
                                "            when 'Desconto' then (wfp.valor * -1) end)) as Liquido\n"+
                                "from vwFolhaPagamento wfp\n" +
                                "join SICAPAP21.dbo.Lotacao l on wfp.idLotacao = l.id\n" +
                                "join UnidadeAdministrativa ud on l.idUnidadeAdministrativa = ud.id\n" +
                                "where wfp.folhaItemUnidadeGestora not like 'Base%'"+
                                ((cpfsServidor==null || cpfsServidor.size()>LIMITE_ACUMULO)?filtro :"")+
                                "group by\n" +
                                "    wfp.RegimePrevidenciario,\n" + //
                                "    ud.codigoUnidadeAdministrativa,\n" +
                                "    wfp.idUnidadeGestora                                            ,\n" +
                                "    wfp.DataExercicio                                               ,\n" +
                                "    wfp.unidadeGestora                                              ,\n" +
                                "    wfp.Competencia                                                 ,\n" +
                                "    wfp.nome                                                        ,\n" +
                                "    wfp.cpfServidor                                                 ,\n" +
                                "    wfp.cargo                                                       ,\n" +
                                "    wfp.Classe                                                      ,\n" +
                                "    wfp.nomeCargoOrigem                                             ,\n" +
                                "    wfp.TipoAdmissao                                                ,\n" +
                                "    wfp.JornadaFolha                                                ,\n" +
                                "    wfp.matriculaServidor                                           ,\n" +
                                "    wfp.TipoAto                                                     ,\n" +
                                "    wfp.NumeroAtoAdmissao                                           ,\n" +
                                "    wfp.nomeLotacao                                                 ,\n" +
                                "    ud.nome\n" +
                                ")\n" +
                                "select distinct * from principal wfp\n" +
                                ((cpfsServidor!=null && cpfsServidor.size()<LIMITE_ACUMULO)?" where 1=1 \n"+ filtro :"")+
                                "order by nome;";
        var query = getEntityManager().createNativeQuery(sql)
                                .setParameter("Ano", ano)
                                .setParameter("UnidadeGestora", UnidadeGestora);
        if(mes != null){
            query.setParameter("Mes", mes);
        }
        if(Natureza != null){
            query.setParameter("Natureza", Natureza);
        }
        if(folhaItem != null){
            query.setParameter("folhaItem", folhaItem);
        }
        if(cpfsServidor==null || cpfsServidor.size() == 0) {
            query.setParameter("cargo", cargo);
            if (UnidadeAdministrativa != null) {
                query.setParameter("UnidadeAdministrativa", UnidadeAdministrativa);
            }
            if (Vinculo != null) {
                query.setParameter("Vinculo", Vinculo);
            }
            if (lotacao != null) {
                query.setParameter("lotacao", lotacao);
            }
        }
        return query;
    }

    private Query getQueryinfoServidor(String cpf) {
        return getEntityManager().createNativeQuery("select distinct nome,cpfServidor,dataNascimento \r\n" + //
                                            "from SICAPAP21..Servidor s \r\n" + //
                                            "join InfoRemessa on s.chave = InfoRemessa.chave\r\n"+
                                            "where s.cpfServidor+s.nome like '%'+:cpf+'%' and InfoRemessa.idUnidadeGestora = :UG")
                .setParameter("cpf", cpf)
                .setParameter("UG", User.getUser(super.request).getUnidadeGestora().getId());
    }

    private Query getQueryHistoricoDeVinculo(String cpf) {
        throw new NotImplementedException();
    }

    public HashMap<String, Object> buscarInfoPesoas(String cpf) {
        var hashMap = new HashMap<String, Object>();

        var queryinfoServidor = getQueryinfoServidor(cpf);
        var queryHistoricoDeVinculo = getQueryHistoricoDeVinculo(cpf);
        var queryAcumulacaoDeVinculos = getEntityManager().createNativeQuery("select 1 as teste");
        var queryRemessasComFolha = getEntityManager().createNativeQuery("select 1 as teste");
        
        hashMap.put("infoServidor", StaticMethods.getHashmapFromQuery(queryinfoServidor));
        hashMap.put("HistoricoDeVinculo", StaticMethods.getHashmapFromQuery(queryHistoricoDeVinculo));
        hashMap.put("AcumulacaoDeVinculos", StaticMethods.getHashmapFromQuery(queryAcumulacaoDeVinculos));
        hashMap.put("RemessasComFolha" , StaticMethods.getHashmapFromQuery(queryRemessasComFolha));
        return hashMap;
    }
}