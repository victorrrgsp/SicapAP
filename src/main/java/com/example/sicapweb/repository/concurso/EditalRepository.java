package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.ap.concurso.ConcursoEnvio;
import br.gov.to.tce.model.ap.concurso.Edital;
import br.gov.to.tce.model.ap.concurso.EmpresaOrganizadora;
import com.example.sicapweb.model.EditalFinalizado;
import com.example.sicapweb.model.EditalConcurso;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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
        long totalRegistros = this.countEditais();
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
           if  ( list.get(i).getCnpjEmpresaOrganizadora()!=null ) {
               try {
                   List<EmpresaOrganizadora> leo =  getEntityManager()
                           .createNativeQuery("select a.* from empresaOrganizadora a where  cnpjEmpresaOrganizadora='" + list.get(i).getCnpjEmpresaOrganizadora() + "'", EmpresaOrganizadora.class)
                           .getResultList();

                   if (leo.size()>0){
                       EmpresaOrganizadora eo =(EmpresaOrganizadora) leo.get(0);
                   editalConcurso.setNomEmpresaOrganizadora(eo.getNome());
                   editalConcurso.setValorContratacao(eo.getValor());
                   }
               }catch (Exception e ){

               }

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
        List<Edital> list = getEntityManager().createNativeQuery("select ed.* from Edital ed" +
                "join InfoRemessa i on ed.chave = i.chave " +
                " where numeroEdital = '" + numeroEdital + "' and  i.idUnidadeGestobra= '"+ User.getUser(super.request).getUnidadeGestora().getId()+ "'", Edital.class).getResultList();
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



    public PaginacaoUtil<EditalFinalizado> buscarEditaiFinalizados(Pageable pageable, String searchParams, Integer tipoParams) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";
        search = getSearch(searchParams, tipoParams);
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<Edital> list = getEntityManager()
                .createNativeQuery("select a.* from Edital a " +
                        "join InfoRemessa i on a.chave = i.chave " +
                        "join ConcursoEnvio e on a.id = e.idEdital and e.fase=1 and e.Status=3 " +
                        "where  a.tipoEdital =1   and i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "' " + search + " ORDER BY " + campo, Edital.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();


        long totalRegistros = list.size();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        List<EditalFinalizado> listc= new ArrayList<EditalFinalizado>() ;
        for(Integer i= 0; i < list.size(); i++){
            EditalFinalizado edf =new EditalFinalizado();
            edf.setNumeroEdital(list.get(i).getNumeroEdital());
            edf.setProcesso(null);
            edf.setData(list.get(i).getDataPublicacao());
            edf.setEdital((Edital)list.get(i));
            List<ConcursoEnvio> leo =  getEntityManager()
                    .createNativeQuery("select E.* from ConcursoEnvio E where  e.fase=1 and e.Status=3 and  idEdital=" + list.get(i).getId() + "", ConcursoEnvio.class)
                    .getResultList();

            if (leo.size()>0){
                ConcursoEnvio eo = leo.get(0);
                edf.setProcesso(eo.getProcesso());
            }
            listc.add(edf);
        }
        return new PaginacaoUtil<EditalFinalizado>(tamanho, pagina, totalPaginas, totalRegistros, listc);
    }

}
