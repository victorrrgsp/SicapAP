package com.example.sicapweb.repository.concessao;

import br.gov.to.tce.model.ap.pessoal.Pensao;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;

@Repository
public class PensaoRepository extends DefaultRepository<Pensao, BigInteger> {

    public PensaoRepository(EntityManager em) {
        super(em);
    }

    public PaginacaoUtil<Pensao> buscaPaginadaPensao(Pageable pageable, String searchParams, Integer tipoParams) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";
        //monta pesquisa search
        if (searchParams.length() > 3) {
            if (tipoParams == 0) { //entra para tratar a string
                String arrayOfStrings[] = searchParams.split("=");
                if (arrayOfStrings[0].equals("nomeCargo"))
                    search = " and c." + arrayOfStrings[0] + " LIKE '%" + arrayOfStrings[1] + "%'  ";
                else if (arrayOfStrings[0].equals("numeroAto"))
                    search = " and ato." + arrayOfStrings[0] + " LIKE '%" + arrayOfStrings[1] + "%'  ";
                else if (arrayOfStrings[0].equals("cpfServidor"))
                    search = " and a." + arrayOfStrings[0] + " LIKE '%" + arrayOfStrings[1] + "%'  ";
                else
                    search = " and " + arrayOfStrings[0] + " LIKE '%" + arrayOfStrings[1] + "%'  ";
            } else {
                search = " and " + searchParams + "   ";
            }
        }
        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<Pensao> list = getEntityManager()
                .createNativeQuery("select a.* from Pensao a " +
                        "join InfoRemessa i on a.chave = i.chave " +
                        "where a.revisao = 0 " +
                        "and i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "' " + search + " ORDER BY " + campo, Pensao.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();
        long totalRegistros = countPensao();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        return new PaginacaoUtil<Pensao>(tamanho, pagina, totalPaginas, totalRegistros, list);
    }

    public Integer countPensao() {
        Query query = getEntityManager().createNativeQuery("select count(*) from Pensao a " +
                "join InfoRemessa i on a.chave = i.chave " +
                "where a.revisao = 0 and i.idUnidadeGestora= '"+ User.getUser(super.request).getUnidadeGestora().getId()+ "'");
        return (Integer) query.getSingleResult();
    }

    public List<Pensao> buscarPensao() {
        return getEntityManager().createNativeQuery(
                        "select * from Pensao where revisao = 0", Pensao.class)
                .getResultList();
    }

    public PaginacaoUtil<Pensao> buscaPaginadaPensaoRevisao(Pageable pageable, String searchParams, Integer tipoParams) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";
        //monta pesquisa search
        if (searchParams.length() > 3) {
            if (tipoParams == 0) { //entra para tratar a string
                String arrayOfStrings[] = searchParams.split("=");
                if (arrayOfStrings[0].equals("nomeCargo"))
                    search = " and c." + arrayOfStrings[0] + " LIKE '%" + arrayOfStrings[1] + "%'  ";
                else if (arrayOfStrings[0].equals("numeroAto"))
                    search = " and ato." + arrayOfStrings[0] + " LIKE '%" + arrayOfStrings[1] + "%'  ";
                else if (arrayOfStrings[0].equals("cpfServidor"))
                    search = " and a." + arrayOfStrings[0] + " LIKE '%" + arrayOfStrings[1] + "%'  ";
                else
                    search = " and " + arrayOfStrings[0] + " LIKE '%" + arrayOfStrings[1] + "%'  ";
            } else {
                search = " and " + searchParams + "   ";
            }
        }
        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<Pensao> list = getEntityManager()
                .createNativeQuery("select a.* from Pensao a " +
                        "join InfoRemessa i on a.chave = i.chave " +
                        "where a.revisao = 1 " +
                        "and i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "' " + search + " ORDER BY " + campo, Pensao.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();
        long totalRegistros = countPensaoRevisao();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        return new PaginacaoUtil<Pensao>(tamanho, pagina, totalPaginas, totalRegistros, list);
    }

    public Integer countPensaoRevisao() {
        Query query = getEntityManager().createNativeQuery("select count(*) from Pensao a " +
                "join InfoRemessa i on a.chave = i.chave " +
                "where a.revisao = 1 and i.idUnidadeGestora= '"+ User.getUser(super.request).getUnidadeGestora().getId()+ "'");
        return (Integer) query.getSingleResult();
    }

    public List<Pensao> buscarPensaoRevisao() {
        return getEntityManager().createNativeQuery(
                "select * from Pensao where revisao = 1", Pensao.class)
                .getResultList();
    }
}
