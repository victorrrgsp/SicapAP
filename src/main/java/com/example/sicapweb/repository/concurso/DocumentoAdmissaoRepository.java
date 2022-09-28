package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.ap.concurso.documento.DocumentoAdmissao;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;

@Repository
public class DocumentoAdmissaoRepository extends DefaultRepository<DocumentoAdmissao, BigInteger>  {

    public DocumentoAdmissaoRepository(EntityManager em) {
        super(em);
    }

    public PaginacaoUtil<DocumentoAdmissao> buscaPaginadaAprovadosSemAdmissao(Pageable pageable, BigInteger id) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<DocumentoAdmissao> listAprovadosSemAdmissao = getEntityManager()
                .createNativeQuery("select a.* from DocumentoAdmissao a " +
                        "where status>0 and idAdmissao is null  and a.idEnvio = " + id + " " + " ORDER BY " + campo, DocumentoAdmissao.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();
        long totalRegistros = this.QuantidadeDocumentoAprovadosSemAdmissao(id);
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        return new PaginacaoUtil<>(tamanho, pagina, totalPaginas, totalRegistros, listAprovadosSemAdmissao);
    }


    public Integer QuantidadeDocumentoAprovadosSemAdmissao(BigInteger id) {
        return (Integer) getEntityManager().createNativeQuery("select count(*) from DocumentoAdmissao a " +
                "where status>0 and idAdmissao is null  and a.idEnvio = "+ id+ "").getSingleResult();
    }


    public PaginacaoUtil<DocumentoAdmissao> buscaPaginadaAprovadosComAdmissao(Pageable pageable, BigInteger id) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<DocumentoAdmissao> listDocumentosAdmissao = getEntityManager()
                .createNativeQuery("select a.* from DocumentoAdmissao a " +
                        "where status>0 and  idAdmissao is not  null  and a.idEnvio = " + id + " " + " ORDER BY " + campo, DocumentoAdmissao.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();
        long totalRegistros = this.quantidadeAprovadosComAdmissao(id);
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        return new PaginacaoUtil<>(tamanho, pagina, totalPaginas, totalRegistros, listDocumentosAdmissao);
    }


    public Integer quantidadeAprovadosComAdmissao(BigInteger id) {
        return (Integer) getEntityManager().createNativeQuery("select count(*) from DocumentoAdmissao a " +
                "where status>0 and  idAdmissao is not  null  and a.idEnvio = "+ id+ "") .getSingleResult();
    }

    public List<DocumentoAdmissao> getAprovadosSemAdmissao(BigInteger id){
        return getEntityManager()
                .createNativeQuery("select a.* from DocumentoAdmissao a " +
                        "where  status>0 and idAdmissao is  null  and a.idEnvio = " + id + " " + " ORDER BY id " , DocumentoAdmissao.class)
                .getResultList();

    }


    public List<DocumentoAdmissao> getAprovadosComAdmissao(BigInteger id){
        return getEntityManager()
                .createNativeQuery("select a.* from DocumentoAdmissao a " +
                        "where status>0 and idAdmissao is not  null  and a.idEnvio = " + id + " " + " ORDER BY id " , DocumentoAdmissao.class)
                .getResultList();

    }

    public List<DocumentoAdmissao> getDocumenttosAdmissaoByIdAprovado(BigInteger id){
        return getEntityManager()
                .createNativeQuery("select a.* from DocumentoAdmissao a " +
                        "where status>0 and idAdmissao is not  null  and a.idAprovado = " + id + " " + " ORDER BY id " , DocumentoAdmissao.class)
                .getResultList();

    }

}
