package com.example.sicapweb.repository.registro;


import br.gov.to.tce.model.ap.registro.Registro;
import br.gov.to.tce.model.ap.registro.RegistroAposentadoria;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.util.StaticMethods;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Repository
public class RegistroAposentadoriaRepository  extends DefaultRepository<RegistroAposentadoria, BigInteger> {


    public RegistroAposentadoriaRepository(EntityManager em) {
        super(em);
    }


    public String getSearch(HashMap<String,String> filtros) {
        String search = "";
        //monta pesquisa search
        String nome= filtros.get("nome");
        String cpf= filtros.get("cpf");

        if (nome !=null && !nome.isEmpty()){
            search=" where s.nome like '%"+nome+"%'";
        } else if (cpf !=null && !cpf.isEmpty()) {
            search=" where a.cpfServidor  = '"+cpf.trim()+"'";
        }
        return search;
    }

    public PaginacaoUtil<HashMap<String, Object>> getMovimentosParaRegistrar(Pageable pageable, HashMap<String, String > filtros,  Registro.Tipo  tipoRegistroEnum ){
        HashMap<String,String> variacoesNaQuery=this.getVariacoesNaQueryAposentadoria().get(tipoRegistroEnum.getLabel());

        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());

        String whereStatemente =getSearch(filtros);

        Query queryMovimentos = getEntityManager().createNativeQuery("" +
                        " with envios as (" +
                        "    select cast( substring(processo,1,len(processo)-5) as int) numeroProcesso, cast( substring(processo,len(processo)-3,len(processo) )  as int) anoProcesso, status,a.idMovimentacao from AdmEnvio a where status=4 " +
                        "), aposentEnvios as (select b.numeroProcesso, b.anoProcesso,a.* from Aposentadoria a  join envios b on a.id=b.idMovimentacao) " +
                        " , apos as (select i.idUnidadeGestora,a.* " +
                        "               from aposentEnvios a " +
                        "                        join InfoRemessa i on a.chave = i.chave "+variacoesNaQuery.get("FiltroSubtipo")+" and  i.idUnidadeGestora = '"+filtros.get("ug")+"' ) " +
                        " select a.id as idMovimentacao ,   " +
                        "       s.cpfServidor as cpfServidor, " +
                        "        s.nome as nome, " +
                        "         "+variacoesNaQuery.get("TipoMovimento")+"    AS tipoMovimentacao, " +
                        "        a.numeroProcesso as numeroProcesso, " +
                        "        a.anoProcesso as anoProcesso, " +
                        "        a.dataAposentadoria as dataMovimentacao, " +
                        "        c.nomeCargo         as nomeCargo, " +
                        "        c.codigoCargo      as codigoCargo,\n"+
                        "        c.id      as idcargo,\n"+
                        "        at.numeroAto        as numeroAto, " +
                        "        at.tipoAto          as tipoAto, " +
                        "        a.idUnidadeGestora  as idUnidadeGestora, " +
                        "        at.dataPublicacao   as dataAto " +
                        " from apos a " +
                        "          join Ato at on a.idAto = at.id " +
                        "          join Admissao ad on a.id = ad.id " +
                        "          join Cargo c on ad.idCargo = c.id " +
                        "          join Servidor s on ad.idServidor = s.id "+whereStatemente+" order by a.id" );

