package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.ap.concurso.ProcessoAdmissao;
import com.example.sicapweb.model.AdmissaoEnvioAssRetorno;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProcessoAdmissaoRepository  extends DefaultRepository<ProcessoAdmissao, BigInteger>  {

    public ProcessoAdmissaoRepository(EntityManager em) {
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

        List<ProcessoAdmissao> list = getEntityManager()
                .createNativeQuery("select a.* from ProcessoAdmissao a " +
                        "where  a.cnpjEmpresaOrganizadora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "' " + search + " ORDER BY " + campo, ProcessoAdmissao.class)
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
            Integer qt = (Integer)  getEntityManager().createNativeQuery("select count(*) from DocumentoAdmissao a " +
                    "where   a.idProcessoAdmissao = "+ pac.getId()+ "").getSingleResult();
            pac.setQuantidade(qt);
            listc.add(pac);
        }

        return new PaginacaoUtil<AdmissaoEnvioAssRetorno>(tamanho, pagina, totalPaginas, totalRegistros, listc);
    }

    public Integer countProcessos() {
        Query query = getEntityManager().createNativeQuery("select count(*) from ProcessoAdmissao a " +
                "where   a.cnpjEmpresaOrganizadora = '"+ User.getUser(super.request).getUnidadeGestora().getId()+ "'");
        return (Integer) query.getSingleResult();
    }


    public PaginacaoUtil<AdmissaoEnvioAssRetorno> buscarProcessosAguardandoAss(Pageable pageable, String searchParams, Integer tipoParams) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";
        search = getSearch(searchParams, tipoParams);
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<ProcessoAdmissao> list = getEntityManager()
                .createNativeQuery("select a.* from ProcessoAdmissao a " +
                        "where not exists(select 1 from AdmissaoEnvioAssinatura ass  where  ass.idProcesso=a.id)  and a.status=2 and  a.cnpjEmpresaOrganizadora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "' " + search + " ORDER BY " + campo, ProcessoAdmissao.class)
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
            Integer qt = (Integer)  getEntityManager().createNativeQuery("select count(*) from DocumentoAdmissao a " +
                    "where   a.idProcessoAdmissao = "+ pac.getId()+ "").getSingleResult();
            pac.setQuantidade(qt);
            listc.add(pac);
        }

        return new PaginacaoUtil<AdmissaoEnvioAssRetorno>(tamanho, pagina, totalPaginas, totalRegistros, listc);
    }



    public Integer countProcessosAguardandoAss() {
        Query query = getEntityManager().createNativeQuery("select count(*) from ProcessoAdmissao a " +
                "where a.status=2 and   a.cnpjEmpresaOrganizadora = '"+ User.getUser(super.request).getUnidadeGestora().getId()+ "'");
        return (Integer) query.getSingleResult();
    }

}
