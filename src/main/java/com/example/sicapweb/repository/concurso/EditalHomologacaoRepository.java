package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.ap.concurso.Edital;
import br.gov.to.tce.model.ap.concurso.EditalHomologacao;
import br.gov.to.tce.model.ap.concurso.EmpresaOrganizadora;
import com.example.sicapweb.model.EditalConcurso;
import com.example.sicapweb.model.EditalHomologaConcurso;
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
public class EditalHomologacaoRepository extends DefaultRepository<EditalHomologacao, BigInteger> {
    public EditalHomologacaoRepository(EntityManager em) {
        super(em);
    }


    public String getSearch(String searchParams, Integer tipoParams) {
        String search = "";
        //monta pesquisa search
        if (searchParams.length() > 3) {
            if (tipoParams == 0) { //entra para tratar a string
                String arrayOfStrings[] = searchParams.split("=");
                if (arrayOfStrings[0].equals("numeroEdital"))
                    search = " and e." + arrayOfStrings[0] + " LIKE '%" + arrayOfStrings[1] + "%'  ";
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
    public PaginacaoUtil<EditalHomologaConcurso> buscaPaginadaEditaisHomologa(Pageable pageable, String searchParams, Integer tipoParams) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";
        //monta pesquisa search
        search = getSearch(searchParams, tipoParams);
        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");


        List<EditalHomologacao> list = getEntityManager()
                .createNativeQuery("select a.* from EditalHomologacao a " +
                        "join Edital e on a.idEdital = e.id " +
                        "join  ConcursoEnvio env on a.idEdital = env.idEdital  and env.fase=1 and env.status=0 " +
                        "join InfoRemessa i on a.chave = i.chave " +
                        "where   i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "' " + search + " ORDER BY " + campo, EditalHomologacao.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();
        long totalRegistros = countEditaishomologa();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        List<EditalHomologaConcurso> listc= new ArrayList<EditalHomologaConcurso>() ;
        for(Integer i= 0; i < list.size(); i++){
            EditalHomologaConcurso editalHomologaConcurso = new EditalHomologaConcurso();
            editalHomologaConcurso.setId(list.get(i).getId());
            editalHomologaConcurso.setNumeroEdital(list.get(i).getNumeroEdital());
            editalHomologaConcurso.setAto(list.get(i).getAto());
            editalHomologaConcurso.setDataPublicacao(list.get(i).getDataPublicacao());
            editalHomologaConcurso.setDataHomologacao(list.get(i).getDataHomologacao());
            editalHomologaConcurso.setEdital(list.get(i).getEdital());
            editalHomologaConcurso.setNumeroAto(list.get(i).getNumeroAto());
            editalHomologaConcurso.setTipoAto(list.get(i).getTipoAto());
            editalHomologaConcurso.setVeiculoPublicacao(list.get(i).getVeiculoPublicacao());
            listc.add(editalHomologaConcurso);
        }
        return new PaginacaoUtil<EditalHomologaConcurso>(tamanho, pagina, totalPaginas, totalRegistros, listc);
    }

    public Integer countEditaishomologa() {
        Query query = getEntityManager().createNativeQuery("select count(*) from EditalHomologacao a " +
                "join InfoRemessa i on a.chave = i.chave " +
                "where   i.idUnidadeGestora= '"+ User.getUser(super.request).getUnidadeGestora().getId()+ "'");
        return (Integer) query.getSingleResult();
    }

}
