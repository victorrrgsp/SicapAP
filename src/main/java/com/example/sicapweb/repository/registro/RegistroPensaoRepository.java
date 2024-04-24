package com.example.sicapweb.repository.registro;

import br.gov.to.tce.model.ap.registro.RegistroPensao;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.util.StaticMethods;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Objects;

@Repository
public class RegistroPensaoRepository  extends DefaultRepository<RegistroPensao, BigInteger> {


    public RegistroPensaoRepository(EntityManager em) {
        super(em);
    }

    public String getSearch(String nome, String cpf) {
        String search = "";
        //monta pesquisa search

        if (nome !=null && !nome.isEmpty()){
            search=" where s.nome like '%"+nome+"%'";
        } else if (cpf !=null && !cpf.isEmpty()) {
            search=" where a.cpfServidor  = '"+cpf+"'";
        }
        return search;
    }

    public String getSearch(HashMap<String,String> filtros) {
        String search = "";
        //monta pesquisa search
        String nome= filtros.get("nome");
        String cpf= filtros.get("cpf");

        if (nome !=null && !nome.isEmpty()){
            search=" where s.nome like '%"+nome+"%'";
        } else if (cpf !=null && !cpf.isEmpty()) {
            search=" where a.cpfServidor like '%"+cpf.trim()+"%'";
        }
        return search;
    }

    public PaginacaoUtil<HashMap<String, Object>> getMovimentosPensaoParaRegistrar(Pageable pageable, HashMap<String,String> filtros){
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());

        String whereStatemente =getSearch(filtros);
        //mas adequando seria filtrar pelo processo enviado -- futura alteracao 
        Query queryMovimentos = getEntityManager().createNativeQuery(
                "with envios as (select cast(substring(processo, 1, len(processo) - 5) as int)             numeroProcesso,\n" +
                        "                       cast(substring(processo, len(processo) - 3, len(processo)) as int) anoProcesso,\n" +
                        "                       status,\n" +
                        "                       a.idMovimentacao\n" +
                        "                from AdmEnvio a\n" +
                        "                where status = 4),\n" +
                        "     pensaoEnvios as (select b.numeroProcesso, b.anoProcesso, a.*, pen.cpfPensionista\n" +
                        "                      from Pensao a\n" +
                        "                                join envios b on a.id = b.idMovimentacao\n" +
                        "                                join Pensionista pen on pen.cpfServidor = a.cpfServidor\n" +
                        "                    ),\n" +
                        "     pens as (select i.idUnidadeGestora, a.*\n" +
                        "              from pensaoEnvios a\n" +
                        "                       join InfoRemessa i on a.chave = i.chave and i.idUnidadeGestora = :ug\n" +
                        "              where not exists(select 1 from RegistroPensao where idPensao = a.id))\n" +
                        "select distinct\n" +
                        "        a.id               as idMovimentacao,\n" +
                        "       s.cpfServidor      as cpfServidor,\n" +
                        "       s.nome             as nome,\n" +
                        "       'PENSÂO'           AS tipoMovimentacao,\n" +
                        "       a.numeroProcesso   as numeroProcesso,\n" +
                        "       a.anoProcesso      as anoProcesso,\n" +
                        "       a.dataObito        as dataMovimentacao,\n" +
                        "       c.nomeCargo        as nomeCargo,\n" +
                        "       c.codigoCargo      as codigoCargo,\n" +
                        "       at.numeroAto       as numeroAto,\n" +
                        "       at.tipoAto         as tipoAto,\n" +
                        "       a.idUnidadeGestora as idUnidadeGestora,\n" +
                        "       at.dataPublicacao  as dataAto\n" +
                        "from pens a\n" +
                        "         join Ato at on a.idAto = at.id\n" +
                        "         join Admissao ad on a.id = ad.id\n" +
                        "         join Cargo c on ad.idCargo = c.id\n" +
                        "         join Servidor s on ad.idServidor = s.id\n"+whereStatemente )
                .setParameter("ug", filtros.get("ug"));