        long totalRegistros = countMovimentosAposentadoriaParaRegistrar(variacoesNaQuery,filtros.get("ug"));
        int tamanhoPorPagina =  (whereStatemente.isEmpty())? Integer.valueOf(pageable.getPageSize()) : (int) totalRegistros ;
        tamanhoPorPagina = tamanhoPorPagina == 0 ? 1:tamanhoPorPagina;
        long totalPaginas = (totalRegistros + (tamanhoPorPagina - 1)) / tamanhoPorPagina;
        //destiva paginação se tiver filtro preenchido
        if ( whereStatemente.isEmpty() && totalRegistros > tamanho ) {
            queryMovimentos.setFirstResult(pagina).setMaxResults(tamanho);
        }
        try{
            return new PaginacaoUtil<>(tamanhoPorPagina, pagina, totalPaginas, totalRegistros, StaticMethods.getHashmapFromQuery(queryMovimentos));
        } catch (RuntimeException e ){
            throw  new RuntimeException("Problema ao consultar os movimentos para registro!!");
        }
    }

    public Integer countMovimentosAposentadoriaParaRegistrar(HashMap<String,String> variacoesNaQuery,String unidadeGestora ) {
        try{
        Query queryQuantidade = getEntityManager().createNativeQuery(
                        " with envios as (" +
                                "    select cast( substring(processo,1,len(processo)-5) as int) numeroProcesso, cast( substring(processo,len(processo)-3,len(processo) )  as int) anoProcesso, status,a.idMovimentacao from AdmEnvio a where status=4 " +
                                "), aposentEnvios as (select b.numeroProcesso, b.anoProcesso,a.* from Aposentadoria a  join envios b on a.id=b.idMovimentacao) " +
                                ",apos as (select count(1) ct " +
                        "               from aposentEnvios a " +
                        "                        join InfoRemessa i on a.chave = i.chave "+variacoesNaQuery.get("FiltroSubtipo")+" and i.idUnidadeGestora = :ug  where not exists(select 1 from RegistroAposentadoria  where idAposentadoria = a.id   ) ) " +
                        " select ct  " +
                        " from apos a")
                .setParameter("ug", unidadeGestora);
        return (Integer) queryQuantidade.getSingleResult();
        } catch (RuntimeException e){
            throw  new RuntimeException("Problema ao consultar os movimentos para registro!!");
        }
    }

    public PaginacaoUtil<HashMap<String, Object>> getRegistros(Pageable pageable,Registro.Tipo tipoRegistroEnum,HashMap<String,String> filtros){

        HashMap<String,String> variacoesNaQuery=this.getVariacoesNaQueryAposentadoria().get(tipoRegistroEnum.getLabel());

        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String whereStatemente =getSearch(filtros);

        Query queryRegistroMovimentos = getEntityManager().createNativeQuery("" +
                        " with registros as (select id, idAposentadoria,dataAtoDecisao,dataCadastro, numeroAnoProcesso, numeroAtoDecisao, tipoAtoDecisao, observacao " +
                        "                   from RegistroAposentadoria " +
                        "                   where idUnidadeGestora = :ug and cpfUsuarioCadastro=:cpfUsuario   and  (:cpfServidor is null or    :cpfServidor = cpfServidor) and dataCadastro  between  cast(:dtini as date) and cast(:dtfim as date)  \n" +
                        "                     ) " +
                        "select \r\n " + //
                        //tentar usar o setFirstResult(0) ou o setMaxResults() sem o setFirstResult() causa erro na consulta 
                        (pagina == 1 ?"       top("+tamanho+")\n":"") + 
                        "       b.id                as idRegistro,\n" +
                        "       idAposentadoria     as idMovimentacao,\n" +
                        "       numeroAnoProcesso   as numeroAnoProcesso,\n" +
                        "       dataAtoDecisao   as dataAtoDecisao,\n" +
                        "       numeroAtoDecisao    as numeroAtoDecisao,\n" +
                        "       tipoAtoDecisao      as tipoAtoDecisao,\n" +
                        "       b.observacao          as observacao,\n" +
                        "       s.cpfServidor       as cpfServidor,\n" +
                        "       s.nome              as nome,\n" +
                        "       'APOSENTADORIA'     AS tipoMovimentacao,\n" +
                        "       a.dataAposentadoria as dataMovimentacao,\n" +
                        "         "+variacoesNaQuery.get("TipoMovimento")+"    AS tipoAposentadoria, " +
                        "       c.nomeCargo         as nomeCargo,\n" +
                        "       at.numeroAto        as numeroAto,\n" +
                        "       at.tipoAto          as tipoAto,\n" +
                        "       b.dataCadastro          as dataCadastro,\n" +
                        "       at.dataPublicacao   as dataAto \n" +
                        "from registros b \n" +
                        "         join Aposentadoria a on b.idAposentadoria=a.id "+variacoesNaQuery.get("FiltroSubtipo")+" \n" +
                        "         join Ato at on a.idAto = at.id \n" +
                        "         join Admissao ad on a.id = ad.id \n" +
                        "         join Cargo c on ad.idCargo = c.id \n" +
                        "         join Servidor s on ad.idServidor = s.id " )
                .setParameter("ug", filtros.get("ug"))
                .setParameter("cpfUsuario", filtros.get("cpfUsuario"))
                .setParameter("dtini", filtros.get("dataInicio"))
                .setParameter("dtfim", filtros.get("dataFim"))
                .setParameter("cpfServidor", Objects.requireNonNullElse(filtros.get("cpf"),"").isEmpty() ? null:filtros.get("cpf"))
                ;

        long totalRegistros = countRegistros(variacoesNaQuery,filtros);
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;

        if (totalRegistros > tamanho  && whereStatemente.isEmpty()&& pagina != 1 ){
            queryRegistroMovimentos.setFirstResult(pagina).setMaxResults(tamanho);
        }
        try{
            return new PaginacaoUtil<>(tamanho, pagina, totalPaginas, totalRegistros, StaticMethods.getHashmapFromQuery(queryRegistroMovimentos));
        } catch (RuntimeException e ){
            throw  new RuntimeException("Problema ao consultar os registros!!");
        }
    }

    public Integer countRegistros(HashMap<String,String> variacoesNaQuery, HashMap<String,String> filtros ) {
        try{
            Query queryQuantidade = getEntityManager().createNativeQuery(
                            " with registros " +
                                    "as (select  idAposentadoria  " +
                                    "                   from RegistroAposentadoria " +
                                    "                   where idUnidadeGestora = :ug and cpfUsuarioCadastro=:cpfUsuario and  (:cpfServidor is null or    :cpfServidor = cpfServidor) and dataCadastro  between  cast(:dtini as date) and cast(:dtfim as date) )" +
                                    "                      " +
                                    "  " +
                                    " select count(1)  " +
                                    " from registros b join Aposentadoria a on b.idAposentadoria= a.id  "+variacoesNaQuery.get("FiltroSubtipo"))
                    .setParameter("ug", filtros.get("ug"))
                    .setParameter("cpfUsuario", filtros.get("cpfUsuario"))
                    .setParameter("dtini", filtros.get("dataInicio"))
                    .setParameter("dtfim", filtros.get("dataFim"))
                    //caso filtro de cpf estiver vazio traz todos resultados referentes aos outros filtros
                    .setParameter("cpfServidor", Objects.requireNonNullElse(filtros.get("cpf"),"").isEmpty() ? null:filtros.get("cpf"))
                            ;
            return (Integer) queryQuantidade.getSingleResult();
        }catch (RuntimeException e){
            throw  new RuntimeException("Problema ao consultar os registros!!");
        }
    }

   //Solucao para não fazer um metodo para cada subtipo de aposentadoria(TIPOS DE ACORDO COM O REGISTRO) visto que a variacao na consulta é pouca
    private  HashMap<String,HashMap<String, String>> getVariacoesNaQueryAposentadoria(){
        HashMap<String, HashMap<String, String>> subtipos = new HashMap<>(){

        };

        HashMap<String,String> variacoesNaQueryAposentadoria =new HashMap<>();
        variacoesNaQueryAposentadoria.put("TipoMovimento"," a.tipoAposentadoria ");
        variacoesNaQueryAposentadoria.put("FiltroSubtipo"," and  a.tipoAposentadoria  not in (7,6) and a.reversao=0 and a.revisao=0  ");
        subtipos.put("Aposentadoria",variacoesNaQueryAposentadoria);

        HashMap<String,String> variacoesNaQueryReforma =new HashMap<>();
        variacoesNaQueryReforma.put("TipoMovimento","'REFORMA'");
        variacoesNaQueryReforma.put("FiltroSubtipo"," and  a.tipoAposentadoria  = 7 and a.reversao=0   and a.revisao=0  ");
        subtipos.put("Reforma",variacoesNaQueryReforma);

        HashMap<String,String> variacoesNaQueryReserva =new HashMap<>();
        variacoesNaQueryReserva.put("TipoMovimento","'RESERVA'");
        variacoesNaQueryReserva.put("FiltroSubtipo"," and  a.tipoAposentadoria  = 6 and a.reversao=0   and a.revisao=0  ");
        subtipos.put("Reserva",variacoesNaQueryReserva);

        HashMap<String,String> variacoesNaQueryRevisaoAposentadoria =new HashMap<>();
        variacoesNaQueryRevisaoAposentadoria.put("TipoMovimento","'REVISÃO DE APOSENTADORIA'");
        variacoesNaQueryRevisaoAposentadoria.put("FiltroSubtipo"," and  a.tipoAposentadoria  not in (7,6) and a.reversao=0  and revisao=1  ");
        subtipos.put("RevisaoAposentadoria",variacoesNaQueryRevisaoAposentadoria);

        HashMap<String,String> variacoesNaQueryRevisaoReforma =new HashMap<>();
        variacoesNaQueryRevisaoReforma.put("TipoMovimento","'REVISÃO DE REFORMA'");
        variacoesNaQueryRevisaoReforma.put("FiltroSubtipo"," and  a.tipoAposentadoria  = 7  and a.reversao=0  and revisao=1  ");
        subtipos.put("RevisaoReforma",variacoesNaQueryRevisaoReforma);

        HashMap<String,String> variacoesNaQueryRevisaoReserva =new HashMap<>();
        variacoesNaQueryRevisaoReserva.put("TipoMovimento","'REViSÃO DE RESERVA'");
        variacoesNaQueryRevisaoReserva.put("FiltroSubtipo"," and  a.tipoAposentadoria  = 6  and a.reversao=0  and revisao=1  ");
        subtipos.put("RevisaoReserva",variacoesNaQueryRevisaoReserva);

        HashMap<String,String> variacoesNaQueryReversao =new HashMap<>();
        variacoesNaQueryReversao.put("TipoMovimento","'REVERSÃO'");
        variacoesNaQueryReversao.put("FiltroSubtipo","   and a.reversao=1  ");
        subtipos.put("Reversao",variacoesNaQueryReversao);

        return subtipos;
    }

    public HashMap<String,Object> getUserInfoFromIdUsuarioAutenticacao(BigInteger idIsuario ){
        try{
                //idIsuario = BigInteger.valueOf(17745);
                // var a = StaticMethods.getHashmapFromQuery(getEntityManager().createNativeQuery("  " +
                //             "select *" +
                //             "from Autenticar.dbo.Usuario"));
                // var b = StaticMethods.getHashmapFromQuery(getEntityManager().createNativeQuery(
                //     "select id as id,codigo as cpfUsuario,login as loginUsuario ,nome " +
                //     "from Autenticar.dbo.Usuario where upper(nome) like '%'+upper('guilherme h')+'%'"));
            
            Query sqlAutenticacao=getEntityManager().createNativeQuery("  " +
                            "  select id as id,codigo as cpfUsuario,login as loginUsuario " +
                            "from Autenticar.dbo.vwUsuario where id_pessoa =:id ")
                    .setParameter("id",idIsuario).setMaxResults(1);
            var aux =StaticMethods.getHashmapFromQuery(sqlAutenticacao).get(0);
            aux.put("login",aux.get("loginUsuario"));

            return aux ;
        }catch (RuntimeException e){
            throw new RuntimeException("problema na autenticação:não encontrou a id do usuario informado!!");
        }
    }

    public String getUserSetorFromLoginNoEcontas(String loginUsuario ){
        try{

            return  (String) getEntityManager().createNativeQuery("  " +
                            " select lotacs_depto  from SCP.dbo.lotacs where lotacs_login= :login   and status='A'  ")
                    .setParameter("login",loginUsuario).setMaxResults(1).getSingleResult();
        }catch (NoResultException e){
            return null;
        }
        catch (RuntimeException e){
            throw new RuntimeException("problema na autenticação:não encontrou o setor do usuario ativo informado!!");
        }
    }


    public List<HashMap<String,Object>> getInforProcessosEcontas(HashMap<String,Object> userInfo){
        return getInforProcessosEcontas(userInfo,null);
    }
    public List<HashMap<String,Object>> getInforProcessosEcontas(HashMap<String,Object> userInfo,HashMap<String,Object> infoMovimentacao){
        try{
            Query sqlProcessos=getEntityManager().createNativeQuery(
                             " with \r\n" + //
                             "   processosRecebidos as\r\n" + //
                             "    (\r\n" + //
                             "        select hcodp_pnumero,\r\n" + //
                             "               hcodp_pano,\r\n" + //
                             "               assunto_desc,\r\n" + //
                             "               id_entidade_origem\r\n" + //
                             "        from SCP.dbo.VW_PROC_RECEBIDOS\r\n" + //
                             "        where (\r\n" + //
                             //"            (hdest_ldepto like '%DIRAP%' ) AND\r\n" + //
                             "            (proc_num_anexo IS NULL or proc_num_anexo =0) and\r\n" + //
                             "            (processo_numaps IS NULL or processo_numaps =0)\r\n" + //
                             "        )  "+
                            (userInfo != null ?"and (trim(hists_dest_resp)= :Usuario or trim(hdest_llogin)= :Usuario )\r\n":"" )+ //
                             "    ),\r\n"+ //
                             "registros as (\r\n" + //
                            "        select numeroAnoProcesso from RegistroPensao\r\n" + //
                            "        union all\r\n" + //
                            "        select numeroAnoProcesso from RegistroAposentadoria\r\n" + //
                            "        union all\r\n" + //
                            "        select numeroAnoProcesso from RegistroAdmissao\r\n" + //
                            "        union all\r\n" + //
                            "        select RegNrAnoProc from SICAPAP..REGISTRO r\r\n" + //
                            "\r\n" + //
                            "    )," + //
                             "    processos as (\r\n" + //
                             "        select distinct\r\n" + //
                             "           hcodp_pnumero as numeroProcesso,\r\n" + //
                             "           hcodp_pano as anoProcesso,\r\n" + //
                             "           docmt_numero as numeroDecisao,\r\n" + //
                             "           docmt_ano as anoDecisao ,\r\n" + //
                             "           docmt_data as dataDecisao ,\r\n" + //
                             "           assunto_desc  as assuntoDesc,\r\n" + //
                             "           e.cpf  as cpfInteressado,\r\n" + //
                             "           e.nome nomeInteressado ,\r\n" + //
                             "           f.codunidadegestora as cnpjEntidade,\r\n" + //
                             "           f.nomeEntidade as nomeEntidade\r\n" + //
                             "        from processosRecebidos a\r\n" + //
                             "            join SCP..PESSOAS_PROCESSO d  on\r\n" + //
                             "                (ID_CARGO=0 )and\r\n" + //
                             "                d.ID_PAPEL<> 1 and\r\n" + //
                             "                (\r\n" + //
                             "                    (d.ID_PAPEL = 14 AND assunto_desc like '%PENSÃO%') or\r\n" + //
                             "                    (d.ID_PAPEL not in (1,14)  AND assunto_desc not like '%PENSÃO%')\r\n" + //
                             "                )and\r\n" + //
                             "                a.hcodp_pnumero=d.NUM_PROC and\r\n" + //
                             "                a.hcodp_pano = d.ANO_PROC\r\n" + //
                             "            join SCP.dbo.document c on c.dcnproc_pnumero = hcodp_pnumero and c.dcnproc_pano = hcodp_pano and c.docmt_tipo_decisao = 'D'  and docmt_tipo='RL'\r\n" + //
                             "            join Cadun.dbo.PessoaFisica e on d.ID_PESSOA=e.Codigo\r\n" + //
                             "            join Cadun.dbo.vwPessoaJuridica f on a.id_entidade_origem= f.id\r\n\r\n" + //
                             "            left join registros on numeroAnoProcesso = CONCAT(a.hcodp_pnumero,a.hcodp_pano)\r\n" + //
                             "        where registros.numeroAnoProcesso is null \r\n" + //
                             (infoMovimentacao==null?"":"        and e.cpf = :cpf and f.codunidadegestora = :cnpj and assunto_desc = :assunto") + //
                             "    ),\r\n" + //
                             "    envios as (\r\n" + //
                             "                select cast(substring(processo, 1, len(processo) - 5) as int)             numeroProcesso,\r\n" + //
                             "                       cast(substring(processo, len(processo) - 3, len(processo)) as int) anoProcesso\r\n" + //
                             "                from AdmEnvio a\r\n" + //
                             "                where processo is not  null and idCancelamentoEnvio is null and idCancelamentoEnvio is null \r\n" + //
                             "                union\r\n" + //
                             "                select cast(substring(processo, 1, len(processo) - 5) as int)             numeroProcesso,\r\n" + //
                             "                       cast(substring(processo, len(processo) - 3, len(processo)) as int) anoProcesso\r\n" + //
                             "                from AdmissaoEnvio a\r\n" + //
                             "                    where status = 3\r\n" + //
                             "    )\r\n" + //
                             "select pss.*\r\n" + //
                             "from envios env\r\n" + //
                             "    join processos pss on\r\n" + //
                             "        env.numeroProcesso = pss.numeroProcesso and\r\n" + //
                             "        env.anoProcesso    = pss.anoProcesso");
                    
                    if (userInfo != null){
                        sqlProcessos.setParameter("Usuario",userInfo.get("loginUsuario"));
                    }
                    if (infoMovimentacao != null) {
                        sqlProcessos
                            .setParameter("cpf",infoMovimentacao.get("cpf"))
                            .setParameter("cnpj",infoMovimentacao.get("cnpjUnidadeGestora"))
                            .setParameter("assunto",infoMovimentacao.get("assuntoProcessoEcontas"));
                    }
            return  StaticMethods.getHashmapFromQuery(sqlProcessos);
        } catch (RuntimeException e){
            throw new RuntimeException("não foi posivel encontrar os processos no econtas!!");
        }
    }


    public Boolean temProcessoEcontasPorInteressado(HashMap<String,Object> userInfo, HashMap<String,Object> infoMovimentacao ){
        return !getInforProcessosEcontas(userInfo, infoMovimentacao).isEmpty();
    }


    public List<HashMap<String,Object>> getDocumentosbyProcessoEcontas(HashMap<String,Object> infoUser,Integer numeroProcesso , Integer anoProcesso  ){
        try{
            Query sqlProcessos=getEntityManager().createNativeQuery("    (select docmt_tipo as docTipo,isnull(b.NOME_ARQ,'Sem nome')  as nomeArquivo, isnull(b.UUID_CAS,b.HASH_DOCUMENTO) as castorFileId from SCP.dbo.document a\n" +
                    "    join SCP.dbo.DOCUMENT_ARQUIVOS b on a.dcnproc_pnumero=:numeroProcesso \n" +
                    "                                        and a.dcnproc_pano =:anoProcesso\n" +
                    "                                        and (TRIM(docmt_tipo)   in ('TA','RL') ) " +
                    "                                        and EXCLUIDO='N'\n" +
                    "                                        and   (isnull(a.id_documento,a.docmt_id) = b.ID ) " +
                    ") " +
                    "union all " +
                    "( " +
                    "select docmt_tipo as docTipo,isnull(b.NOME_ARQ,'Sem nome') as nomeArquivo, isnull(b.UUID_CAS,b.HASH_DOCUMENTO)  as castorFileId from SCP.dbo.document a\n" +
                    "    join SCP.dbo.DOCUMENT_ARQUIVOS b on a.dcnproc_pnumero=:numeroProcesso \n" +
                    "                                        and a.dcnproc_pano =:anoProcesso \n" +
                    "                                        and (TRIM(docmt_tipo)   in ('TA','RL') )\n" +
                    "                                            and EXCLUIDO='N'\n" +
                    "                                        and   ( isnull(a.id_documento,a.docmt_id) = b.ID_DOCUMENT)\n" +
                    ")  ")
                    .setParameter("numeroProcesso",numeroProcesso)
                    .setParameter("anoProcesso",anoProcesso);
            return  StaticMethods.getHashmapFromQuery(sqlProcessos);
        }catch (RuntimeException e){
            throw new RuntimeException("não encontro os documentos iniciais e a decisão no processo no econtas!!");
        }
    }




}
