package com.example.sicapweb.repository;

import br.gov.to.tce.model.ap.concurso.Edital;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public class EditalRepository extends DefaultRepository<Edital, BigInteger> {

    public PaginacaoUtil<Edital> buscaPaginada(int pagina, String direcao) {
        int tamanho = 10;
        int inicio = (pagina -1) * tamanho;
        List<Edital> editalList = getEntityManager()
                .createNativeQuery("select e.* from Edital e order by e.numeroEdital " + direcao, Edital.class)
                .setFirstResult(inicio)
                .setMaxResults(tamanho)
                .getResultList();

        long totalRegistros = count();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        return new PaginacaoUtil<>(tamanho, pagina, totalPaginas, direcao, editalList);
    }

    public long count() {
        return getEntityManager().createQuery("select count(*) from Edital", Long.class).getSingleResult();
    }
}
