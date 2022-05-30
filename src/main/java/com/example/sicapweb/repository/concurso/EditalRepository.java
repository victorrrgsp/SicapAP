package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.ap.concurso.Edital;
import br.gov.to.tce.model.ap.concurso.EmpresaOrganizadora;
import br.gov.to.tce.model.ap.concurso.documento.DocumentoEdital;
import br.gov.to.tce.model.ap.pessoal.Aposentadoria;
import com.example.sicapweb.model.EditalConcurso;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.ap.concessao.ConcessaoAposentadoriaController;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Repository
public class EditalRepository extends DefaultRepository<Edital, BigInteger> {


    public EditalRepository(EntityManager em) {
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
    public PaginacaoUtil<EditalConcurso> buscaPaginadaEditais(Pageable pageable, String searchParams, Integer tipoParams) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";
        //monta pesquisa search
        search = getSearch(searchParams, tipoParams);
        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");


        List<Edital> list = getEntityManager()
                .createNativeQuery("select a.* from Edital a " +
                        "join InfoRemessa i on a.chave = i.chave " +
                        "where  a.tipoEdital =1  and i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "' " + search + " ORDER BY " + campo, Edital.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();
        long totalRegistros = countEditais();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        List<EditalConcurso> listc= new ArrayList<EditalConcurso>() ;
        for(Integer i= 0; i < list.size(); i++){
            EditalConcurso editalConcurso = new EditalConcurso();
            editalConcurso.setId(list.get(i).getId());
            editalConcurso.setTipoEdital(list.get(i).getTipoEdital());
            editalConcurso.setNumeroEdital(list.get(i).getNumeroEdital());
            editalConcurso.setDataPublicacao(list.get(i).getDataPublicacao());
            editalConcurso.setDataInicioInscricoes(list.get(i).getDataInicioInscricoes());
            editalConcurso.setDataFimInscricoes(list.get(i).getDataFimInscricoes());
            editalConcurso.setPrazoValidade(list.get(i).getPrazoValidade());
            editalConcurso.setVeiculoPublicacao(list.get(i).getVeiculoPublicacao());
            editalConcurso.setCnpjEmpresaOrganizadora(list.get(i).getCnpjEmpresaOrganizadora());
           if  (!list.get(i).getCnpjEmpresaOrganizadora().isEmpty()) {
               EmpresaOrganizadora eo = (EmpresaOrganizadora) getEntityManager()
                       .createNativeQuery("select a.* from empresaOrganizadora a where  cnpjEmpresaOrganizadora='" + list.get(i).getCnpjEmpresaOrganizadora() + "'", EmpresaOrganizadora.class)
                       .getResultList().get(0);
               editalConcurso.setNomEmpresaOrganizadora(eo.getNome() );
               editalConcurso.setValorContratacao(eo.getValor());

           }
            listc.add(editalConcurso);
        }
        return new PaginacaoUtil<EditalConcurso>(tamanho, pagina, totalPaginas, totalRegistros, listc);
    }

    public Integer countEditais() {
        Query query = getEntityManager().createNativeQuery("select count(*) from Edital a " +
                "join InfoRemessa i on a.chave = i.chave " +
                "where a.tipoEdital =1  and  i.idUnidadeGestora= '"+ User.getUser(super.request).getUnidadeGestora().getId()+ "'");
        return (Integer) query.getSingleResult();
    }
    public Edital buscarEditalPorNumero(String numeroEdital) {
        List<Edital> list = getEntityManager().createNativeQuery("select * from Edital ed" +
                " where numeroEdital = '" + numeroEdital + "'", Edital.class).getResultList();
        if (list.size()>0 ){
            return list.get(0);
        }
        else {
            return null;
        }
    }

    public List<Edital> buscarEditaisNaoHomologados() {
        List<Edital> list = getEntityManager().createNativeQuery("select * from Edital ed where " +
                "not exists (select * from EditalHomologacao eh where ed.id = eh.idEdital)", Edital.class).getResultList();
        return list;
    }

}
