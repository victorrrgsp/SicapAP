package com.example.sicapweb.repository.concessao;

import br.gov.to.tce.model.ap.pessoal.Aposentadoria;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;

@Repository
public class AposentadoriaRepository extends DefaultRepository<Aposentadoria, BigInteger> {

    public AposentadoriaRepository(EntityManager em) {
        super(em);
    }

    public String getSearch(String searchParams, Integer tipoParams) {
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
        return search;
    }

    public PaginacaoUtil<Aposentadoria> buscaPaginadaAposentadorias(Pageable pageable, String searchParams, Integer tipoParams) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";
        //monta pesquisa search
        search = getSearch(searchParams, tipoParams);
        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<Aposentadoria> list = getEntityManager()
                .createNativeQuery("select a.* from Aposentadoria a " +
                        "join InfoRemessa i on a.chave = i.chave " +
                        "join Admissao ad on ad.id = a.id " +
                        "join Cargo c on c.id = ad.idCargo " +
                        "join Servidor s on s.cpfServidor = a.cpfServidor " +
                        "join Ato ato on ato.id = a.idAto " +
                        "where a.reversao = 0 and a.revisao = 0 and a.tipoAposentadoria not in (6,7) " +
                        "and i.idUnidadeGestora = '" + User.getUser().getUnidadeGestora().getId() + "' " + search + " ORDER BY " + campo, Aposentadoria.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();
        long totalRegistros = count();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        return new PaginacaoUtil<Aposentadoria>(tamanho, pagina, totalPaginas, totalRegistros, list);
    }

    public List<Aposentadoria> buscarAposentadorias() {
        return getEntityManager().createNativeQuery(
                "select a.* from Aposentadoria a " +
                        "join InfoRemessa i on a.chave = i.chave " +
                        "where a.reversao = 0 and a.revisao = 0 and a.tipoAposentadoria not in (6,7) " +
                        "and i.idUnidadeGestora = '" + User.getUser().getUnidadeGestora().getId() + "'", Aposentadoria.class)
                .getResultList();
    }

    public PaginacaoUtil<Aposentadoria> buscaPaginadaPorTipo(Pageable pageable, String searchParams, Integer tipoParams, Integer tipoAposentadoria) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";
        //monta pesquisa search
        search = getSearch(searchParams, tipoParams);
        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<Aposentadoria> list = getEntityManager()
                .createNativeQuery("select a.* from Aposentadoria a " +
                        "join InfoRemessa i on a.chave = i.chave " +
                        "join Admissao ad on ad.id = a.id " +
                        "join Cargo c on c.id = ad.idCargo " +
                        "join Servidor s on s.cpfServidor = a.cpfServidor " +
                        "join Ato ato on ato.id = a.idAto " +
                        "where a.reversao = 0 and a.revisao = 0 and a.tipoAposentadoria = " + tipoAposentadoria +
                        " and i.idUnidadeGestora = '" + User.getUser().getUnidadeGestora().getId() + "' " + search + " ORDER BY " + campo, Aposentadoria.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();
        long totalRegistros = count();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        return new PaginacaoUtil<Aposentadoria>(tamanho, pagina, totalPaginas, totalRegistros, list);
    }

    public List<Aposentadoria> buscarAposentadoriaPorTipo(Integer tipoAposentadoria) {
        return getEntityManager().createNativeQuery(
                "select a.* from Aposentadoria a " +
                        "join InfoRemessa i on a.chave = i.chave " +
                        "where a.reversao = 0 and a.revisao = 0 and a.tipoAposentadoria = " + tipoAposentadoria +
                        " and i.idUnidadeGestora = '" + User.getUser().getUnidadeGestora().getId() + "'", Aposentadoria.class)
                .getResultList();
    }

