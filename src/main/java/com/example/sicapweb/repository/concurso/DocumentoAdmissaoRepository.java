package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.ap.concurso.Edital;
import br.gov.to.tce.model.ap.concurso.EmpresaOrganizadora;
import br.gov.to.tce.model.ap.concurso.documento.DocumentoAdmissao;
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
public class DocumentoAdmissaoRepository extends DefaultRepository<DocumentoAdmissao, BigInteger>  {

    public DocumentoAdmissaoRepository(EntityManager em) {
        super(em);
    }

    public PaginacaoUtil<DocumentoAdmissao> buscaPaginadaApr(Pageable pageable,  BigInteger id) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");


        List<DocumentoAdmissao> list = getEntityManager()
                .createNativeQuery("select a.* from DocumentoAdmissao a " +
                        "where status=2 and idAdmissao is null  and a.idProcessoAdmissao = " + id + " " + " ORDER BY " + campo, DocumentoAdmissao.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();
        long totalRegistros = this.countApr(id);
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        return new PaginacaoUtil<DocumentoAdmissao>(tamanho, pagina, totalPaginas, totalRegistros, list);
    }


    public Integer countApr(BigInteger id) {
        Query query = getEntityManager().createNativeQuery("select count(*) from DocumentoAdmissao a " +
                "where status =2 and idAdmissao is null  and a.idProcessoAdmissao = "+ id+ "");
        return (Integer) query.getSingleResult();
    }


    public PaginacaoUtil<DocumentoAdmissao> buscaPaginadaAdm(Pageable pageable,  BigInteger id) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");


        List<DocumentoAdmissao> list = getEntityManager()
                .createNativeQuery("select a.* from DocumentoAdmissao a " +
                        "where status =2 and  idAdmissao is not  null  and a.idProcessoAdmissao = " + id + " " + " ORDER BY " + campo, DocumentoAdmissao.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();
        long totalRegistros = this.countAdm(id);
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        return new PaginacaoUtil<DocumentoAdmissao>(tamanho, pagina, totalPaginas, totalRegistros, list);
    }


    public Integer countAdm(BigInteger id) {
        Query query = getEntityManager().createNativeQuery("select count(*) from DocumentoAdmissao a " +
                "where status =2 and  idAdmissao is not  null  and a.idProcessoAdmissao = "+ id+ "");
        return (Integer) query.getSingleResult();
    }

    public List<DocumentoAdmissao> getAprovadosSemAdmissao(BigInteger id){
        return getEntityManager()
                .createNativeQuery("select a.* from DocumentoAdmissao a " +
                        "where  status=2 idAdmissao is  null  and a.idProcessoAdmissao = " + id + " " + " ORDER BY id " , DocumentoAdmissao.class)
                .getResultList();

    }


    public List<DocumentoAdmissao> getAprovadosComAdmissao(BigInteger id){
        String i ="";
        return getEntityManager()
                .createNativeQuery("select a.* from DocumentoAdmissao a " +
                        "where status = 2 and idAdmissao is not  null  and a.idProcessoAdmissao = " + id + " " + " ORDER BY id " , DocumentoAdmissao.class)
                .getResultList();

    }

}
