package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.ap.concurso.AdmissaoEnvio;
import com.example.sicapweb.model.AdmissaoEnvioAssRetorno;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Repository
public class AdmissaoEnvioRepository extends DefaultRepository<AdmissaoEnvio, BigInteger>  {

    public AdmissaoEnvioRepository(EntityManager em) {
        super(em);
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

    public PaginacaoUtil<AdmissaoEnvioAssRetorno> buscarProcessos(Pageable pageable, String searchParams, Integer tipoParams) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";
        search = getSearch(searchParams, tipoParams);
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<AdmissaoEnvio> list = getEntityManager()
                .createNativeQuery("select a.* from AdmissaoEnvio a  where  a.cnpjUnidadeGestora='" + User.getUser(super.request).getUnidadeGestora().getId() +"' "+
                        " and   1=1 " + search + " ORDER BY " + campo, AdmissaoEnvio.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();


        long totalRegistros = countProcessos();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;

        List<AdmissaoEnvioAssRetorno> listc= new ArrayList<AdmissaoEnvioAssRetorno>() ;
        for(Integer i= 0; i < list.size(); i++){
            AdmissaoEnvioAssRetorno pac =new AdmissaoEnvioAssRetorno();
            pac.setNumeroEdital(list.get(i).getEdital().getNumeroEdital());
            pac.setId(list.get(i).getId());
            pac.setDtcriacao(list.get(i).getDataCriacao());
            pac.setStatus(list.get(i).getStatus());
            pac.setEdital(list.get(i).getEdital());
            pac.setProcesso(list.get(i).getProcesso());
            pac.setNumeroEnvio(list.get(i).getNumeroEnvio());
            Integer qt = (Integer)  getEntityManager().createNativeQuery("select count(*) from DocumentoAdmissao a " +
                    "where status > 0  and  a.idEnvio = "+ pac.getId()+ "").getSingleResult();
            pac.setQuantidade(qt);
            listc.add(pac);
        }

        return new PaginacaoUtil<AdmissaoEnvioAssRetorno>(tamanho, pagina, totalPaginas, totalRegistros, listc);
    }

    public Integer countProcessos() {
        return  (Integer)getEntityManager().createNativeQuery("select count(*) from AdmissaoEnvio a  where a.cnpjUnidadeGestora='" + User.getUser(super.request).getUnidadeGestora().getId() +"' "
                ).getSingleResult();
    }


    public PaginacaoUtil<AdmissaoEnvioAssRetorno> buscarProcessosAguardandoAss(Pageable pageable, String searchParams, Integer tipoParams) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";
        search = getSearch(searchParams, tipoParams);
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<AdmissaoEnvio> list = getEntityManager()
                .createNativeQuery("select a.* from AdmissaoEnvio a where  a.cnpjUnidadeGestora='" + User.getUser(super.request).getUnidadeGestora().getId() +"' "+
                        " and  status=2 and not exists(select 1 from AdmissaoEnvioAssinatura ass  where  ass.idEnvio=a.id)  and a.status=2 " + search + " ORDER BY " + campo, AdmissaoEnvio.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();


        long totalRegistros = countProcessosAguardandoAss();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;

        List<AdmissaoEnvioAssRetorno> listc= new ArrayList<AdmissaoEnvioAssRetorno>() ;
        for(Integer i= 0; i < list.size(); i++){
            AdmissaoEnvioAssRetorno pac =new AdmissaoEnvioAssRetorno();
            pac.setNumeroEdital(list.get(i).getEdital().getNumeroEdital());
            pac.setId(list.get(i).getId());
            pac.setDtcriacao(list.get(i).getDataCriacao());
            pac.setStatus(list.get(i).getStatus());
            pac.setEdital(list.get(i).getEdital());
            pac.setNumeroEnvio(list.get(i).getNumeroEnvio());
            Integer qt = (Integer)  getEntityManager().createNativeQuery("select count(*) from DocumentoAdmissao a " +
                    "where status > 0 and    a.idEnvio = "+ pac.getId()+ "").getSingleResult();
            pac.setQuantidade(qt);
            listc.add(pac);
        }

        return new PaginacaoUtil<AdmissaoEnvioAssRetorno>(tamanho, pagina, totalPaginas, totalRegistros, listc);
    }



    public Integer countProcessosAguardandoAss() {
        return (Integer) getEntityManager().createNativeQuery("select count(*) from AdmissaoEnvio a where  a.cnpjUnidadeGestora='" + User.getUser(super.request).getUnidadeGestora().getId() +"' "+
                " and  a.status=2 ").getSingleResult();
    }

    public List<AdmissaoEnvio> GetEmAbertoByEdital(BigInteger idedital){
        return  getEntityManager().createNativeQuery("select top 1 a.* from AdmissaoEnvio a where  a.cnpjUnidadeGestora='" + User.getUser(super.request).getUnidadeGestora().getId() +"' "+
                " and  a.processo is null and   a.idEdital ="+idedital, AdmissaoEnvio.class).getResultList();

    }



    public List<Map<String,Object>> getValidInfoEnvio(BigInteger idEdital) {

        List<Map<String, Object>> retorno = new ArrayList<Map<String, Object>>();

        try {

            List<Object[]> list = entityManager.createNativeQuery(

                    "select  ev.id idvaga,ev.codigoVaga, c.nomeCargo, ev.especialidadeVaga,  ev.tipoConcorrencia,  cast(ev.quantidade as INTEGER) quantidade , count(1) qt_aprov, min( cast(classificacao as INTEGER)) min_classif, " +
                            "        max(cast(classificacao as INTEGER)) max_classif,  sum(case when da.status=1 then 1 else 0 end) ct_nao_anexados " +
                            "from dbo.AdmissaoEnvio pa  join dbo.DocumentoAdmissao da on pa.id=da.idEnvio   and da.status> 0 " +
                            "join dbo.EditalAprovado EA on da.idAprovado = ea.id " +
                            "join dbo.EditalVaga EV on ea.idEditalVaga = ev.id " +
                            "join dbo.Cargo c on ev.idCargo = c.id " +
                            "where pa.idEdital =:idEdital " +
                            " group by ev.id ,ev.codigoVaga, c.nomeCargo, ev.especialidadeVaga,  ev.tipoConcorrencia, ev.quantidade ").setParameter("idEdital",idEdital).getResultList();


            for (Object[] obj : list) {

                Map<String, Object> mapa = new HashMap<String, Object>();

                mapa.put("idvaga", (BigDecimal) obj[0]);
                mapa.put("codigoVaga", (String) obj[1]);
                mapa.put("nomeCargo", (String) obj[2]);
                mapa.put("especialidadeVaga", (String) obj[3]);
                mapa.put("tipoConcorrencia", (Integer) obj[4]);
                mapa.put("quantidade", (Integer) obj[5]);
                mapa.put("qt_aprov", (Integer) obj[6]);
                mapa.put("min_classif", (Integer) obj[7]);
                mapa.put("max_classif", (Integer) obj[8]);
                mapa.put("ct_nao_anexados", (Integer) obj[9]);
                String descricao_tipo ="";
                switch ( ((Integer)mapa.get("tipoConcorrencia")) ){
                    case 1:
                        descricao_tipo="Geral";
                        break;
                    case 2:
                        descricao_tipo="PNE";
                        break;
                    case 3:
                        descricao_tipo="Cota racial";
                        break;
                    default:
                        descricao_tipo="Outros";

                }
                if ( ((Integer)mapa.get("quantidade")) <  ((Integer)mapa.get("max_classif"))  ){
                    mapa.put("valido", (Boolean) false);
                    mapa.put("ocorrencia", (String) " o numero de aprovados axcedeu o limite estipulado da vaga de codigo "+((String)mapa.get("codigoVaga"))+"-"+((String)mapa.get("nomeCargo"))+"-" +descricao_tipo );
                } else if (((Integer)mapa.get("ct_nao_anexados")) > 0 ){
                    mapa.put("valido", (Boolean) false);
                    mapa.put("ocorrencia", (String) "A vaga de codigo "+((String)mapa.get("codigoVaga"))+"-"+((String)mapa.get("nomeCargo"))+"-" +descricao_tipo+" tem aprovados ao qual não foi anexado documentos!! " );
                }
                else if ( ((Integer)mapa.get("max_classif")) >   ((Integer)mapa.get("qt_aprov"))   ){
                    mapa.put("valido", (Boolean) false);
                    mapa.put("ocorrencia", (String) "A vaga de codigo "+((String)mapa.get("codigoVaga"))+"-"+((String)mapa.get("nomeCargo"))+"-" +descricao_tipo+" não tem os classificados na ordem de classificacão!! " );
                }
                else{
                    mapa.put("valido", (Boolean) true);
                    mapa.put("ocorrencia", (String) "");
                }


                retorno.add(mapa);

            }
            ;

            return retorno;


        } catch (Exception e) {
            throw  new RuntimeException(e.getMessage());
        }
    }


    public Integer getLastNumeroEnvioByEdital(BigInteger idEdital){
        return (Integer) getEntityManager().createNativeQuery("select top 1 ( a.numeroEnvio+1) from AdmissaoEnvio a  " +
                "where  a.idEdital ="+idEdital+ " order by  numeroEnvio desc ").getSingleResult();
    }

}
