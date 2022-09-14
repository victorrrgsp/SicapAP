package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.adm.AdmEnvio;
import br.gov.to.tce.model.ap.concurso.ConcursoEnvio;
import com.example.sicapweb.exception.InvalitInsert;
import com.example.sicapweb.model.ConcursoEnvioAssRetorno;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ConcursoEnvioRepository extends DefaultRepository<ConcursoEnvio, BigInteger> {

    public ConcursoEnvioRepository(EntityManager em) {
        super(em);
    }

    public List<ConcursoEnvio> buscarEnvioFAse1PorEdital(BigInteger idEdital) {
        return  getEntityManager().createNativeQuery(
                        "select ev.* from ConcursoEnvio ev "+
                               " where fase=1 and  idEdital = " + idEdital, ConcursoEnvio.class)
                .getResultList();
    }

    public List<ConcursoEnvio> buscarEnvioFAse2PorEdital(BigInteger idEdital) {
        return  getEntityManager().createNativeQuery(
                        "select ev.* from ConcursoEnvio ev "+
                                " where fase=2 and  idEdital = " + idEdital, ConcursoEnvio.class)
                .getResultList();
    }

    public ConcursoEnvio buscarEnvioFAse1PorEditalassinado(BigInteger idEdital) {
        List<ConcursoEnvio> lc=  getEntityManager().createNativeQuery(
                        "select  ev.* from ConcursoEnvio ev "+
                                " where fase=1 and status in (2,3,4) and  idEdital = " + idEdital+ " order by dataEnvio desc ", ConcursoEnvio.class)
                .getResultList();
        if (lc.size() ==1 ) { return lc.get(0); }
        else if (lc.size() > 1 ) {
            throw new InvalitInsert("Encontrou mais de um processo pai para o envio da homologação!! ");
        }
        return null;
    }
    public String getSearch(String searchParams, Integer tipoParams) {
        String search = "";
        //monta pesquisa search
        if (searchParams.length() > 3) {
            if (tipoParams == 0) { //entra para tratar a string
                String arrayOfStrings[] = searchParams.split("=");
                if (arrayOfStrings[0].equals("numeroEdital"))
                    search = " and a." + arrayOfStrings[0] + " LIKE '%" + arrayOfStrings[1] + "%'  ";
                else if (arrayOfStrings[0].equals("veiculoPublicacao"))
                    search = " and a." + arrayOfStrings[0] + " LIKE '%" + arrayOfStrings[1] + "%'  ";
                else if (arrayOfStrings[0].equals("dataPublicacao"))
                    search = " and a." + arrayOfStrings[0] + " LIKE '%" + arrayOfStrings[1] + "%'  ";
                else
                    search = " and " + arrayOfStrings[0] + " LIKE '%" + arrayOfStrings[1] + "%'  ";
            } else {
                search = " and " + searchParams + "   ";
            }
        }
        return search;
    }


    public PaginacaoUtil<ConcursoEnvioAssRetorno> buscarEnviosAguardandoAss(Pageable pageable, String searchParams, Integer tipoParams) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";
        search = getSearch(searchParams, tipoParams);
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<ConcursoEnvio> list = getEntityManager()
                .createNativeQuery("select a.* from ConcursoEnvio a " +
                        " where status=3 and  not exists(select 1 from ConcursoEnvioAssinatura ass  where  ass.idEnvio=a.id) " + search + " ORDER BY " + campo, ConcursoEnvio.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();


        long totalRegistros = countEnviosAguardandoAss(search);
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;

        List<ConcursoEnvioAssRetorno> listc= new ArrayList<ConcursoEnvioAssRetorno>() ;
        for(Integer i= 0; i < list.size(); i++){
            ConcursoEnvioAssRetorno pac =new ConcursoEnvioAssRetorno();
            pac.setConcursoEnvio(list.get(i));
            Integer qt = (Integer)  getEntityManager().createNativeQuery("select count(*) from ConcursoEnvioAssinatura a " +
                    "where   a.idEnvio = "+ list.get(i).getId()+ "").getSingleResult();
            if (qt ==0 ){
                pac.setStatusAssinatura(1);
            }else  pac.setStatusAssinatura(2);
          listc.add(pac);
        }

        return new PaginacaoUtil<ConcursoEnvioAssRetorno>(tamanho, pagina, totalPaginas, totalRegistros, listc);
    }



    public Integer countEnviosAguardandoAss(String search) {
        Query query = getEntityManager().createNativeQuery("select count(*) from ConcursoEnvio a " +
                " where not exists(select 1 from ConcursoEnvioAssinatura ass  where  ass.idEnvio=a.id) " + search);
        return (Integer) query.getSingleResult();
    }

    public List<Map<String, Integer>> getProcessosEcontas(Integer numEdital, Integer anoEdital, String ug){
        List<Map<String, Integer>> retorno = new ArrayList<Map<String, Integer>>();
        Query query = getEntityManager().createNativeQuery("SELECT " +
                "                        distinct p.processo_numero, p.processo_ano " +
                "                   FROM " +
                "                          SCP..processo p " +
                " INNER JOIN SCP..ProcEdital pe on p.processo_numero = pe.NumProc and p.processo_ano = pe.AnoProc " +
                "                    INNER JOIN SCP..assunto a on a.assunto_classe_assunto = p.processo_assunto_classe_assunto " +
                "                                            and a.assunto_codigo = p.processo_assunto_codigo " +
                "                                            and a.id = :assunto " +
                "                    INNER JOIN Cadun..vwPessoaGeral pj ON (pj.id = p.id_entidade_origem) OR (pj.id = p.id_entidade_vinc) " +
                "                   WHERE pj.cnpj = :cnpj " +
                "   AND p.processo_assunto_codigo = 6" +
                "   AND p.processo_assunto_classe_assunto = 8 " +
                "   AND pe.NumEdital = :numeroedital and pe.AnoEdital = :anoedital");
        query.setParameter("cnpj",ug);
        query.setParameter("numeroedital",numEdital);
        query.setParameter("anoedital",anoEdital);
        query.setParameter("assunto",64);
        List<Object[]> list  = query.getResultList();
        for (Object[] obj :list){
            Map<String, Integer> mapa = new HashMap<String, Integer>();

            mapa.put("processo_numero", (Integer) obj[0]);
            mapa.put("processo_ano", (Integer) obj[1]);
            retorno.add(mapa);
        }
        return retorno;


    }


    public List<HashMap<String,Object>> buscaTotalNaoPaginada( List<String> ug, List<Integer> fase , LocalDate dataInico, LocalDate dataFim, Integer Ststuss){

        var query = getEntityManager().createNativeQuery(
                        "with ConcursoEnvioassinatura1 as                                       (select ROW_NUMBER() over(partition by idEnvio order by data_assinatura) as rank, idEnvio ,data_assinatura\n" +
                                "                                         from SICAPAP21..ConcursoEnvioAssinatura\n" +
                                "                                        )\n" +
                                "                                select\n" +
                                "                                   concenv.fase,\n" +
                                "                                   concenv.processo,\n" +
                                "                                   case when ed.complementoNumero is not null then  ed.numeroEdital+'-'+ed.complementoNumero else ed.numeroEdital end  numeroEdital,\n" +
                                "                                    inf.nomeUnidade as nomeUg,\n" +
                                "                                   coalesce(ug.nome,inf.nomeUnidade) as nomeUgOrigem,\n" +
                                "                                   concenv.status,\n" +
                                "                                    adA.data_assinatura DataAssinatura \n" +
                                "                                from ConcursoEnvio concenv \n" +
                                "                                    join Edital ed on concenv.idEdital= ed.id\n" +
                                "                                    join InfoRemessa inf on ed.chave=inf.chave and inf.idUnidadeGestora <> '00000000000000'\n" +
                                "                                     left join SICAPAP21.dbo.UnidadeGestora UG on UG.id = concenv.orgaoorigem\n" +
                                "                                     left join ConcursoEnvioassinatura1 adA on adA.idEnvio = concenv.id and adA.rank = 1\n" +
                                "                                where (inf.idUnidadeGestora in :ug or 'todos' in :ug )\n" +
                                "                                     and (concenv.fase in :fase or -1 in :fase )\n" +
                                "                                     and ((adA.data_assinatura between :dataInico and :dataFim) or (:dataInico is null or :dataFim is null) or adA.data_assinatura is null) " +
                                "\n" +
                                "                                     and ( :status = -1 or concenv.status = :status) "
                )
                .setParameter("ug" ,ug)
                .setParameter("fase" ,fase)
                .setParameter("dataInico" ,dataInico)
                .setParameter("status" ,Ststuss)
                .setParameter("dataFim" ,dataFim);
        List<HashMap<String,Object>> retorno = new ArrayList<HashMap<String,Object>>();

        retorno=getMapList(query);
        retorno=  retorno.stream().map( hashmap -> {
            hashmap.put("fase",getFaseByValue(hashmap.get("fase")).toString().toUpperCase() )   ;
            hashmap.put("status",geStatusByValue(hashmap.get("status")).toString().toUpperCase() )   ;
            return hashmap;
        } ).collect(Collectors.toList());
        return retorno;


    }

    public static ConcursoEnvio.Fase getFaseByValue(Object value){
        var tipos = Arrays.asList(ConcursoEnvio.Fase.values());
        return tipos
                .stream()
                .filter(tipo -> tipo.getValor() == value)
                .findFirst()
                .get()
                ;
    }

    public static ConcursoEnvio.Status geStatusByValue(Object value){
        var tipos = Arrays.asList(ConcursoEnvio.Status.values());
        return tipos
                .stream()
                .filter(tipo -> tipo.getValor() == value)
                .findFirst()
                .get()
                ;
    }
    private List<HashMap<String,Object>> getMapList(Query query) {
        return   ( (NativeQueryImpl) query).setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE).getResultList();
    }



}