        long totalRegistros = queryMovimentos.getResultList().size();
        int tamanhoPorPagina =  (whereStatemente.isEmpty())? Integer.valueOf(pageable.getPageSize()) : (int) totalRegistros ;
        tamanhoPorPagina = tamanhoPorPagina == 0 ? 1:tamanhoPorPagina;
        long totalPaginas = (totalRegistros + (tamanhoPorPagina - 1)) / tamanhoPorPagina;
        if (totalRegistros > tamanho  && whereStatemente.isEmpty() ){
            queryMovimentos.setFirstResult(pagina).setMaxResults(tamanho);
        }
        try{
            return new PaginacaoUtil<>( tamanhoPorPagina, pagina, totalPaginas, totalRegistros, StaticMethods.getHashmapFromQuery(queryMovimentos) );
        } catch (RuntimeException e ){
            throw  new RuntimeException("Problema ao consultar os movimentos para registro!!");
        }
    }

    public Integer countMovimentosPensaoParaRegistrar(String unidadeGestora) {
        try{
            Query queryQuantidade = getEntityManager().createNativeQuery(
                    "with envios as (\n" +
                            "    select cast( substring(processo,1,len(processo)-5) as int) numeroProcesso, cast( substring(processo,len(processo)-3,len(processo) )  as int) anoProcesso, status,a.idMovimentacao from AdmEnvio a\n" +
                            "), pensaoEnvios as (select b.numeroProcesso, b.anoProcesso,a.* from Pensao a  join envios b on a.id=b.idMovimentacao)"+
                            " , pens as (select count(1) ct " +
                                    "               from pensaoEnvios a " +
                                    "                        join InfoRemessa i on a.chave = i.chave and i.idUnidadeGestora = :ug join AdmEnvio b on a.id = b.idMovimentacao  and b.status=4 where not exists(select 1 from RegistroPensao where idPensao = a.id   ) )\n" +
                                    " select ct  " +
                                    " from pens a")
                    .setParameter("ug", unidadeGestora);
            return (Integer) queryQuantidade.getSingleResult();
        }catch (RuntimeException e){
            throw  new RuntimeException("Problema ao consultar os movimentos para registro!!");
        }
    }

    public PaginacaoUtil<HashMap<String, Object>> getRegistrosPensao(Pageable pageable,HashMap<String,String> filtro){
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        // String whereStatemente =getSearch(FiltroNome, filtroCpf);

        Query queryRegistroMovimentos = getEntityManager().createNativeQuery("" +
                        " with registros as (select id, idPensao ,dataAtoDecisao,dataCadastro, numeroAnoProcesso, numeroAtoDecisao, tipoAtoDecisao, observacao" +
                        "                   from RegistroPensao " +
                        "                   where idUnidadeGestora = :ug and  (:cpfServidor is null or    :cpfServidor = cpfServidor)  and dataCadastro  between  cast(:dtini as date) and cast(:dtfim as date) \n" +
                        "                    ) " +
                        "select a.id                as idRegistro," +
                        "       idPensao     as idMovimentacao,\n" +
                        "       numeroAnoProcesso   as numeroAnoProcesso,\n" +
                        "       dataAtoDecisao   as dataAtoDecisao,\n" +
                        "       numeroAtoDecisao    as numeroAtoDecisao,\n" +
                        "       tipoAtoDecisao      as tipoAtoDecisao,\n" +
                        "       a.observacao          as observacao,\n" +
                        "       s.cpfServidor       as cpfServidor,\n" +
                        "       s.nome              as nome,\n" +
                        "       'PENSÃO'     AS tipoMovimentacao,\n" +
                        "       b.dataObito as dataMovimentacao,\n" +
                        "       c.nomeCargo         as nomeCargo,\n" +
                        "       at.numeroAto        as numeroAto,\n" +
                        "       a.dataCadastro          as dataCadastro,\n" +
                        "       at.tipoAto          as tipoAto,\n" +
                        "       at.dataPublicacao   as dataAto " +
                        "from registros a " +
                        "         join Pensao b on a.idPensao=b.id " +
                        "         join Ato at on b.idAto = at.id " +
                        "         join Admissao ad on b.id = ad.id\n" +
                        "         join Cargo c on ad.idCargo = c.id\n" +
                        "         join Servidor s on ad.idServidor = s.id   " )
                .setParameter("ug", filtro.get("ug"))
                .setParameter("dtini", filtro.get("dataInicio"))
                .setParameter("dtfim", filtro.get("dataFim"))
                .setParameter("cpfServidor", Objects.requireNonNullElse(filtro.get("cpf"),"").isEmpty() ? null:filtro.get("cpf"))
                ;

        long totalRegistros = countRegistrosPensao(filtro);
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
           if (totalRegistros > tamanho   ){
        queryRegistroMovimentos.setFirstResult(pagina).setMaxResults(tamanho);
           }
        try{
            return new PaginacaoUtil<>(tamanho, pagina, totalPaginas, totalRegistros, StaticMethods.getHashmapFromQuery(queryRegistroMovimentos));
        } catch (RuntimeException e ){
            throw  new RuntimeException("Problema ao consultar os registros!!");
        }
    }

    public Integer countRegistrosPensao(HashMap<String, String> filtros) {
        try{
            Query queryQuantidade = getEntityManager().createNativeQuery(
                            " with registros " +
                                    "as (select count(1) ct   " +
                                    "                   from RegistroPensao " +
                                    "                   where idUnidadeGestora = :ug and cpfUsuarioCadastro=:cpfUsuario   and  (:cpfServidor is null or    :cpfServidor = cpfServidor) and dataCadastro  between  cast(:dtini as date) and cast(:dtfim as date) )  " +
                                    " select ct  " +
                                    " from registros a")
                    .setParameter("ug", filtros.get("ug"))
                    .setParameter("cpfUsuario", filtros.get("cpfUsuario"))
                    .setParameter("dtini", filtros.get("dataInicio"))
                    .setParameter("dtfim", filtros.get("dataFim"))
                    .setParameter("cpfServidor",  Objects.requireNonNullElse(filtros.get("cpf"),"").isEmpty() ? null:filtros.get("cpf") )
                    ;
            return (Integer) queryQuantidade.getSingleResult();
        }catch (RuntimeException e){
            throw  new RuntimeException("Problema ao consultar os registros!!");
        }
    }

}
