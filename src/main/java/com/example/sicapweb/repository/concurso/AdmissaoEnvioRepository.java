package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.ap.concurso.AdmissaoEnvio;
import br.gov.to.tce.model.ap.concurso.EditalVaga;
import com.example.sicapweb.model.AdmissaoEnvioAssRetorno;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.util.StaticMethods;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

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
                .createNativeQuery("select a.* from AdmissaoEnvio a  where  a.cnpjUnidadeGestora='" + user.getUser(super.request).getUnidadeGestora().getId() +"' "+
                        " and   1=1 " + search + " ORDER BY " + campo, AdmissaoEnvio.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();
        long totalRegistros = countProcessos(search);
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        List<AdmissaoEnvioAssRetorno> listDtosAdmissaoRetorno= new ArrayList<AdmissaoEnvioAssRetorno>() ;
        for(Integer i= 0; i < list.size(); i++){
            AdmissaoEnvioAssRetorno dtoAssinaturaEnvio =new AdmissaoEnvioAssRetorno();
            dtoAssinaturaEnvio.setNumeroEdital(list.get(i).getEdital().getNumeroEdital());
            dtoAssinaturaEnvio.setId(list.get(i).getId());
            dtoAssinaturaEnvio.setDtcriacao(list.get(i).getDataCriacao());
            dtoAssinaturaEnvio.setStatus(list.get(i).getStatus());
            dtoAssinaturaEnvio.setEdital(list.get(i).getEdital());
            dtoAssinaturaEnvio.setProcesso(list.get(i).getProcesso());
            dtoAssinaturaEnvio.setNumeroEnvio(list.get(i).getNumeroEnvio());
            dtoAssinaturaEnvio.setQuantidade((Integer)  getEntityManager().createNativeQuery("select count(*) from DocumentoAdmissao a " +
                    "where status > 0  and  a.idEnvio = "+ dtoAssinaturaEnvio.getId()+ "").getSingleResult());
            listDtosAdmissaoRetorno.add(dtoAssinaturaEnvio);
        }

        return new PaginacaoUtil<AdmissaoEnvioAssRetorno>(tamanho, pagina, totalPaginas, totalRegistros, listDtosAdmissaoRetorno);
    }

    public Integer countProcessos(String search) {
        return  (Integer)getEntityManager().createNativeQuery("select count(*) from AdmissaoEnvio a  where a.cnpjUnidadeGestora='" + user.getUser(super.request).getUnidadeGestora().getId() +"' "+search
                ).getSingleResult();
    }

    public PaginacaoUtil<AdmissaoEnvioAssRetorno> buscarProcessosAguardandoAss(Pageable pageable, String searchParams, Integer tipoParams) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";
        search = getSearch(searchParams, tipoParams);
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<AdmissaoEnvio> list = getEntityManager()
                .createNativeQuery("select a.* from AdmissaoEnvio a where  a.cnpjUnidadeGestora='" + user.getUser(super.request).getUnidadeGestora().getId() +"' "+
                        " and  status=2 and not exists(select 1 from AdmissaoEnvioAssinatura ass  where  ass.idEnvio=a.id)  and a.status=2 " + search + " ORDER BY " + campo, AdmissaoEnvio.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();


        long totalRegistros = countProcessosAguardandoAss(search);
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;

        List<AdmissaoEnvioAssRetorno> listDtosAdmissaoRetorno= new ArrayList<AdmissaoEnvioAssRetorno>() ;
        for(Integer i= 0; i < list.size(); i++){
            AdmissaoEnvioAssRetorno dtoAssinaturaEnvio =new AdmissaoEnvioAssRetorno();
            dtoAssinaturaEnvio.setNumeroEdital(list.get(i).getEdital().getNumeroEdital());
            dtoAssinaturaEnvio.setId(list.get(i).getId());
            dtoAssinaturaEnvio.setDtcriacao(list.get(i).getDataCriacao());
            dtoAssinaturaEnvio.setStatus(list.get(i).getStatus());
            dtoAssinaturaEnvio.setEdital(list.get(i).getEdital());
            dtoAssinaturaEnvio.setNumeroEnvio(list.get(i).getNumeroEnvio());
            dtoAssinaturaEnvio.setQuantidade((Integer)  getEntityManager().createNativeQuery("select count(*) from DocumentoAdmissao a " +
                    "where status > 0 and    a.idEnvio = "+ dtoAssinaturaEnvio.getId()+ "").getSingleResult());
            listDtosAdmissaoRetorno.add(dtoAssinaturaEnvio);
        }

        return new PaginacaoUtil<AdmissaoEnvioAssRetorno>(tamanho, pagina, totalPaginas, totalRegistros, listDtosAdmissaoRetorno);
    }

    public Integer countProcessosAguardandoAss(String search) {
        return (Integer) getEntityManager().createNativeQuery("select count(*) from AdmissaoEnvio a where  a.cnpjUnidadeGestora='" + user.getUser(super.request).getUnidadeGestora().getId() +"' "+
                " and  a.status=2 "+search).getSingleResult();
    }

    public List<AdmissaoEnvio> GetEmAbertoByEdital(BigInteger idedital){
            return getEntityManager().createNativeQuery("select top 1 a.* from AdmissaoEnvio a where  a.cnpjUnidadeGestora='" + user.getUser(super.request).getUnidadeGestora().getId() + "' " +
                    " and  a.processo is null and   a.idEdital =" + idedital, AdmissaoEnvio.class).getResultList();
    }

    public List<Map<String,Object>> getValidInfoEnvio(BigInteger idEdital) {

        List<Map<String, Object>> validacoesVagaAprovado = new ArrayList<Map<String, Object>>();

        try {

            var query = entityManager.createNativeQuery(
                            "select ev.id                                          idvaga,\n" +
                            "       ev.codigoVaga,\n" +
                            "       c.nomeCargo,\n" +
                            "       ev.especialidadeVaga,\n" +
                            "       ev.tipoConcorrencia,\n" +
                            "       count(EA.id)                                       qt_aprov,\n" +
                            "       min(cast(classificacao as INTEGER))            min_classif,\n" +
                            "       max(cast(classificacao as INTEGER))            max_classif,\n" +
                            "       sum(case when da.status = 1 then 1 else 0 end) ct_nao_anexados,\n" +
                            "       count(Desligamento.id)                         qt_aprovDesligado,\n" +
                            "       count(da.opcaoDesistencia)                     qt_desistencia,\n" +
                            "       COUNT(CASE\n" +
                            "                WHEN da.opcaoDesistencia = 3 THEN 3\n" +
                            "                WHEN da.opcaoDesistencia = 6 THEN 6\n" +
                            "                ELSE NULL\n" +
                            "            END) AS qt_desistencia_prorogacao,\n" +
                            "       COUNT(CASE\n" +
                            "                WHEN da.opcaoDesistencia = 3 THEN null\n" +
                            "                WHEN da.opcaoDesistencia = 6 THEN null\n" +
                            "                WHEN da.opcaoDesistencia is null THEN null\n" +
                            "                ELSE 1\n" +
                            "            END) AS qt_desistencia_desclasificado " +
                            "from dbo.AdmissaoEnvio pa\n" +
                            "         join dbo.DocumentoAdmissao da on pa.id = da.idEnvio and da.status > 0\n" +
                            "         join dbo.EditalAprovado EA on da.idAprovado = ea.id\n" +
                            "         join dbo.EditalVaga ev on ea.idEditalVaga = ev.id\n" +
                            "         join dbo.Cargo c on ev.idCargo = c.id\n" +
                            "         left join dbo.Admissao on da.idAdmissao = Admissao.id\n" +
                            "         left join dbo.Desligamento on Admissao.id = Desligamento.idAdmissao\n" +
                            "where pa.idEdital = :idEdital\n" +
                            "group by ev.id, ev.codigoVaga, c.nomeCargo, ev.especialidadeVaga, ev.tipoConcorrencia, ev.quantidade").setParameter("idEdital",idEdital);
            var list = StaticMethods.getHashmapFromQuery(query);

//            for (Object[] obj : list) {
            for (int i = 0; i < list.size(); i++) {

                Map<String, Object> vagaAprovado = list.get(i);

                //quantidade= qtVagas +(qtDesistencias + qtDesligados)
                {
                    Integer aux = ((Integer) vagaAprovado.get("qtvagas")) + ((Integer) vagaAprovado.get("qt_desistencia_desclasificado")) + ((Integer) vagaAprovado.get("qtDesligados"));
                    vagaAprovado.put("quantidade", aux);
                }

                String nomeTipoConcorrencia =  Arrays.stream(EditalVaga.TipoConcorrencia.values()).filter(tipoConcorrencia -> tipoConcorrencia.getValor()==vagaAprovado.get("tipoConcorrencia")).collect( Collectors.toList()).get(0).name();

                // if ( ((Integer)vagaAprovado.get("quantidade")) <  ((Integer)vagaAprovado.get("max_classif"))  ){
                //     vagaAprovado.put("valido",  false);
                //     vagaAprovado.put("ocorrencia",  " o numero de aprovados axcedeu o limite estipulado da vaga de codigo "+(vagaAprovado.get("codigoVaga"))+"-"+(vagaAprovado.get("nomeCargo"))+"-" +nomeTipoConcorrencia );
                // } else 
                if (((Integer)vagaAprovado.get("ct_nao_anexados")) > 0 ){
                    vagaAprovado.put("valido", false);
                    vagaAprovado.put("ocorrencia",  "A vaga de código "+(vagaAprovado.get("codigoVaga"))+"-"+(vagaAprovado.get("nomeCargo"))+"-" +nomeTipoConcorrencia+" tem aprovados ao qual não foi anexado documentos!! " );
                }
                else if ( ((Integer)vagaAprovado.get("max_classif")) >   ((Integer)vagaAprovado.get("qt_aprov"))   ){
                    vagaAprovado.put("valido",  false);
                    vagaAprovado.put("ocorrencia",  "A vaga de código "+(vagaAprovado.get("codigoVaga"))+"-"+(vagaAprovado.get("nomeCargo"))+"-" +nomeTipoConcorrencia+" não tem os classificados na ordem de classificacão!! " );
                }
                else{
                    vagaAprovado.put("valido",  true);
                    vagaAprovado.put("ocorrencia", "");
                }
                validacoesVagaAprovado.add(vagaAprovado);
            }
            return validacoesVagaAprovado;

        } catch (Exception e) {
            throw  new RuntimeException("Problema ao validar aprovados e vagas!! Entre em contato com o administrador do Sicap AP!!");
        }
    }

    public Integer getLastNumeroEnvioByEdital(BigInteger idEdital){
        try {
            return (Integer) getEntityManager().createNativeQuery("select top 1 ( a.numeroEnvio+1) from AdmissaoEnvio a  " +
                    "where  a.idEdital =" + idEdital + " order by  numeroEnvio desc ").getSingleResult();
        } catch (NoResultException e){
            return 1;
        }
    }

}
