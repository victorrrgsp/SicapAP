package com.example.sicapweb.repository.geral;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.security.access.method.P;
import org.springframework.stereotype.Repository;

import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.util.StaticMethods;

import br.gov.to.tce.model.ap.relacional.Lei;

@Repository
public class RelatorioRepository extends DefaultRepository<Lei, BigInteger> {
    public RelatorioRepository(EntityManager em) {
        super(em);
    }

    public List<HashMap<String, Object>> buscarfolhaAnalitica( String cpf, String nome, String Natureza, List<String> Vinculo, int ano, int mes, List<String> lotacao, List<String> UnidadeAdministrativa, String UnidadeGestora, String folhaItem,String cargo) {
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
                                "where (:UnidadeGestora = 'todos' or wfp.idUnidadeGestora = :UnidadeGestora )\n" +
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
                                .setParameter("UnidadeGestora", UnidadeGestora);
                                
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

    public List<HashMap<String, Object>> buscarFolhaPesoas( String matriculaServidor , String Natureza, int ano, int mes, String folhaItem, String unidadeGestora ) {
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
                                .setParameter("UnidadeGestora", unidadeGestora);
                                
        return StaticMethods.getHashmapFromQuery(query);
    }

    public List<HashMap<String, Object>> buscarPesoasfolha( String cpf, String nome, int ano,Integer mes) {
        return buscarPesoasfolha(cpf, nome, null, null, ano,mes,null, null, null, null, null);
    }
    public List<HashMap<String, Object>> buscarPesoasfolha( String cpf, String nome, String Natureza, List<String> Vinculo, int ano, Integer mes, List<String> lotacao, List<String> UnidadeAdministrativa, String UnidadeGestora, String folhaItem,String cargo) {
        var PesoaParam = cpf != null?cpf:nome;
        List<String> cpfsServidor = new ArrayList<>();

        if (PesoaParam != null && PesoaParam.length()>4) {
             cpfsServidor = (List) getQueryinfoServidor(PesoaParam)
                                                .getResultList()
                                                .stream()
                                                .map(x -> {
                                                    return ((Object[]) x)[1];
                                                }).collect(Collectors.toList());
        }
        // retorna um array de string com o cpf [coluna 1] da lista de array de objetos de getQueryinfoServidor(PesoaParam).getResultList()
        
        var sql = "with principal as(\n" +
                                "    select distinct ud.codigoUnidadeAdministrativa,\n" +
                                "       wfp.idUnidadeGestora                                          as CNPJ,\n" +
                                "       wfp.unidadeGestora                                            as Entidade,\n" +
                                "       YEAR(wfp.Competencia)                                         as Ano,\n" +
                                "       MONTH(wfp.Competencia)                                        as Mes,\n" +
                                "       wfp.nome                                                      as Nome,\n" +
                                "       wfp.cpfServidor                                               as CPF,\n" +
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
                                "       sum((case wfp.NaturezaRubrica when 'Vantagem' then wfp.valor end)) as Vantagem,\n" +
                                "       sum((case wfp.NaturezaRubrica when 'Desconto' then wfp.valor end)) as Descontos,\n" +
                                "       sum((case wfp.NaturezaRubrica\n" +
                                "            when 'Vantagem' then wfp.valor\n" +
                                "            when 'Desconto' then (wfp.valor * -1) end)) as Liquido\n" +
                                "from vwFolhaPagamento wfp\n" +
                                "join SICAPAP21.dbo.Lotacao l on wfp.idLotacao = l.id\n" +
                                "join UnidadeAdministrativa ud on l.idUnidadeAdministrativa = ud.id\n" +
                                "where \n";
                                if(cpfsServidor.size() > 0){
                                    sql +=" wfp.cpfServidor in :cpfsServidor\n" ;
                                }else{
                                    sql +=" (:UnidadeGestora = 'todos' or wfp.idUnidadeGestora = :UnidadeGestora )\n" + 
                                    (Vinculo != null?"  and wfp.TipoAdmissao in :Vinculo\n":"")+
                                    (UnidadeAdministrativa != null?"  and ud.codigoUnidadeAdministrativa  in :UnidadeAdministrativa\n":"") +
                                    (lotacao != null?"  and wfp.nomeLotacao in :lotacao\n":"")+
                                    "  and (\n" +
                                    "           :cargo is null or\n" +
                                    "           wfp.nomeCargoOrigem = :cargo\n" +
                                    "       )\n" ;
                                }
                                sql += "  and Exercicio = :Ano\n" +
                                (mes != null?"  and remessa = :Mes \n":"") +
                                (Natureza != null?"  and wfp.NaturezaRubrica in :Natureza\n":"") +
                                "  and wfp.folhaItemUnidadeGestora not like 'Base%'\n" +
                                (folhaItem != null?"  and wfp.FolhaItemUnidadeGestora like '%'+ :folhaItem +'%'\n":"") +
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
                                "select distinct * from principal\n" +
                                "order by nome;";

        var query = getEntityManager().createNativeQuery(sql)
                                .setParameter("Ano", ano);
        
        if(mes != null){
            query.setParameter("Mes", mes);
        }
        if(Natureza != null){
            query.setParameter("Natureza", Natureza);
        }
        if(folhaItem != null){
            query.setParameter("folhaItem", folhaItem);
        }
        if(cpfsServidor.size() > 0){
            query.setParameter("cpfsServidor", cpfsServidor);
        }else{
            query.setParameter("UnidadeGestora", UnidadeGestora)
                 .setParameter("cargo", cargo);
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

        return StaticMethods.getHashmapFromQuery(query);
    }

    public List<HashMap<String, Object>> buscarFichafinanceira(String cpf, String nome, int ano) {
        var query = getEntityManager().createNativeQuery(
            "declare @tbfolha table(ug varchar(14),cpf varchar(11),ano  int, mes  int ,JornadaSemanal int);\r\n" + //
            "declare @tbjoin  table(ug varchar(14),cpf varchar(11),ano  int, mes  int ,chtotal int );\r\n" + //
            "declare @tbgrupo table(total int ,cpf varchar(11),ano  int, mes  int,chtotal int );\r\n" + //
             "\n" +
            "IF :nome is null\n" +
            "    INSERT INTO @tbfolha\n" +
            "    SELECT\n" +
            "        p.idUnidadeGestora, p.cpfServidor, p.exercicio, p.remessa, CASE WHEN p.JornadaFolha = '88' THEN 0 WHEN p.JornadaFolha = '99' THEN 0 WHEN UPPER(p.JornadaFolha) = 'OUTROS' THEN 0 WHEN p.JornadaFolha = 'Regime de plantão' THEN 0 ELSE p.JornadaFolha END JornadaFolha\n" +
            "    FROM SICAPAP21..vwFolhaPagamento p\n" +
            "    WHERE\n" +
            "        p.idUnidadeGestora != '61365284013435' --unidade de teste\n" +
            "        AND p.exercicio = :Ano\n" +
            "        AND (p.cpfServidor = :cpf)\n" +
            "    GROUP BY p.idUnidadeGestora, p.cpfServidor, p.exercicio, p.remessa, p.JornadaFolha;\n" +
            "ELSE\n" +
            "    INSERT INTO @tbfolha\n" +
            "    SELECT\n" +
            "        p.idUnidadeGestora, p.cpfServidor, p.exercicio, p.remessa, CASE WHEN p.JornadaFolha = '88' THEN 0 WHEN p.JornadaFolha = '99' THEN 0 WHEN UPPER(p.JornadaFolha) = 'OUTROS' THEN 0 WHEN p.JornadaFolha = 'Regime de plantão' THEN 0 ELSE p.JornadaFolha END JornadaFolha\n" +
            "    FROM SICAPAP21..vwFolhaPagamento p\n" +
            "    WHERE\n" +
            "        p.idUnidadeGestora != '61365284013435' --unidade de teste\n" +
            "        AND p.exercicio = :Ano\n" +
            "        AND (UPPER(p.nome) LIKE '%' + UPPER(:nome) + '%')\n" +
            "    GROUP BY p.idUnidadeGestora, p.cpfServidor, p.exercicio, p.remessa, p.JornadaFolha;\n" +
            "\n" +
            "INSERT INTO @tbgrupo\n" +
            "    SELECT (COUNT(ug)) total, cpf, ano, Mes, SUM(JornadaSemanal) chtotal\n" +
            "    FROM @tbfolha\n" +
            "    GROUP BY cpf, ano, mes;\n" +
            "\n" +
            "INSERT INTO @tbjoin\n" +
            "    SELECT p.ug, o.cpf, o.ano, o.mes, o.chtotal\n" +
            "    FROM @tbfolha p\n" +
            "    JOIN @tbgrupo o ON o.cpf = p.cpf AND o.ano = p.ano AND o.mes = p.mes;\n" +
            "\n" +
            "SELECT DISTINCT\n" +
            "    p.cpf cpflimpo, cadun.dbo.formatarCPF(p.cpf) CPF, p.ano Ano, p.mes Mes, cadun.dbo.formatarCNPJ(p.ug) CNPJ, f.nome Pessoa, pj.nomeEntidade Entidade,\n" +
            "    f.NaturezaRubrica, f.codigoFolhaItem, f.matriculaServidor, f.cargo, f.Funcao, f.Valor, f.DataExercicio inicioExercicio,\n" +
            "    p.chtotal, f.TipoAdmissao vinculo, f.FolhaItemESocial, f.FolhaItemUnidadeGestora,\n" +
            "    CASE p.mes\n" +
            "        WHEN 1 THEN 'Janeiro'\n" +
            "        WHEN 2 THEN 'Fevereiro'\n" +
            "        WHEN 3 THEN 'Março'\n" +
            "        WHEN 4 THEN 'Abril'\n" +
            "        WHEN 5 THEN 'Maio'\n" +
            "        WHEN 6 THEN 'Junho'\n" +
            "        WHEN 7 THEN 'Julho'\n" +
            "        WHEN 8 THEN 'Agosto'\n" +
            "        WHEN 9 THEN 'Setembro'\n" +
            "        WHEN 10 THEN 'Outubro'\n" +
            "        WHEN 11 THEN 'Novembro'\n" +
            "        WHEN 12 THEN 'Dezembro'\n" +
            "    END AS mesExtenso,\n" +
            "    CASE\n" +
            "        WHEN f.JornadaFolha = '99' THEN 'Plantão'\n" +
            "        WHEN f.JornadaFolha = '88' THEN 'Outros'\n" +
            "        WHEN f.JornadaFolha = '0' THEN 'Inativo'\n" +
            "        ELSE CAST(f.JornadaFolha AS VARCHAR(50))\n" +
            "    END JornadaFolha\n" +
            "FROM @tbjoin p\n" +
            "LEFT JOIN SICAPAP21..vwFolhaPagamento f ON f.exercicio = p.ano AND f.remessa = p.mes AND f.cpfServidor = p.cpf AND f.idUnidadeGestora = p.ug\n" +
            "LEFT JOIN cadun..vwPessoaSimples pj ON pj.CNPJ = RTRIM(p.ug)");

        query.setParameter("cpf", cpf);
        query.setParameter("nome", nome);
        query.setParameter("Ano", ano);

        return StaticMethods.getHashmapFromQuery(query);
    }

    private Query getQueryinfoServidor(String cpf) {
        return getEntityManager()
                                    .createNativeQuery("select distinct nome,cpfServidor,dataNascimento \r\n" + //
                                            "from SICAPAP21..Servidor s \r\n" + //
                                            "where s.cpfServidor+s.nome like '%'+:cpf+'%'")
                .setParameter("cpf", cpf);
    }

    private Query getQueryHistoricoDeVinculo(String cpf) {
        var queryHistoricoDeVinculo = getEntityManager().createNativeQuery("with\r\n" + //
                "     MAdmisao as (\r\n" + //
                "    select --D.dataDesligamento,\r\n" + //
                "           D.id,\r\n" + //
                "           --D.tipoDesligamento,\r\n" + //
                "           D.idAto,\r\n" + //
                "           D.chave,\r\n" + //
                "           --D.idAdmissao,\r\n" + //
                "           --a.*,\r\n" + //
                "           D.id as idAdmisao,\r\n" + //
                "           D.dataExercicio,\r\n" + //
                "           D.dataFim,\r\n" + //
                "           D.dataInicio,\r\n" + //
                "           D.dataPosse,\r\n" + //
                "           D.matriculaServidor,\r\n" + //
                "           D.numeroEdital,\r\n" + //
                "           D.numeroInscricao,\r\n" + //
                "           D.tipoAdmissao,\r\n" + //
                "           D.idAto as idAtoAdmisao,\r\n" + //
                "           D.idCargo,\r\n" + //
                "           D.idServidor,\r\n" + //
                "           D.idAdmissaoFolha,\r\n" + //
                "\r\n" + //
                "           at.cnpjUgPublicacao,\r\n" + //
                "           at.dataPublicacao,\r\n" + //
                "           at.numeroAto,\r\n" + //
                "           -- at.tipoAto,\r\n" + //
                "           case\r\n" + //
                "               WHEN  at.tipoAto = 1 then  'Lei'\r\n" + //
                "               WHEN  at.tipoAto = 2 then  'Decreto'\r\n" + //
                "               WHEN  at.tipoAto = 3 then  'Decreto legislativo'\r\n" + //
                "               WHEN  at.tipoAto = 4 then  'Portaria'\r\n" + //
                "               WHEN  at.tipoAto = 5 then  'Resolu??o'\r\n" + //
                "               WHEN  at.tipoAto = 6 then  'Circular'\r\n" + //
                "               WHEN  at.tipoAto = 7 then  'Despacho'\r\n" + //
                "               WHEN  at.tipoAto = 8 then  'Processo'\r\n" + //
                "               WHEN  at.tipoAto = 9 then  'Sem ato'\r\n" + //
                "               WHEN  at.tipoAto = 10 then  'Ato'\r\n" + //
                "               WHEN  at.tipoAto = 11 then  'Contrato'\r\n" + //
                "               WHEN  at.tipoAto = 12 then  'Emenda Constitucional'\r\n" + //
                "               WHEN  at.tipoAto = 99 then  'Outros'\r\n" + //
                "               end as tipoAto,\r\n" + //
                "           at.veiculoPublicacao,\r\n" + //
                "           rank() over (partition by D.matriculaServidor ,at.numeroAto,AT.tipoAto order by d.id ) posicaoId\r\n" + //
                "    from SICAPAP21..Admissao D\r\n" + //
                "             left join Ato at on D.idAto = at.id\r\n" + //
                "\r\n" + //
                ")\r\n" + //
                "\r\n" + //
                "    ,AdimisoeDistintas as (\r\n" + //
                "    select Ma.*,\r\n" + //
                "           '' as desligamento,\r\n" + //
                "           s.cpfServidor,\r\n" + //
                "           c.nomeCargo,\r\n" + //
                "           'admiss?o'as movimentaca\r\n" + //
                "    from MAdmisao Ma\r\n" + //
                "            left join Servidor  s on Ma.idServidor = s.id\r\n" + //
                "            inner join SICAPAP21..cargo c on Ma.idCargo = c.id\r\n" + //
                "    where posicaoId = 1 and S.cpfServidor = :cpf\r\n" + //
                ")\r\n" + //
                "-- select * from AdimisoeDistintas\r\n" + //
                "    ,MAposentadoria as (\r\n" + //
                "    select --D.dataDesligamento,\r\n" + //
                "           D.id,\r\n" + //
                "           --D.tipoDesligamento,\r\n" + //
                "           D.idAto,\r\n" + //
                "           D.chave,\r\n" + //
                "           --D.idAdmissao,\r\n" + //
                "           --a.*,\r\n" + //
                "           a.id as idAdmisao,\r\n" + //
                "           a.dataExercicio,\r\n" + //
                "           a.dataFim,\r\n" + //
                "           a.dataInicio,\r\n" + //
                "           a.dataPosse,\r\n" + //
                "           a.matriculaServidor,\r\n" + //
                "           a.numeroEdital,\r\n" + //
                "           a.numeroInscricao,\r\n" + //
                "           a.tipoAdmissao,\r\n" + //
                "           a.idAto as idAtoAdmisao,\r\n" + //
                "           a.idCargo,\r\n" + //
                "           a.idServidor,\r\n" + //
                "           a.idAdmissaoFolha,\r\n" + //
                "\r\n" + //
                "           at.cnpjUgPublicacao,\r\n" + //
                "           at.dataPublicacao,\r\n" + //
                "           at.numeroAto,\r\n" + //
                "           -- at.tipoAto,\r\n" + //
                "\r\n" + //
                "\r\n" + //
                "           case\r\n" + //
                "               WHEN  at.tipoAto = 1 then  'Lei'\r\n" + //
                "               WHEN  at.tipoAto = 2 then  'Decreto'\r\n" + //
                "               WHEN  at.tipoAto = 3 then  'Decreto legislativo'\r\n" + //
                "               WHEN  at.tipoAto = 4 then  'Portaria'\r\n" + //
                "               WHEN  at.tipoAto = 5 then  'Resolu??o'\r\n" + //
                "               WHEN  at.tipoAto = 6 then  'Circular'\r\n" + //
                "               WHEN  at.tipoAto = 7 then  'Despacho'\r\n" + //
                "               WHEN  at.tipoAto = 8 then  'Processo'\r\n" + //
                "               WHEN  at.tipoAto = 9 then  'Sem ato'\r\n" + //
                "               WHEN  at.tipoAto = 10 then  'Ato'\r\n" + //
                "               WHEN  at.tipoAto = 11 then  'Contrato'\r\n" + //
                "               WHEN  at.tipoAto = 12 then  'Emenda Constitucional'\r\n" + //
                "               WHEN  at.tipoAto = 99 then  'Outros'\r\n" + //
                "               end as tipoAto,\r\n" + //
                "           at.veiculoPublicacao,\r\n" + //
                "           rank() over (partition by A.matriculaServidor ,at.numeroAto,AT.tipoAto order by d.id ) posicaoId\r\n" + //
                "    from SICAPAP21..Aposentadoria D\r\n" + //
                "             join SICAPAP21..Admissao A on D.id = A.id\r\n" + //
                "             left join Ato at on D.idAto = at.id\r\n" + //
                ")\r\n" + //
                "\r\n" + //
                "    ,AposentadoriaDistintas as (\r\n" + //
                "    select Ma.*,\r\n" + //
                "           '' as desligamento,\r\n" + //
                "           s.cpfServidor,\r\n" + //
                "           c.nomeCargo,\r\n" + //
                "           'Aposentadoria' as movimentaca\r\n" + //
                "    from MAposentadoria Ma\r\n" + //
                "            left join Servidor  s on Ma.idServidor = s.id\r\n" + //
                "            inner join SICAPAP21..cargo c on Ma.idCargo = c.id\r\n" + //
                "    where posicaoId = 1 and S.cpfServidor = :cpf\r\n" + //
                ")\r\n" + //
                "--select * from AposentadoriaDistintas\r\n" + //
                "    ,MDesligamento as (\r\n" + //
                "    select --D.dataDesligamento,\r\n" + //
                "           D.id,\r\n" + //
                "           --D.tipoDesligamento,\r\n" + //
                "           D.idAto,\r\n" + //
                "           D.chave,\r\n" + //
                "           D.tipoDesligamento,\r\n" + //
                "           --D.idAdmissao,\r\n" + //
                "           --a.*,\r\n" + //
                "           a.id as idAdmisao,\r\n" + //
                "           a.dataExercicio,\r\n" + //
                "           a.dataFim,\r\n" + //
                "           a.dataInicio,\r\n" + //
                "           a.dataPosse,\r\n" + //
                "           a.matriculaServidor,\r\n" + //
                "           a.numeroEdital,\r\n" + //
                "           a.numeroInscricao,\r\n" + //
                "           a.tipoAdmissao,\r\n" + //
                "           a.idAto as idAtoAdmisao,\r\n" + //
                "           a.idCargo,\r\n" + //
                "           a.idServidor,\r\n" + //
                "           a.idAdmissaoFolha,\r\n" + //
                "\r\n" + //
                "           at.cnpjUgPublicacao,\r\n" + //
                "           at.dataPublicacao,\r\n" + //
                "           at.numeroAto,\r\n" + //
                "           -- at.tipoAto,\r\n" + //
                "\r\n" + //
                "\r\n" + //
                "           case\r\n" + //
                "               WHEN  at.tipoAto = 1 then  'Lei'\r\n" + //
                "               WHEN  at.tipoAto = 2 then  'Decreto'\r\n" + //
                "               WHEN  at.tipoAto = 3 then  'Decreto legislativo'\r\n" + //
                "               WHEN  at.tipoAto = 4 then  'Portaria'\r\n" + //
                "               WHEN  at.tipoAto = 5 then  'Resolu??o'\r\n" + //
                "               WHEN  at.tipoAto = 6 then  'Circular'\r\n" + //
                "               WHEN  at.tipoAto = 7 then  'Despacho'\r\n" + //
                "               WHEN  at.tipoAto = 8 then  'Processo'\r\n" + //
                "               WHEN  at.tipoAto = 9 then  'Sem ato'\r\n" + //
                "               WHEN  at.tipoAto = 10 then  'Ato'\r\n" + //
                "               WHEN  at.tipoAto = 11 then  'Contrato'\r\n" + //
                "               WHEN  at.tipoAto = 12 then  'Emenda Constitucional'\r\n" + //
                "               WHEN  at.tipoAto = 99 then  'Outros'\r\n" + //
                "               end as tipoAto,\r\n" + //
                "           at.veiculoPublicacao,\r\n" + //
                "           rank() over (partition by A.matriculaServidor ,at.numeroAto,AT.tipoAto order by d.id ) posicaoId\r\n" + //
                "    from SICAPAP21..Desligamento D\r\n" + //
                "             join SICAPAP21..Admissao A on D.idAdmissao = A.id\r\n" + //
                "             left join Ato at on D.idAto = at.id\r\n" + //
                ")\r\n" + //
                "\r\n" + //
                "    ,DesligamentoDistintas as (\r\n" + //
                "    select Ma.id,\r\n" + //
                "           idAto,\r\n" + //
                "           Ma.chave,\r\n" + //
                "           -- tipoDesligamento,\r\n" + //
                "           idAdmisao,\r\n" + //
                "           dataExercicio,\r\n" + //
                "           dataFim,\r\n" + //
                "           dataInicio,\r\n" + //
                "           dataPosse,\r\n" + //
                "           matriculaServidor,\r\n" + //
                "           numeroEdital,\r\n" + //
                "           numeroInscricao,\r\n" + //
                "           tipoAdmissao,\r\n" + //
                "           idAtoAdmisao,\r\n" + //
                "           idCargo,\r\n" + //
                "           idServidor,\r\n" + //
                "           idAdmissaoFolha,\r\n" + //
                "           cnpjUgPublicacao,\r\n" + //
                "           dataPublicacao,\r\n" + //
                "           Ma.numeroAto,\r\n" + //
                "           tipoAto,\r\n" + //
                "           veiculoPublicacao,\r\n" + //
                "           posicaoId,\r\n" + //
                "\r\n" + //
                "\r\n" + //
                "           case\r\n" + //
                "            WHEN Ma.tipoDesligamento = 1 then 'Exonera??o'\r\n" + //
                "            WHEN Ma.tipoDesligamento = 2 then 'Aposentadoria'\r\n" + //
                "            WHEN Ma.tipoDesligamento = 3 then 'Posse em outro cargo'\r\n" + //
                "            WHEN Ma.tipoDesligamento = 4 then 'Falecimento'\r\n" + //
                "            WHEN Ma.tipoDesligamento = 5 then 'Rescis?o de contrato'\r\n" + //
                "            WHEN Ma.tipoDesligamento = 6 then 'Demiss?o'\r\n" + //
                "            WHEN Ma.tipoDesligamento = 7 then 'Reserva/Reforma'\r\n" + //
                "            WHEN Ma.tipoDesligamento = 8 then 'Disponibilidade'\r\n" + //
                "             else null\r\n" + //
                "            end as desligamento,\r\n" + //
                "        s.cpfServidor,\r\n" + //
                "           c.nomeCargo,\r\n" + //
                "           'Desligamento' as movimentaca\r\n" + //
                "    from MDesligamento Ma\r\n" + //
                "             left join Servidor  s on Ma.idServidor = s.id\r\n" + //
                "             left join SICAPAP21..cargo c on Ma.idCargo = c.id\r\n" + //
                "    where posicaoId = 1 and S.cpfServidor = :cpf\r\n" + //
                ")\r\n" + //
                "-- select * from DesligamentoDistintas\r\n" + //
                "    ,MReadaptacao as (\r\n" + //
                "    select --D.dataDesligamento,\r\n" + //
                "           D.id,\r\n" + //
                "           --D.tipoDesligamento,\r\n" + //
                "           D.idAto,\r\n" + //
                "           D.chave,\r\n" + //
                "           --D.idAdmissao,\r\n" + //
                "           --a.*,\r\n" + //
                "           a.id as idAdmisao,\r\n" + //
                "           a.dataExercicio,\r\n" + //
                "           a.dataFim,\r\n" + //
                "           a.dataInicio,\r\n" + //
                "           a.dataPosse,\r\n" + //
                "           a.matriculaServidor,\r\n" + //
                "           a.numeroEdital,\r\n" + //
                "           a.numeroInscricao,\r\n" + //
                "           a.tipoAdmissao,\r\n" + //
                "           a.idAto as idAtoAdmisao,\r\n" + //
                "           a.idCargo,\r\n" + //
                "           a.idServidor,\r\n" + //
                "           a.idAdmissaoFolha,\r\n" + //
                "\r\n" + //
                "           at.cnpjUgPublicacao,\r\n" + //
                "           at.dataPublicacao,\r\n" + //
                "           at.numeroAto,\r\n" + //
                "           -- at.tipoAto,\r\n" + //
                "\r\n" + //
                "\r\n" + //
                "           case\r\n" + //
                "               WHEN  at.tipoAto = 1 then  'Lei'\r\n" + //
                "               WHEN  at.tipoAto = 2 then  'Decreto'\r\n" + //
                "               WHEN  at.tipoAto = 3 then  'Decreto legislativo'\r\n" + //
                "               WHEN  at.tipoAto = 4 then  'Portaria'\r\n" + //
                "               WHEN  at.tipoAto = 5 then  'Resolu??o'\r\n" + //
                "               WHEN  at.tipoAto = 6 then  'Circular'\r\n" + //
                "               WHEN  at.tipoAto = 7 then  'Despacho'\r\n" + //
                "               WHEN  at.tipoAto = 8 then  'Processo'\r\n" + //
                "               WHEN  at.tipoAto = 9 then  'Sem ato'\r\n" + //
                "               WHEN  at.tipoAto = 10 then  'Ato'\r\n" + //
                "               WHEN  at.tipoAto = 11 then  'Contrato'\r\n" + //
                "               WHEN  at.tipoAto = 12 then  'Emenda Constitucional'\r\n" + //
                "               WHEN  at.tipoAto = 99 then  'Outros'\r\n" + //
                "               end as tipoAto,\r\n" + //
                "           at.veiculoPublicacao,\r\n" + //
                "           rank() over (partition by A.matriculaServidor ,at.numeroAto,AT.tipoAto order by d.id ) posicaoId\r\n" + //
                "    from SICAPAP21..Readaptacao D\r\n" + //
                "             join SICAPAP21..Admissao A on D.id = A.id\r\n" + //
                "             left join Ato at on D.idAto = at.id\r\n" + //
                ")\r\n" + //
                "\r\n" + //
                "    ,ReadaptacaoDistintas as (\r\n" + //
                "    select Ma.*,\r\n" + //
                "           '' as desligamento,\r\n" + //
                "           s.cpfServidor,\r\n" + //
                "           c.nomeCargo,\r\n" + //
                "           'Readaptacao' as movimentaca\r\n" + //
                "    from MReadaptacao Ma\r\n" + //
                "        left join Servidor  s on Ma.idServidor = s.id\r\n" + //
                "        inner join SICAPAP21..cargo c on Ma.idCargo = c.id\r\n" + //
                "    where posicaoId = 1 and S.cpfServidor = :cpf\r\n" + //
                ")\r\n" + //
                "-- select * from ReadaptacaoDistintas\r\n" + //
                "    ,MReconducao as (\r\n" + //
                "    select --D.dataDesligamento,\r\n" + //
                "           D.id,\r\n" + //
                "           --D.tipoDesligamento,\r\n" + //
                "           D.idAto,\r\n" + //
                "           D.chave,\r\n" + //
                "           --D.idAdmissao,\r\n" + //
                "           --a.*,\r\n" + //
                "           a.id as idAdmisao,\r\n" + //
                "           a.dataExercicio,\r\n" + //
                "           a.dataFim,\r\n" + //
                "           a.dataInicio,\r\n" + //
                "           a.dataPosse,\r\n" + //
                "           a.matriculaServidor,\r\n" + //
                "           a.numeroEdital,\r\n" + //
                "           a.numeroInscricao,\r\n" + //
                "           a.tipoAdmissao,\r\n" + //
                "           a.idAto as idAtoAdmisao,\r\n" + //
                "           a.idCargo,\r\n" + //
                "           a.idServidor,\r\n" + //
                "           a.idAdmissaoFolha,\r\n" + //
                "\r\n" + //
                "           at.cnpjUgPublicacao,\r\n" + //
                "           at.dataPublicacao,\r\n" + //
                "           at.numeroAto,\r\n" + //
                "           -- at.tipoAto,\r\n" + //
                "           case\r\n" + //
                "               WHEN  at.tipoAto = 1 then  'Lei'\r\n" + //
                "               WHEN  at.tipoAto = 2 then  'Decreto'\r\n" + //
                "               WHEN  at.tipoAto = 3 then  'Decreto legislativo'\r\n" + //
                "               WHEN  at.tipoAto = 4 then  'Portaria'\r\n" + //
                "               WHEN  at.tipoAto = 5 then  'Resolu??o'\r\n" + //
                "               WHEN  at.tipoAto = 6 then  'Circular'\r\n" + //
                "               WHEN  at.tipoAto = 7 then  'Despacho'\r\n" + //
                "               WHEN  at.tipoAto = 8 then  'Processo'\r\n" + //
                "               WHEN  at.tipoAto = 9 then  'Sem ato'\r\n" + //
                "               WHEN  at.tipoAto = 10 then  'Ato'\r\n" + //
                "               WHEN  at.tipoAto = 11 then  'Contrato'\r\n" + //
                "               WHEN  at.tipoAto = 12 then  'Emenda Constitucional'\r\n" + //
                "               WHEN  at.tipoAto = 99 then  'Outros'\r\n" + //
                "               end as tipoAto,\r\n" + //
                "           at.veiculoPublicacao,\r\n" + //
                "           rank() over (partition by A.matriculaServidor ,at.numeroAto,AT.tipoAto order by d.id ) posicaoId\r\n" + //
                "    from SICAPAP21..Reconducao D\r\n" + //
                "             join SICAPAP21..Admissao A on D.id = A.id\r\n" + //
                "             left join Ato at on D.idAto = at.id\r\n" + //
                ")\r\n" + //
                "\r\n" + //
                "    ,ReconducaoDistintas as (\r\n" + //
                "    select Ma.*,\r\n" + //
                "           '' as desligamento,\r\n" + //
                "           s.cpfServidor,\r\n" + //
                "           c.nomeCargo,\r\n" + //
                "           'Reconducao' as movimentaca\r\n" + //
                "    from MReconducao Ma\r\n" + //
                "            left join Servidor  s on Ma.idServidor = s.id\r\n" + //
                "            inner join SICAPAP21..cargo c on Ma.idCargo = c.id\r\n" + //
                "    where posicaoId = 1 and S.cpfServidor = :cpf\r\n" + //
                ")\r\n" + //
                "-- select * from ReadaptacaoDistintas\r\n" + //
                "    ,MReintegracao as (\r\n" + //
                "    select --D.dataDesligamento,\r\n" + //
                "           D.id,\r\n" + //
                "           --D.tipoDesligamento,\r\n" + //
                "           D.idAto,\r\n" + //
                "           D.chave,\r\n" + //
                "           --D.idAdmissao,\r\n" + //
                "           --a.*,\r\n" + //
                "           a.id as idAdmisao,\r\n" + //
                "           a.dataExercicio,\r\n" + //
                "           a.dataFim,\r\n" + //
                "           a.dataInicio,\r\n" + //
                "           a.dataPosse,\r\n" + //
                "           a.matriculaServidor,\r\n" + //
                "           a.numeroEdital,\r\n" + //
                "           a.numeroInscricao,\r\n" + //
                "           a.tipoAdmissao,\r\n" + //
                "           a.idAto as idAtoAdmisao,\r\n" + //
                "           a.idCargo,\r\n" + //
                "           a.idServidor,\r\n" + //
                "           a.idAdmissaoFolha,\r\n" + //
                "\r\n" + //
                "           at.cnpjUgPublicacao,\r\n" + //
                "           at.dataPublicacao,\r\n" + //
                "           at.numeroAto,\r\n" + //
                "           -- at.tipoAto,\r\n" + //
                "           case\r\n" + //
                "               WHEN  at.tipoAto = 1 then  'Lei'\r\n" + //
                "               WHEN  at.tipoAto = 2 then  'Decreto'\r\n" + //
                "               WHEN  at.tipoAto = 3 then  'Decreto legislativo'\r\n" + //
                "               WHEN  at.tipoAto = 4 then  'Portaria'\r\n" + //
                "               WHEN  at.tipoAto = 5 then  'Resolu??o'\r\n" + //
                "               WHEN  at.tipoAto = 6 then  'Circular'\r\n" + //
                "               WHEN  at.tipoAto = 7 then  'Despacho'\r\n" + //
                "               WHEN  at.tipoAto = 8 then  'Processo'\r\n" + //
                "               WHEN  at.tipoAto = 9 then  'Sem ato'\r\n" + //
                "               WHEN  at.tipoAto = 10 then  'Ato'\r\n" + //
                "               WHEN  at.tipoAto = 11 then  'Contrato'\r\n" + //
                "               WHEN  at.tipoAto = 12 then  'Emenda Constitucional'\r\n" + //
                "               WHEN  at.tipoAto = 99 then  'Outros'\r\n" + //
                "               end as tipoAto,\r\n" + //
                "           at.veiculoPublicacao,\r\n" + //
                "           rank() over (partition by A.matriculaServidor ,at.numeroAto,AT.tipoAto order by d.id ) posicaoId\r\n" + //
                "    from SICAPAP21..Reintegracao D\r\n" + //
                "             join SICAPAP21..Admissao A on D.id = A.id\r\n" + //
                "             left join Ato at on D.idAto = at.id\r\n" + //
                ")\r\n" + //
                "\r\n" + //
                "    ,ReintegracaoDistintas as (\r\n" + //
                "    select Ma.*,\r\n" + //
                "           '' as desligamento,\r\n" + //
                "           s.cpfServidor,\r\n" + //
                "           c.nomeCargo,\r\n" + //
                "           'ReintegracaoDistintas' as movimentaca\r\n" + //
                "    from MReintegracao Ma\r\n" + //
                "            left join Servidor  s on Ma.idServidor = s.id\r\n" + //
                "            inner join SICAPAP21..cargo c on Ma.idCargo = c.id\r\n" + //
                "    where posicaoId = 1 and S.cpfServidor = :cpf\r\n" + //
                ")\r\n" + //
                "-- select * from ReintegracaoDistintas\r\n" + //
                "    ,MPensao as (\r\n" + //
                "    select --D.dataDesligamento,\r\n" + //
                "           D.id,\r\n" + //
                "           --D.tipoDesligamento,\r\n" + //
                "           D.idAto,\r\n" + //
                "           D.chave,\r\n" + //
                "           --D.idAdmissao,\r\n" + //
                "           --a.*,\r\n" + //
                "           a.id as idAdmisao,\r\n" + //
                "           a.dataExercicio,\r\n" + //
                "           a.dataFim,\r\n" + //
                "           a.dataInicio,\r\n" + //
                "           a.dataPosse,\r\n" + //
                "           a.matriculaServidor,\r\n" + //
                "           a.numeroEdital,\r\n" + //
                "           a.numeroInscricao,\r\n" + //
                "           a.tipoAdmissao,\r\n" + //
                "           a.idAto as idAtoAdmisao,\r\n" + //
                "           a.idCargo,\r\n" + //
                "           a.idServidor,\r\n" + //
                "           a.idAdmissaoFolha,\r\n" + //
                "\r\n" + //
                "           at.cnpjUgPublicacao,\r\n" + //
                "           at.dataPublicacao,\r\n" + //
                "           at.numeroAto,\r\n" + //
                "           -- at.tipoAto,\r\n" + //
                "           case\r\n" + //
                "               WHEN  at.tipoAto = 1 then  'Lei'\r\n" + //
                "               WHEN  at.tipoAto = 2 then  'Decreto'\r\n" + //
                "               WHEN  at.tipoAto = 3 then  'Decreto legislativo'\r\n" + //
                "               WHEN  at.tipoAto = 4 then  'Portaria'\r\n" + //
                "               WHEN  at.tipoAto = 5 then  'Resolu??o'\r\n" + //
                "               WHEN  at.tipoAto = 6 then  'Circular'\r\n" + //
                "               WHEN  at.tipoAto = 7 then  'Despacho'\r\n" + //
                "               WHEN  at.tipoAto = 8 then  'Processo'\r\n" + //
                "               WHEN  at.tipoAto = 9 then  'Sem ato'\r\n" + //
                "               WHEN  at.tipoAto = 10 then  'Ato'\r\n" + //
                "               WHEN  at.tipoAto = 11 then  'Contrato'\r\n" + //
                "               WHEN  at.tipoAto = 12 then  'Emenda Constitucional'\r\n" + //
                "               WHEN  at.tipoAto = 99 then  'Outros'\r\n" + //
                "               end as tipoAto,\r\n" + //
                "           at.veiculoPublicacao,\r\n" + //
                "           rank() over (partition by A.matriculaServidor ,at.numeroAto,AT.tipoAto order by d.id ) posicaoId\r\n" + //
                "    from SICAPAP21..Pensao D\r\n" + //
                "             join SICAPAP21..Admissao A on D.id = A.id\r\n" + //
                "             left join Ato at on D.idAto = at.id\r\n" + //
                ")\r\n" + //
                "\r\n" + //
                "    ,PensaoDistintas as (\r\n" + //
                "    select Ma.*,\r\n" + //
                "           '' as desligamento,\r\n" + //
                "           s.cpfServidor,\r\n" + //
                "           c.nomeCargo,\r\n" + //
                "           'Pensao'as movimentaca\r\n" + //
                "    from MPensao Ma\r\n" + //
                "            left join Servidor  s on Ma.idServidor = s.id\r\n" + //
                "            inner join SICAPAP21..cargo c on Ma.idCargo = c.id\r\n" + //
                "    where posicaoId = 1 and S.cpfServidor = :cpf\r\n" + //
                ")\r\n" + //
                "-- select * from PensaoDistintas\r\n" + //
                "    ,MLicenca as (\r\n" + //
                "    select --D.dataDesligamento,\r\n" + //
                "           D.id,\r\n" + //
                "           --D.tipoDesligamento,\r\n" + //
                "           D.idAto,\r\n" + //
                "           D.chave,\r\n" + //
                "           --D.idAdmissao,\r\n" + //
                "           --a.*,\r\n" + //
                "           a.id as idAdmisao,\r\n" + //
                "           a.dataExercicio,\r\n" + //
                "           a.dataFim,\r\n" + //
                "           a.dataInicio,\r\n" + //
                "           a.dataPosse,\r\n" + //
                "           a.matriculaServidor,\r\n" + //
                "           a.numeroEdital,\r\n" + //
                "           a.numeroInscricao,\r\n" + //
                "           a.tipoAdmissao,\r\n" + //
                "           a.idAto as idAtoAdmisao,\r\n" + //
                "           a.idCargo,\r\n" + //
                "           a.idServidor,\r\n" + //
                "           a.idAdmissaoFolha,\r\n" + //
                "\r\n" + //
                "           at.cnpjUgPublicacao,\r\n" + //
                "           at.dataPublicacao,\r\n" + //
                "           at.numeroAto,\r\n" + //
                "           -- at.tipoAto,\r\n" + //
                "           case\r\n" + //
                "               WHEN  at.tipoAto = 1 then  'Lei'\r\n" + //
                "               WHEN  at.tipoAto = 2 then  'Decreto'\r\n" + //
                "               WHEN  at.tipoAto = 3 then  'Decreto legislativo'\r\n" + //
                "               WHEN  at.tipoAto = 4 then  'Portaria'\r\n" + //
                "               WHEN  at.tipoAto = 5 then  'Resolu??o'\r\n" + //
                "               WHEN  at.tipoAto = 6 then  'Circular'\r\n" + //
                "               WHEN  at.tipoAto = 7 then  'Despacho'\r\n" + //
                "               WHEN  at.tipoAto = 8 then  'Processo'\r\n" + //
                "               WHEN  at.tipoAto = 9 then  'Sem ato'\r\n" + //
                "               WHEN  at.tipoAto = 10 then  'Ato'\r\n" + //
                "               WHEN  at.tipoAto = 11 then  'Contrato'\r\n" + //
                "               WHEN  at.tipoAto = 12 then  'Emenda Constitucional'\r\n" + //
                "               WHEN  at.tipoAto = 99 then  'Outros'\r\n" + //
                "               end as tipoAto,\r\n" + //
                "           at.veiculoPublicacao,\r\n" + //
                "           rank() over (partition by A.matriculaServidor ,at.numeroAto,AT.tipoAto order by d.id ) posicaoId\r\n" + //
                "    from SICAPAP21..Licenca D\r\n" + //
                "             join SICAPAP21..Admissao A on D.idAdmissao = A.id\r\n" + //
                "             left join Ato at on D.idAto = at.id\r\n" + //
                ")\r\n" + //
                "    ,LicencaDistintas as (\r\n" + //
                "    select Ma.*,\r\n" + //
                "           '' as desligamento,\r\n" + //
                "           s.cpfServidor,\r\n" + //
                "           c.nomeCargo,\r\n" + //
                "           'Licenca' as movimentaca\r\n" + //
                "    from MLicenca Ma\r\n" + //
                "            left join Servidor  s on Ma.idServidor = s.id\r\n" + //
                "            inner join SICAPAP21..cargo c on Ma.idCargo = c.id\r\n" + //
                "    where posicaoId = 1 and S.cpfServidor = :cpf\r\n" + //
                ")\r\n" + //
                "--select * from LicencaDistintas;\r\n" + //
                "    ,MDesignacaoDeFuncao as (\r\n" + //
                "    select --D.dataDesligamento,\r\n" + //
                "           D.id,\r\n" + //
                "           --D.tipoDesligamento,\r\n" + //
                "           D.idAto,\r\n" + //
                "           D.chave,\r\n" + //
                "           --D.idAdmissao,\r\n" + //
                "           --a.*,\r\n" + //
                "           a.id as idAdmisao,\r\n" + //
                "           a.dataExercicio,\r\n" + //
                "           a.dataFim,\r\n" + //
                "           a.dataInicio,\r\n" + //
                "           a.dataPosse,\r\n" + //
                "           a.matriculaServidor,\r\n" + //
                "           a.numeroEdital,\r\n" + //
                "           a.numeroInscricao,\r\n" + //
                "           a.tipoAdmissao,\r\n" + //
                "           a.idAto as idAtoAdmisao,\r\n" + //
                "           a.idCargo,\r\n" + //
                "           a.idServidor,\r\n" + //
                "           a.idAdmissaoFolha,\r\n" + //
                "\r\n" + //
                "           at.cnpjUgPublicacao,\r\n" + //
                "           at.dataPublicacao,\r\n" + //
                "           at.numeroAto,\r\n" + //
                "           -- at.tipoAto,\r\n" + //
                "           case\r\n" + //
                "               WHEN  at.tipoAto = 1 then  'Lei'\r\n" + //
                "               WHEN  at.tipoAto = 2 then  'Decreto'\r\n" + //
                "               WHEN  at.tipoAto = 3 then  'Decreto legislativo'\r\n" + //
                "               WHEN  at.tipoAto = 4 then  'Portaria'\r\n" + //
                "               WHEN  at.tipoAto = 5 then  'Resolu??o'\r\n" + //
                "               WHEN  at.tipoAto = 6 then  'Circular'\r\n" + //
                "               WHEN  at.tipoAto = 7 then  'Despacho'\r\n" + //
                "               WHEN  at.tipoAto = 8 then  'Processo'\r\n" + //
                "               WHEN  at.tipoAto = 9 then  'Sem ato'\r\n" + //
                "               WHEN  at.tipoAto = 10 then  'Ato'\r\n" + //
                "               WHEN  at.tipoAto = 11 then  'Contrato'\r\n" + //
                "               WHEN  at.tipoAto = 12 then  'Emenda Constitucional'\r\n" + //
                "               WHEN  at.tipoAto = 99 then  'Outros'\r\n" + //
                "               end as tipoAto,\r\n" + //
                "           at.veiculoPublicacao,\r\n" + //
                "           rank() over (partition by A.matriculaServidor ,at.numeroAto,AT.tipoAto order by d.id ) posicaoId\r\n" + //
                "    from SICAPAP21..DesignacaoFuncao D\r\n" + //
                "             join SICAPAP21..Admissao A on D.idAdmissao = A.id\r\n" + //
                "             left join Ato at on D.idAto = at.id\r\n" + //
                ")\r\n" + //
                "\r\n" + //
                "    ,DesignacaoDeFuncaoDistintas as (\r\n" + //
                "    select Ma.*,\r\n" + //
                "           '' as desligamento,\r\n" + //
                "           s.cpfServidor,\r\n" + //
                "           c.nomeCargo,\r\n" + //
                "           'DesignacaoDeFuncao' as movimentaca\r\n" + //
                "    from MDesignacaoDeFuncao Ma\r\n" + //
                "            left join Servidor  s on Ma.idServidor = s.id\r\n" + //
                "            inner join SICAPAP21..cargo c on Ma.idCargo = c.id\r\n" + //
                "    where posicaoId = 1 and S.cpfServidor = :cpf\r\n" + //
                ")\r\n" + //
                "\r\n" + //
                "    ,MCessao as (\r\n" + //
                "    select --D.dataDesligamento,\r\n" + //
                "           D.id,\r\n" + //
                "           --D.tipoDesligamento,\r\n" + //
                "           D.idAto,\r\n" + //
                "           D.chave,\r\n" + //
                "           --D.idAdmissao,\r\n" + //
                "           --a.*,\r\n" + //
                "           a.id as idAdmisao,\r\n" + //
                "           a.dataExercicio,\r\n" + //
                "           a.dataFim,\r\n" + //
                "           a.dataInicio,\r\n" + //
                "           a.dataPosse,\r\n" + //
                "           a.matriculaServidor,\r\n" + //
                "           a.numeroEdital,\r\n" + //
                "           a.numeroInscricao,\r\n" + //
                "           a.tipoAdmissao,\r\n" + //
                "           a.idAto as idAtoAdmisao,\r\n" + //
                "           a.idCargo,\r\n" + //
                "           a.idServidor,\r\n" + //
                "           a.idAdmissaoFolha,\r\n" + //
                "\r\n" + //
                "           at.cnpjUgPublicacao,\r\n" + //
                "           at.dataPublicacao,\r\n" + //
                "           at.numeroAto,\r\n" + //
                "           -- at.tipoAto,\r\n" + //
                "           case\r\n" + //
                "               WHEN  at.tipoAto = 1 then  'Lei'\r\n" + //
                "               WHEN  at.tipoAto = 2 then  'Decreto'\r\n" + //
                "               WHEN  at.tipoAto = 3 then  'Decreto legislativo'\r\n" + //
                "               WHEN  at.tipoAto = 4 then  'Portaria'\r\n" + //
                "               WHEN  at.tipoAto = 5 then  'Resolu??o'\r\n" + //
                "               WHEN  at.tipoAto = 6 then  'Circular'\r\n" + //
                "               WHEN  at.tipoAto = 7 then  'Despacho'\r\n" + //
                "               WHEN  at.tipoAto = 8 then  'Processo'\r\n" + //
                "               WHEN  at.tipoAto = 9 then  'Sem ato'\r\n" + //
                "               WHEN  at.tipoAto = 10 then  'Ato'\r\n" + //
                "               WHEN  at.tipoAto = 11 then  'Contrato'\r\n" + //
                "               WHEN  at.tipoAto = 12 then  'Emenda Constitucional'\r\n" + //
                "               WHEN  at.tipoAto = 99 then  'Outros'\r\n" + //
                "               end as tipoAto,\r\n" + //
                "           at.veiculoPublicacao,\r\n" + //
                "           rank() over (partition by A.matriculaServidor ,at.numeroAto,AT.tipoAto order by d.id ) posicaoId\r\n" + //
                "    from SICAPAP21..Cessao D\r\n" + //
                "             join SICAPAP21..Admissao A on D.idAdmissao = A.id\r\n" + //
                "             left join Ato at on D.idAto = at.id\r\n" + //
                ")\r\n" + //
                "\r\n" + //
                "    ,CesaoDistintas as (\r\n" + //
                "    select Ma.*,\r\n" + //
                "           '' as desligamento,\r\n" + //
                "           s.cpfServidor,\r\n" + //
                "           c.nomeCargo,\r\n" + //
                "           'Cesao' as movimentaca\r\n" + //
                "    from MCessao Ma\r\n" + //
                "            left  join Servidor  s on Ma.idServidor = s.id\r\n" + //
                "            inner join SICAPAP21..cargo c on Ma.idCargo = c.id\r\n" + //
                "    where posicaoId = 1 and S.cpfServidor = :cpf\r\n" + //
                ")\r\n" + //
                "\r\n" + //
                "    ,MAproveitamento as (\r\n" + //
                "    select --D.dataDesligamento,\r\n" + //
                "           D.id,\r\n" + //
                "           --D.tipoDesligamento,\r\n" + //
                "           D.idAto,\r\n" + //
                "           D.chave,\r\n" + //
                "           --D.idAdmissao,\r\n" + //
                "           --a.*,\r\n" + //
                "           a.id as idAdmisao,\r\n" + //
                "           a.dataExercicio,\r\n" + //
                "           a.dataFim,\r\n" + //
                "           a.dataInicio,\r\n" + //
                "           a.dataPosse,\r\n" + //
                "           a.matriculaServidor,\r\n" + //
                "           a.numeroEdital,\r\n" + //
                "           a.numeroInscricao,\r\n" + //
                "           a.tipoAdmissao,\r\n" + //
                "           a.idAto as idAtoAdmisao,\r\n" + //
                "           a.idCargo,\r\n" + //
                "           a.idServidor,\r\n" + //
                "           a.idAdmissaoFolha,\r\n" + //
                "\r\n" + //
                "           at.cnpjUgPublicacao,\r\n" + //
                "           at.dataPublicacao,\r\n" + //
                "           at.numeroAto,\r\n" + //
                "           -- at.tipoAto,\r\n" + //
                "           case\r\n" + //
                "               WHEN  at.tipoAto = 1 then  'Lei'\r\n" + //
                "               WHEN  at.tipoAto = 2 then  'Decreto'\r\n" + //
                "               WHEN  at.tipoAto = 3 then  'Decreto legislativo'\r\n" + //
                "               WHEN  at.tipoAto = 4 then  'Portaria'\r\n" + //
                "               WHEN  at.tipoAto = 5 then  'Resolu??o'\r\n" + //
                "               WHEN  at.tipoAto = 6 then  'Circular'\r\n" + //
                "               WHEN  at.tipoAto = 7 then  'Despacho'\r\n" + //
                "               WHEN  at.tipoAto = 8 then  'Processo'\r\n" + //
                "               WHEN  at.tipoAto = 9 then  'Sem ato'\r\n" + //
                "               WHEN  at.tipoAto = 10 then  'Ato'\r\n" + //
                "               WHEN  at.tipoAto = 11 then  'Contrato'\r\n" + //
                "               WHEN  at.tipoAto = 12 then  'Emenda Constitucional'\r\n" + //
                "               WHEN  at.tipoAto = 99 then  'Outros'\r\n" + //
                "               end as tipoAto,\r\n" + //
                "           at.veiculoPublicacao,\r\n" + //
                "           rank() over (partition by A.matriculaServidor ,at.numeroAto,AT.tipoAto order by d.id ) posicaoId\r\n" + //
                "    from SICAPAP21..Aproveitamento D\r\n" + //
                "             join SICAPAP21..Admissao A on D.id = A.id\r\n" + //
                "             left join Ato at on D.idAto = at.id\r\n" + //
                ")\r\n" + //
                "\r\n" + //
                "    ,AproveitamentoDistintos as (\r\n" + //
                "    select Ma.*,\r\n" + //
                "           '' as desligamento,\r\n" + //
                "           s.cpfServidor,\r\n" + //
                "           c.nomeCargo,\r\n" + //
                "           'Aproveitamento' as movimentaca\r\n" + //
                "    from MAproveitamento Ma\r\n" + //
                "            left  join Servidor  s on Ma.idServidor = s.id\r\n" + //
                "            inner join SICAPAP21..cargo c on Ma.idCargo = c.id\r\n" + //
                "    where posicaoId = 1 and S.cpfServidor = :cpf\r\n" + //
                ")\r\n" + //
                "\r\n" + //
                "   , movimentacoes as (\r\n" + //
                "    SELECT * from AdimisoeDistintas\r\n" + //
                "    Union all\r\n" + //
                "    select * from DesligamentoDistintas\r\n" + //
                "    Union All\r\n" + //
                "    select * from ReadaptacaoDistintas\r\n" + //
                "    Union All\r\n" + //
                "    select * from ReconducaoDistintas\r\n" + //
                "    Union All\r\n" + //
                "    select * from ReintegracaoDistintas\r\n" + //
                "    Union All\r\n" + //
                "    select * from AposentadoriaDistintas\r\n" + //
                "    Union All\r\n" + //
                "    select * from PensaoDistintas\r\n" + //
                "    Union All\r\n" + //
                "    select * from LicencaDistintas\r\n" + //
                "    Union All\r\n" + //
                "    select * from DesignacaoDeFuncaoDistintas\r\n" + //
                "    Union All\r\n" + //
                "    select * from CesaoDistintas\r\n" + //
                "    Union All\r\n" + //
                "    select * from AproveitamentoDistintos\r\n" + //
                "         )\r\n" + //
                "select M.* ,\r\n" + //
                "       I.nomeUnidade,\r\n" + //
                "       1 AS count\r\n" + //
                "from movimentacoes M\r\n" + //
                "    inner join SICAPAP21..InfoRemessa i on i.chave = M.chave\r\n" + //
                "where cpfServidor like :cpf order by matriculaServidor , exercicio ,remessa")
                .setParameter("cpf", cpf);
        return queryHistoricoDeVinculo;
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
