package com.example.sicapweb.repository.registro;

import br.gov.to.tce.model.ap.registro.RegistroAdmissao;
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
public class RegistroAdmissaoRepository  extends DefaultRepository<RegistroAdmissao, BigInteger> {

    public RegistroAdmissaoRepository(EntityManager em) {
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
            search=" where s.nome like '%"+nome.trim()+"%'";
        } else if (cpf !=null && !cpf.isEmpty()) {
            search=" where s.cpfServidor  = '"+cpf.trim()+"'";
        }
        return search;
    }

    public PaginacaoUtil<HashMap<String, Object>> getMovimentosAdmissaoParaRegistrar(Pageable pageable, HashMap<String,String> filtros ){
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String whereStatemente =getSearch(filtros);
        Query queryMovimentos = getEntityManager().createNativeQuery("" +
                        " with envios as (select cast(substring(processo, 1, len(processo) - 5) as int)             numeroProcesso, " +
                        "                       cast(substring(processo, len(processo) - 3, len(processo)) as int) anoProcesso, " +
                        "                       idAdmissao idMovimentacao " +
                        "                from AdmissaoEnvio a\n" +
                        "                         join DocumentoAdmissao b on a.id = b.idEnvio " +
                        "                where a.status = 3 " +
                        "                  and b.status <> 0), " +
                        " admissaoEnvios as (select b.numeroProcesso, b.anoProcesso,a.* from Admissao a  join envios b on a.id=b.idMovimentacao) "+
                        " ,adm as (select i.idUnidadeGestora,a.* " +
                        "               from admissaoEnvios a " +
                        "                        join InfoRemessa i on a.chave = i.chave and i.idUnidadeGestora = :ug and a.tipoAdmissao=1  where not exists(select 1 from RegistroAdmissao where idAdmissao = a.id ) ) " +
                        " select a.id as idMovimentacao ,   " +

                        "        s.nome as nome, " +
                        "       s.cpfServidor as cpfServidor, " +
                        "        'ADMISSÃO'     AS tipoMovimentacao, " +
                        "        a.dataInicio as dataMovimentacao, " +
                        "        a.numeroProcesso as numeroProcesso, " +
                        "        a.anoProcesso as anoProcesso, " +
                        "        c.nomeCargo         as nomeCargo, " +
                        "        o.numeroAto        as numeroAto, " +
                        "        o.tipoAto          as tipoAto, " +
                        "        a.idUnidadeGestora          as idUnidadeGestora, " +
                        "        o.dataPublicacao   as dataAto " +
                        " from adm a " +
                        "          join Ato o on a.idAto = o.id " +
                        "          join Cargo c on a.idCargo = c.id " +
                        "          join Servidor s on a.idServidor = s.id "+whereStatemente )
                .setParameter("ug", filtros.get("ug"));

        long totalRegistros = countMovimentosAdmissaoParaRegistrar(filtros.get("ug"));
        int tamanhoPorPagina =  (whereStatemente.isEmpty())? Integer.valueOf(pageable.getPageSize()) : (int) totalRegistros ;
        tamanhoPorPagina = tamanhoPorPagina == 0 ? 1:tamanhoPorPagina;
        long totalPaginas = (totalRegistros + (tamanhoPorPagina - 1)) / tamanhoPorPagina;
        //desabilita paginacao caso tenho filtro
        if ( whereStatemente.isEmpty() && totalRegistros > tamanho ){
            queryMovimentos.setFirstResult(pagina).setMaxResults(tamanho);
        }
        try{
            return new PaginacaoUtil<>(tamanhoPorPagina, pagina, totalPaginas, totalRegistros, StaticMethods.getHashmapFromQuery(queryMovimentos));
        } catch (RuntimeException e ){
            throw  new RuntimeException("Problema ao consultar os movimentos para registro!!");
        }
    }

    public Integer countMovimentosAdmissaoParaRegistrar(String unidadeGestora) {
        try{
            Query queryQuantidade = getEntityManager().createNativeQuery(
                            " with envios as (select cast(substring(processo, 1, len(processo) - 5) as int)             numeroProcesso, " +
                                    "                       cast(substring(processo, len(processo) - 3, len(processo)) as int) anoProcesso, " +
                                    "                       idAdmissao idMovimentacao " +
                                    "                from AdmissaoEnvio a " +
                                    "                         join DocumentoAdmissao b on a.id = b.idEnvio " +
                                    "                where a.status = 3 " +
                                    "                  and b.status <> 0), " +
                                    "  admissaoEnvios as (select b.numeroProcesso, b.anoProcesso,a.* from Admissao a  join envios b on a.id=b.idMovimentacao) "+
                                    " ,adm as (select count(1) ct " +
                                    "               from admissaoEnvios a " +
                                    "                        join InfoRemessa i on a.chave = i.chave and i.idUnidadeGestora = :ug and a.tipoAdmissao=1   where not exists(select 1 from RegistroAdmissao where idAdmissao = a.id   ) ) " +
                                    " select ct  " +
                                    " from adm a")
                    .setParameter("ug", unidadeGestora);
            return (Integer) queryQuantidade.getSingleResult();
        }catch (RuntimeException e){
            throw  new RuntimeException("Problema ao consultar os movimentos para registro!!");
        }
    }

    public PaginacaoUtil<HashMap<String, Object>> getRegistrosAdmissao(Pageable pageable,HashMap<String,String> filtro){
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
         String whereStatemente =getSearch(filtro);

        Query queryRegistroMovimentos = getEntityManager().createNativeQuery("" +
                        " with   registros as (select id, idAdmissao ,dataCadastro,dataAtoDecisao, numeroAnoProcesso, numeroAtoDecisao, tipoAtoDecisao, observacao\n" +
                        "                   from RegistroAdmissao " +
                        "                   where idUnidadeGestora = :ug and  (:cpfServidor is null or    :cpfServidor = cpfServidor) and cpfUsuarioCadastro=:cpfUsuario and dataCadastro  between  cast(:dtini as date) and cast(:dtfim as date)  " +
                        "                     ) " +
                        "select a.id                as idRegistro,\n" +
                        "       idAdmissao     as idMovimentacao,\n" +
                        "       numeroAnoProcesso   as numeroAnoProcesso,\n" +
                        "       dataAtoDecisao   as dataAtoDecisao,\n" +
                        "       numeroAtoDecisao    as numeroAtoDecisao,\n" +
                        "       tipoAtoDecisao      as tipoAtoDecisao,\n" +
                        "       a.observacao          as observacao,\n" +
                        "       s.cpfServidor       as cpfServidor,\n" +
                        "       s.nome              as nome,\n" +
                        "       'ADMISSÃO'     AS tipoMovimentacao,\n" +
                        "       b.dataExercicio as dataMovimentacao,\n" +
                        "       c.nomeCargo         as nomeCargo,\n" +
                        "       o.numeroAto        as numeroAto,\n" +
                        "       o.tipoAto          as tipoAto,\n" +
                        "       a.dataCadastro          as dataCadastro,\n" +
                        "       o.dataPublicacao   as dataAto " +
                        "from registros a " +
                        "         join Admissao b on a.idAdmissao=b.id " +
                        "         join Ato o on b.idAto = o.id " +
                        "         join Cargo c on b.idCargo = c.id " +
                        "         join Servidor s on b.idServidor = s.id    " )
                .setParameter("ug", filtro.get("ug"))
                .setParameter("cpfUsuario", filtro.get("cpfUsuario"))
                .setParameter("dtini", filtro.get("dataInicio"))
                .setParameter("dtfim", filtro.get("dataFim"))
                .setParameter("cpfServidor", Objects.requireNonNullElse(filtro.get("cpf"),"").isEmpty() ? null:filtro.get("cpf"))
                ;

        long totalRegistros = countRegistrosPensao(filtro);
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;

        if (totalRegistros > tamanho  && whereStatemente.isEmpty() ){
            queryRegistroMovimentos.setFirstResult(pagina).setMaxResults(tamanho);
        }

        try{
            return new PaginacaoUtil<>(tamanho, pagina, totalPaginas, totalRegistros, StaticMethods.getHashmapFromQuery(queryRegistroMovimentos));
        } catch (RuntimeException e ){
            throw  new RuntimeException("Problema ao consultar os registros!!");
        }
    }


    public Integer countRegistrosPensao(HashMap<String,String> filtros) {
        try{
            Query queryQuantidade = getEntityManager().createNativeQuery(
                            " with registros " +
                                    "as (select count(1) ct   " +
                                    "                   from RegistroAdmissao " +
                                    "                   where idUnidadeGestora = :ug and cpfUsuarioCadastro=:cpfUsuario  and  (:cpfServidor is null or    :cpfServidor = cpfServidor)  and  dataCadastro  between  cast(:dtini as date) and cast(:dtfim as date)  \n" +
                                    "     )" +
                                    " select ct  " +
                                    " from registros a")
                    .setParameter("ug", filtros.get("ug"))
                    .setParameter("cpfUsuario", filtros.get("cpfUsuario"))
                    .setParameter("dtini", filtros.get("dataInicio"))
                    .setParameter("dtfim", filtros.get("dataFim"))
                    .setParameter("cpfServidor", Objects.requireNonNullElse(filtros.get("cpf"),"").isEmpty() ? null:filtros.get("cpf"));
            return (Integer) queryQuantidade.getSingleResult();
        }catch (RuntimeException e){
            throw  new RuntimeException("Problema ao consultar os registros!!");
        }
    }

}
