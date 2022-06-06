package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.UnidadeGestora;
import br.gov.to.tce.model.ap.concurso.Edital;
import br.gov.to.tce.model.ap.concurso.EditalAprovado;
import br.gov.to.tce.model.ap.concurso.EmpresaOrganizadora;
import com.example.sicapweb.model.EditalAprovadoConcurso;
import com.example.sicapweb.model.EditalConcurso;
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
public class EditalAprovadoRepository extends DefaultRepository<EditalAprovado, BigInteger> {
    public EditalAprovadoRepository(EntityManager em) {
        super(em);
    }



    public PaginacaoUtil<EditalAprovadoConcurso> buscaPaginadaAprovados(Pageable pageable) {

        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<EditalAprovado> list = getEntityManager()
                .createNativeQuery("select a.* from EditalAprovado a " +
                        "join InfoRemessa i on a.chave = i.chave " +
                        "where not exists(select 1 from Admissao ad  where ad.numeroInscricao=a.numeroInscricao)  and  i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "' "  + " ORDER BY " + campo, EditalAprovado.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();
        long totalRegistros = countAprovados();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        List<EditalAprovadoConcurso> listc= new ArrayList<EditalAprovadoConcurso>() ;
        for(Integer i= 0; i < list.size(); i++){
            EditalAprovadoConcurso editalAprovadoConcurso = new EditalAprovadoConcurso();
            editalAprovadoConcurso.setEditalVaga(list.get(i).getEditalVaga());
            editalAprovadoConcurso.setNome(list.get(i).getNome());
            editalAprovadoConcurso.setNumeroEdital(list.get(i).getNumeroEdital());
            editalAprovadoConcurso.setClassificacao(list.get(i).getClassificacao());
            editalAprovadoConcurso.setCodigoVaga(list.get(i).getCodigoVaga());
            editalAprovadoConcurso.setCpf(list.get(i).getCpf());
            editalAprovadoConcurso.setEditalaprovado(list.get(i));
            editalAprovadoConcurso.setSituacao("não definido ainda");
            listc.add(editalAprovadoConcurso);
        }
        return new PaginacaoUtil<EditalAprovadoConcurso>(tamanho, pagina, totalPaginas, totalRegistros, listc);
    }

    public Integer countAprovados() {
        Query query = getEntityManager().createNativeQuery("select count(*) from EditalAprovado a " +
                "join InfoRemessa i on a.chave = i.chave " +
                "where not exists(select 1 from Admissao ad  where ad.numeroInscricao=a.numeroInscricao)  and  i.idUnidadeGestora= '"+ User.getUser(super.request).getUnidadeGestora().getId()+ "'");
        return (Integer) query.getSingleResult();
    }

}
