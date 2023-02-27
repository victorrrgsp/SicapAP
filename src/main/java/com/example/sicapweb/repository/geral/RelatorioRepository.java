package com.example.sicapweb.repository.geral;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;

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
}
