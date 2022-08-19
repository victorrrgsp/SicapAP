package com.example.sicapweb.repository.concessao;

import br.gov.to.tce.model.adm.AdmEnvio;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;

@Repository
public class AdmEnvioRepository extends DefaultRepository<AdmEnvio, BigInteger> {

    public AdmEnvioRepository(EntityManager em) {
        super(em);
    }

    public PaginacaoUtil<AdmEnvio> buscaPaginada(Pageable pageable, String searchParams, Integer tipoParams) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";

        //monta pesquisa search
        if (searchParams.length() > 3) {

            if (tipoParams == 0) { //entra para tratar a string
                String arrayOfStrings[] = searchParams.split("=");
                search = " AND " + arrayOfStrings[0] + " LIKE  '%" + arrayOfStrings[1] + "%'  ";
            } else {
                search = " AND " + searchParams + "   ";
            }
        }

        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<AdmEnvio> list = getEntityManager().createNativeQuery("" +
                        "select * from AdmEnvio " +
                        "where status = 3 and unidadeGestora = '"
                        + User.getUser(request).getUnidadeGestora().getId() + "' " + search + " ORDER BY "
                        + campo, AdmEnvio.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();

        long totalRegistros = countEnvio();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;

        return new PaginacaoUtil<AdmEnvio>(tamanho, pagina, totalPaginas, totalRegistros, list);
    }

    public List<HashMap<String, Object>> infoByRecibo(long idAdmissao) {
        List<Object[]> list = getEntityManager().createNativeQuery("select top 1  env.processo,env.unidadeGestora ,ug.nome as nomeEntidade ,CAST(se.nome AS varchar(500)) as nomeServidor ,env.tipoRegistro ,AEA.data_assinatura\n" +
                        "    from AdmEnvio env\n" +
                        "    join unidadegestora ug on env.unidadeGestora = ug.id\n" +
                        "    join Admissao ad on env.idAdmissao = ad.id\n" +
                        "    join Servidor se on ad.idServidor = se.id\n" +
                        "    left join AdmEnvioAssinatura AEA on env.id = AEA.idEnvio" +
                        "      where " +
                        //"status = 3 and\n" +
                        "        env.idAdmissao = ?")
                .setParameter(1, idAdmissao)
                .getResultList();

        List<HashMap<String, Object>> resutSet = new ArrayList<>();
        list.forEach(admEnvio -> {
            var aux = new HashMap<String, Object>();
            aux.put("processo", (String) admEnvio[0]);
            aux.put("UnidadeGestora", (String) admEnvio[1]);
            aux.put("nomeEntidade", (String) admEnvio[2]);
            aux.put("nomeServidor", (String) admEnvio[3]);
            aux.put("dataAssinatura", new Date(((Timestamp)admEnvio[5]).getTime()));
            var tipoLabel = Arrays.stream(AdmEnvio.TipoRegistro.values()).filter(a -> a.getValor() == (int) admEnvio[4]).findFirst().get().getLabel();
            aux.put("tipoRegistro", tipoLabel.toUpperCase());

            resutSet.add(aux);
        });
        return resutSet;
    }

    public List<HashMap<String,Object>> buscaTotalNaoPaginada(String searchParams, List<String> ug, List<Integer> tipoRegistro , LocalDate dataInico, LocalDate dataFim){

                var query = getEntityManager().createNativeQuery(
                                        "with AdmEnvioAssinatura1 as\n" +
                                        "         (select ROW_NUMBER() over(partition by idEnvio order by data_assinatura) as rank, idEnvio ,data_assinatura\n" +
                                        "          from SICAPAP21..AdmEnvioAssinatura\n" +
                                        "        )\n" +
                                        "select\n" +
                                        "    ad.*,\n" +
                                        "    UG.nome as nomeUg,\n" +
                                        "    UGorigen.nome as nomeUgOrigem,\n" +
                                        "    adA.data_assinatura\n" +
                                        "from AdmEnvio ad\n" +
                                        "     join SICAPAP21.dbo.UnidadeGestora UG on UG.id = ad.unidadeGestora\n" +
                                        "     join SICAPAP21.dbo.UnidadeGestora UGorigen on UGorigen.id = ad.orgaoOrigem\n" +
                                        "     left join AdmEnvioAssinatura1 adA on adA.idEnvio = ad.id and adA.rank = 1\n" +
                                        "where (ad.unidadeGestora in :ug or 'todos' in :ug ) \n"+
                                        "     and (ad.tipoRegistro in :TipoRegistro or -1 in :TipoRegistro )\n" +
                                        "     and ((adA.data_assinatura between :dataInico and :dataFim) or (:dataInico is null or :dataFim is null))\n" +
                                        "     and ad.unidadeGestora <> '00000000000000'"
                )
                .setParameter("ug" ,ug)
                .setParameter("TipoRegistro" ,tipoRegistro)
                .setParameter("dataInico" ,dataInico)
                .setParameter("dataFim" ,dataFim);
        List<Object[]> list = query.getResultList();
        List<HashMap<String,Object>> retorno = new ArrayList<HashMap<String,Object>>();

        list.forEach(envio ->{
            var aux = new HashMap<String,Object>();
            //aux.put("id", envio[0] );
            aux.put("TipoRegistro",this.getTipoByValue((Integer)envio[1]));
            //aux.put("UnidadeGestora", envio[2] );
            aux.put("processo", envio[3] );
            {
                var status = Arrays.asList(AdmEnvio.Status.values());
                aux.put("status",
                            status
                                .stream()
                                .filter(stat -> stat.getValor() == (Integer)envio[4])
                                .findFirst()
                                .get()
                        );
            }
            //aux.put("orgaoOrigem", envio[5] );
            //aux.put("idMovimentacoes", envio[6] );
            aux.put("Complemento", envio[7] );
            aux.put("nomeUg", envio[9] );
            aux.put("nomeUgOrigem", envio[10] );
            aux.put("DataAsinatura", envio[11] );
            retorno.add(aux);
        });
        return retorno;


    }
    public static AdmEnvio.TipoRegistro getTipoByValue(Integer value){
        var tipos = Arrays.asList(AdmEnvio.TipoRegistro.values());
        return tipos
                    .stream()
                    .filter(tipo -> tipo.getValor() == value)
                    .findFirst()
                    .get();
    }


    public Integer countEnvio() {
        Query query = getEntityManager().createNativeQuery("" +
                "select count(*) from AdmEnvio " +
                "where status = 3 and unidadeGestora = '"
                + User.getUser(request).getUnidadeGestora().getId() + "' ");
        return (Integer) query.getSingleResult();
    }
}
