package com.example.sicapweb.repository.geral;

import br.gov.to.tce.model.ap.relacional.Lei;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;

@Repository
public class LeiRepository extends DefaultRepository<Lei, BigInteger> {
    public LeiRepository(EntityManager em) {
        super(em);
    }

    public List<Lei> buscarDocumentoLei(BigInteger id) {
        return getEntityManager().createNativeQuery(
                        "select * from lei l where l.id = " + id, Lei.class)
                .getResultList();
    }
    public PaginacaoUtil<Lei> buscaPaginada(Pageable pageable, String searchParams, Integer tipoParams) {

        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";

        //monta pesquisa search
        if (searchParams.length() > 3) {

            if (tipoParams == 0) { //entra para tratar a string
                String arrayOfStrings[] = searchParams.split("=");
                search = " WHERE " + arrayOfStrings[0] + " LIKE  '%" + arrayOfStrings[1] + "%'  ";
            } else {
                search = " WHERE " + searchParams + "   ";
            }
        }

        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<Lei> list = getEntityManager()
                .createNativeQuery("select DISTINCT a.* from   (select  id,idcastorfile,dataPublicacao,ementa, numerolei,veiculoPublicacao,idAto,chave  from Lei e" +
                        "     where e.id =(select max(id) from lei where dataPublicacao = e.dataPublicacao and  ementa=e.ementa and  numeroLei = e.numeroLei and veiculoPublicacao = e.veiculoPublicacao and idAto = e.idAto ) ) a " +
                        " join InfoRemessa info on info.chave = a.chave and info.idUnidadeGestora = '"
                        + User.getUser(request).getUnidadeGestora().getId() + "' "  + search + " ORDER BY " + campo, Lei.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();

        long totalRegistros = count();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;

        return new PaginacaoUtil<Lei>(tamanho, pagina, totalPaginas, totalRegistros, list);
    }
}