    public PaginacaoUtil<Aposentadoria> buscaPaginadaAposentadoriaRevisao(Pageable pageable, String searchParams, Integer tipoParams) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";
        //monta pesquisa search
        search = getSearch(searchParams, tipoParams);
        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<Aposentadoria> list = getEntityManager()
                .createNativeQuery("select a.* from Aposentadoria a " +
                        "join InfoRemessa i on a.chave = i.chave " +
                        "join Admissao ad on ad.id = a.id " +
                        "join Cargo c on c.id = ad.idCargo " +
                        "join Servidor s on s.cpfServidor = a.cpfServidor " +
                        "join Ato ato on ato.id = a.idAto " +
                        "where a.reversao = 0 and a.revisao = 1 and a.tipoAposentadoria not in (6,7) " +
                        "and i.idUnidadeGestora = '" + User.getUser().getUnidadeGestora().getId() + "' " + search + " ORDER BY " + campo, Aposentadoria.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();
        long totalRegistros = count();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        return new PaginacaoUtil<Aposentadoria>(tamanho, pagina, totalPaginas, totalRegistros, list);
    }

    public List<Aposentadoria> buscarAposentadoriaRevisao() {
        return getEntityManager().createNativeQuery(
                "select a.* from Aposentadoria a " +
                        "join InfoRemessa i on a.chave = i.chave " +
                        "where a.reversao = 0 and revisao = 1 and tipoAposentadoria not in (6,7) " +
                        "and i.idUnidadeGestora = '" + User.getUser().getUnidadeGestora().getId() + "'", Aposentadoria.class)
                .getResultList();
    }

    public PaginacaoUtil<Aposentadoria> buscaPaginadaRevisaoReserva(Pageable pageable, String searchParams, Integer tipoParams) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";
        //monta pesquisa search
        search = getSearch(searchParams, tipoParams);
        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<Aposentadoria> list = getEntityManager()
                .createNativeQuery("select a.* from Aposentadoria a " +
                        "join InfoRemessa i on a.chave = i.chave " +
                        "join Admissao ad on ad.id = a.id " +
                        "join Cargo c on c.id = ad.idCargo " +
                        "join Servidor s on s.cpfServidor = a.cpfServidor " +
                        "join Ato ato on ato.id = a.idAto " +
                        "where a.reversao = 0 and a.revisao = 1 and a.tipoAposentadoria = "+ Aposentadoria.TipoAposentadoria.Reserva.getValor() +
                        " and i.idUnidadeGestora = '" + User.getUser().getUnidadeGestora().getId() + "' " + search + " ORDER BY " + campo, Aposentadoria.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();
        long totalRegistros = count();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        return new PaginacaoUtil<Aposentadoria>(tamanho, pagina, totalPaginas, totalRegistros, list);
    }

    public List<Aposentadoria> buscarAposentadoriaRevisaoReserva() {
        return getEntityManager().createNativeQuery(
                "select a.* from Aposentadoria a " +
                        "join InfoRemessa i on a.chave = i.chave " +
                        "where revisao = 1 and tipoAposentadoria = " + Aposentadoria.TipoAposentadoria.Reserva.getValor() +
                        " and i.idUnidadeGestora = '" + User.getUser().getUnidadeGestora().getId() + "'", Aposentadoria.class)
                .getResultList();
    }

    public List<Aposentadoria> buscarAposentadoriaRevisaoReforma() {
        return getEntityManager().createNativeQuery(
                "select a.* from Aposentadoria a " +
                        "join InfoRemessa i on a.chave = i.chave " +
                        "where revisao = 1 and tipoAposentadoria = " + Aposentadoria.TipoAposentadoria.Reforma.getValor() +
                        " and i.idUnidadeGestora = '" + User.getUser().getUnidadeGestora().getId() + "'", Aposentadoria.class)
                .getResultList();
    }

    public List<Aposentadoria> buscarReversaoAposentadoriaReserva() {
        return getEntityManager().createNativeQuery(
                "select * from Aposentadoria a " +
                        "join InfoRemessa i on a.chave = i.chave " +
                        "where reversao = 1 and tipoAposentadoria != 7 " +
                        "and i.idUnidadeGestora = '" + User.getUser().getUnidadeGestora().getId() + "'", Aposentadoria.class)
                .getResultList();
    }
}
