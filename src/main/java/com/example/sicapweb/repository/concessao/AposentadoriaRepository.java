package com.example.sicapweb.repository.concessao;

import br.gov.to.tce.model.ap.pessoal.Aposentadoria;
import com.example.sicapweb.model.dto.AposentadoriaDTO;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Repository
public class AposentadoriaRepository extends DefaultRepository<Aposentadoria, BigInteger> {

    public AposentadoriaRepository(EntityManager em) {
        super(em);
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // ---------------------------------- Search utilizado por todas as funções ------------------------------------- //
    // -------------------------------------------------------------------------------------------------------------- //
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
    // -------------------------------------------------------------------------------------------------------------- //


    // -------------------------------------------------------------------------------------------------------------- //
    // ----------------------------------------- Concessão Aposentadoria -------------------------------------------- //
    // -------------------------------------------------------------------------------------------------------------- //
    public PaginacaoUtil<AposentadoriaDTO> buscaPaginadaAposentadorias(Pageable pageable, String searchParams, Integer tipoParams) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";
        //monta pesquisa search
        search = getSearch(searchParams, tipoParams);
        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        Query query = getEntityManager()
                .createNativeQuery("select distinct a.cpfServidor, ser.nome, car.nomeCargo, a.tipoAposentadoria, ato.numeroAto, " +
                        " (CASE WHEN ae.status IS NULL THEN 1 ELSE ae.status END) as status, a.id from Aposentadoria a " +
                        "join Admissao ad on ad.id = a.id " +
                        "join Servidor ser on ser.id = ad.idServidor " +
                        "join Cargo car on car.id = ad.idCargo " +
                        "join Ato ato on ato.id = a.idAto " +
                        "left join AdmEnvio ae on ae.idMovimentacao = a.id " +
                        "join InfoRemessa i on a.chave = i.chave " +
                        "where a.reversao = 0 and a.revisao = 0 and a.tipoAposentadoria not in (6,7) " +
                        "and i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "' "
                        + search + " ORDER BY a." + campo)
                .setFirstResult(pagina)
                .setMaxResults(tamanho);

        List<Object> list = (List<Object>) query.getResultList();
        Iterator result = list.iterator();
        List<AposentadoriaDTO> aposentadoriaDTOList = new ArrayList<>();
        convertResult(result, aposentadoriaDTOList);
        long totalRegistros = countAposentadoria();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        return new PaginacaoUtil<AposentadoriaDTO>(tamanho, pagina, totalPaginas, totalRegistros, aposentadoriaDTOList);
    }

    public Integer countAposentadoria() {
        Query query = getEntityManager().createNativeQuery("select count(*) from Aposentadoria a " +
                "join InfoRemessa i on a.chave = i.chave " +
                "where a.reversao = 0 and a.revisao = 0 and a.tipoAposentadoria not in (6,7) " +
                "and i.idUnidadeGestora= '" + User.getUser(super.request).getUnidadeGestora().getId() + "'");
        return (Integer) query.getSingleResult();
    }

    public List<Aposentadoria> buscarAposentadorias() {
        return getEntityManager().createNativeQuery(
                        "select a.* from Aposentadoria a " +
                                "join InfoRemessa i on a.chave = i.chave " +
                                "where a.reversao = 0 and a.revisao = 0 and a.tipoAposentadoria not in (6,7) " +
                                "and i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "'", Aposentadoria.class)
                .getResultList();
    }
    // -------------------------------------------------------------------------------------------------------------- //


    // -------------------------------------------------------------------------------------------------------------- //
    // ----------------------------------------- Concessão Reforma / Reserva----------------------------------------- //
    // -------------------------------------------------------------------------------------------------------------- //
    public PaginacaoUtil<AposentadoriaDTO> buscaPaginadaPorTipo(Pageable pageable, String searchParams, Integer tipoParams, Integer tipoAposentadoria) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";
        //monta pesquisa search
        search = getSearch(searchParams, tipoParams);
        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<Object[]> list = getEntityManager()
                .createNativeQuery("select distinct a.cpfServidor, ser.nome, car.nomeCargo, a.tipoAposentadoria, ato.numeroAto, " +
                        " (CASE WHEN ae.status IS NULL THEN 1 ELSE ae.status END) as status, a.id " +
                        " from Aposentadoria a " +
                        " join Admissao ad on ad.id = a.id " +
                        " join Servidor ser on ser.id = ad.idServidor " +
                        " join Cargo car on car.id = ad.idCargo " +
                        " join Ato ato on ato.id = a.idAto " +
                        " left join AdmEnvio ae on ae.idMovimentacao = a.id " +
                        " join InfoRemessa i on a.chave = i.chave " +
                        " where a.reversao = 0 and a.revisao = 0 and a.tipoAposentadoria = " + tipoAposentadoria +
                        " and i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "' "
                        + search + " ORDER BY " + campo)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();

        Iterator result = list.iterator();
        List<AposentadoriaDTO> aposentadoriaDTOList = new ArrayList<>();
        convertResult(result, aposentadoriaDTOList);

        long totalRegistros = countAposentadoriaPorTipo(tipoAposentadoria);
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        return new PaginacaoUtil<AposentadoriaDTO>(tamanho, pagina, totalPaginas, totalRegistros, aposentadoriaDTOList );
    }

    public Integer countAposentadoriaPorTipo(Integer tipoAposentadoria) {
        Query query = getEntityManager().createNativeQuery("select count(*) from Aposentadoria a " +
                "join InfoRemessa i on a.chave = i.chave " +
                "where a.reversao = 0 and a.revisao = 0 and a.tipoAposentadoria = " + tipoAposentadoria +
                " and i.idUnidadeGestora= '" + User.getUser(super.request).getUnidadeGestora().getId() + "'");
        return (Integer) query.getSingleResult();
    }


    public List<Aposentadoria> buscarAposentadoriaPorTipo(Integer tipoAposentadoria) {
        return getEntityManager().createNativeQuery(
                        "select a.* from Aposentadoria a " +
                                "join InfoRemessa i on a.chave = i.chave " +
                                "where a.reversao = 0 and a.revisao = 0 and a.tipoAposentadoria = " + tipoAposentadoria +
                                " and i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "'", Aposentadoria.class)
                .getResultList();
    }
    // -------------------------------------------------------------------------------------------------------------- //


    // -------------------------------------------------------------------------------------------------------------- //
    // --------------------------------------- Concessão Revisão Aposentadoria -------------------------------------- //
    // -------------------------------------------------------------------------------------------------------------- //
    public PaginacaoUtil<AposentadoriaDTO> buscaPaginadaAposentadoriaRevisao(Pageable pageable, String searchParams, Integer tipoParams) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";
        //monta pesquisa search
        search = getSearch(searchParams, tipoParams);
        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<Object[]> list = getEntityManager()
                .createNativeQuery("select distinct a.cpfServidor, ser.nome, car.nomeCargo, a.tipoAposentadoria, ato.numeroAto, " +
                        " (CASE WHEN ae.status IS NULL THEN 1 ELSE ae.status END) as status, a.id " +
                        " from Aposentadoria a " +
                        " join Admissao ad on ad.id = a.id " +
                        " join Servidor ser on ser.id = ad.idServidor " +
                        " join Cargo car on car.id = ad.idCargo " +
                        " join Ato ato on ato.id = a.idAto " +
                        " left join AdmEnvio ae on ae.idMovimentacao = a.id " +
                        "join InfoRemessa i on a.chave = i.chave " +
                        "where a.reversao = 0 and a.revisao = 1 and a.tipoAposentadoria not in (6,7) " +
                        "and i.idUnidadeGestora = '"
                        + User.getUser(super.request).getUnidadeGestora().getId()
                        + "' " + search + " ORDER BY " + campo)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();

        Iterator result = list.iterator();
        List<AposentadoriaDTO> aposentadoriaDTOList = new ArrayList<>();
        convertResult(result, aposentadoriaDTOList);

        long totalRegistros = countAposentadoriaRevisao();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        return new PaginacaoUtil<AposentadoriaDTO>(tamanho, pagina, totalPaginas, totalRegistros,aposentadoriaDTOList );
    }

    public Integer countAposentadoriaRevisao() {
        Query query = getEntityManager().createNativeQuery("select count(*) from Aposentadoria a " +
                "join InfoRemessa i on a.chave = i.chave " +
                "where a.reversao = 0 and revisao = 1 and tipoAposentadoria not in (6,7) " +
                "and i.idUnidadeGestora= '" + User.getUser(super.request).getUnidadeGestora().getId() + "'");
        return (Integer) query.getSingleResult();
    }

    public List<Aposentadoria> buscarAposentadoriaRevisao() {
        return getEntityManager().createNativeQuery(
                        "select a.* from Aposentadoria a " +
                                "join InfoRemessa i on a.chave = i.chave " +
                                "where a.reversao = 0 and revisao = 1 and tipoAposentadoria not in (6,7) " +
                                "and i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "'", Aposentadoria.class)
                .getResultList();
    }
    // -------------------------------------------------------------------------------------------------------------- //


    // -------------------------------------------------------------------------------------------------------------- //
    // ----------------------------------------- Concessão Revisão Reserva ------------------------------------------ //
    // -------------------------------------------------------------------------------------------------------------- //
    public PaginacaoUtil<AposentadoriaDTO> buscaPaginadaRevisaoReserva(Pageable pageable, String searchParams, Integer tipoParams) {
        int pagina =  Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";
        //monta pesquisa search
        search = getSearch(searchParams, tipoParams);
        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<Object[]> list = getEntityManager()
                .createNativeQuery("select distinct a.cpfServidor, ser.nome, car.nomeCargo, a.tipoAposentadoria, ato.numeroAto, " +
                        " (CASE WHEN ae.status IS NULL THEN 1 ELSE ae.status END) as status, a.id " +
                                " from Aposentadoria a " +
                                " join Admissao ad on ad.id = a.id " +
                                " join Servidor ser on ser.id = ad.idServidor " +
                                " join Cargo car on car.id = ad.idCargo " +
                                " join Ato ato on ato.id = a.idAto " +
                                " left join AdmEnvio ae on ae.idMovimentacao = a.id " +
                                " join InfoRemessa i on a.chave = i.chave " +
                        " where a.reversao = 0 and a.revisao = 1 and a.tipoAposentadoria = " + Aposentadoria.TipoAposentadoria.Reserva.getValor() +
                        " and i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "' " + search + " ORDER BY " + campo )
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();

        Iterator result = list.iterator();
        List<AposentadoriaDTO> aposentadoriaDTOList = new ArrayList<>();
        convertResult(result, aposentadoriaDTOList);

        long totalRegistros = countAposentadoriaRevisaoReserva();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        return new PaginacaoUtil<AposentadoriaDTO>( tamanho , pagina , totalPaginas , totalRegistros , aposentadoriaDTOList );
    }

    public Integer countAposentadoriaRevisaoReserva() {
        Query query = getEntityManager().createNativeQuery("select count(*) from Aposentadoria a " +
                "join InfoRemessa i on a.chave = i.chave " +
                "where revisao = 1 and tipoAposentadoria = " + Aposentadoria.TipoAposentadoria.Reserva.getValor() +
                " and i.idUnidadeGestora= '" + User.getUser(super.request).getUnidadeGestora().getId() + "'");
        return (Integer) query.getSingleResult();
    }

    public List<Aposentadoria> buscarAposentadoriaRevisaoReserva() {
        return getEntityManager().createNativeQuery(
                        "select a.* from Aposentadoria a " +
                                "join InfoRemessa i on a.chave = i.chave " +
                                "where revisao = 1 and tipoAposentadoria = " + Aposentadoria.TipoAposentadoria.Reserva.getValor() +
                                " and i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "'", Aposentadoria.class)
                .getResultList();
    }
    // -------------------------------------------------------------------------------------------------------------- //


    // -------------------------------------------------------------------------------------------------------------- //
    // ----------------------------------------- Concessão Revisão Reforma ------------------------------------------ //
    // -------------------------------------------------------------------------------------------------------------- //
    public PaginacaoUtil<AposentadoriaDTO> buscaPaginadaRevisaoReforma(Pageable pageable, String searchParams, Integer tipoParams) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";
        //monta pesquisa search
        search = getSearch(searchParams, tipoParams);
        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");
        // Object test = new Integer[]{1,2,3,4,5};
        List<Object[]> list = getEntityManager()
                .createNativeQuery("select distinct a.cpfServidor, ser.nome, car.nomeCargo, a.tipoAposentadoria, ato.numeroAto, " +
                        " (CASE WHEN ae.status IS NULL THEN 1 ELSE ae.status END) as status, a.id " +
                                " from Aposentadoria a " +
                                " join Admissao ad on ad.id = a.id " +
                                " join Servidor ser on ser.id = ad.idServidor " +
                                " join Cargo car on car.id = ad.idCargo " +
                                " join Ato ato on ato.id = a.idAto " +
                                " left join AdmEnvio ae on ae.idMovimentacao = a.id " +
                                " join InfoRemessa i on a.chave = i.chave " +
                        " where a.reversao = 0 and a.revisao = 1 and a.tipoAposentadoria = " + Aposentadoria.TipoAposentadoria.Reforma.getValor() +
                        " and i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "' " + search + " ORDER BY " + campo)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();

        Iterator result = list.iterator();
        List<AposentadoriaDTO> aposentadoriaDTOList = new ArrayList<>();
        convertResult(result, aposentadoriaDTOList);

        long totalRegistros = countAposentadoriaRevisaoReforma();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        return new PaginacaoUtil<AposentadoriaDTO>(tamanho, pagina, totalPaginas, totalRegistros,aposentadoriaDTOList);
    }

    public Integer countAposentadoriaRevisaoReforma() {
        Query query = getEntityManager().createNativeQuery("select count(*) from Aposentadoria a " +
                "join InfoRemessa i on a.chave = i.chave " +
                "where revisao = 1 and tipoAposentadoria = " + Aposentadoria.TipoAposentadoria.Reforma.getValor() +
                " and i.idUnidadeGestora= '" + User.getUser(super.request).getUnidadeGestora().getId() + "'");
        return (Integer) query.getSingleResult();
    }

    public List<Aposentadoria> buscarAposentadoriaRevisaoReforma() {
        return getEntityManager().createNativeQuery(
                        "select a.* from Aposentadoria a " +
                                "join InfoRemessa i on a.chave = i.chave " +
                                "where revisao = 1 and tipoAposentadoria = " + Aposentadoria.TipoAposentadoria.Reforma.getValor() +
                                " and i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "'", Aposentadoria.class)
                .getResultList();
    }
    // -------------------------------------------------------------------------------------------------------------- //


    // -------------------------------------------------------------------------------------------------------------- //
    // --------------------------------- Concessão Reversão Aposentadoria / Reserva --------------------------------- //
    // -------------------------------------------------------------------------------------------------------------- //
    public PaginacaoUtil<AposentadoriaDTO> buscaPaginadaReversaoAposentadoriaReserva(Pageable pageable, String searchParams, Integer tipoParams) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";
        //monta pesquisa search
        search = getSearch(searchParams, tipoParams);
        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<AposentadoriaDTO> list = getEntityManager()
                .createNativeQuery("select distinct a.cpfServidor, ser.nome, car.nomeCargo, a.tipoAposentadoria, ato.numeroAto, " +
                        " (CASE WHEN ae.status IS NULL THEN 1 ELSE ae.status END) as status, a.id " +
                        " from Aposentadoria a " +
                        " join Admissao ad on ad.id = a.id " +
                        " join Servidor ser on ser.id = ad.idServidor " +
                        " join Cargo car on car.id = ad.idCargo " +
                        " join Ato ato on ato.id = a.idAto " +
                        " left join AdmEnvio ae on ae.idMovimentacao = a.id " +
                        " join InfoRemessa i on a.chave = i.chave " +
                        "where a.reversao = 1 and a.revisao = 0 and a.tipoAposentadoria != " + Aposentadoria.TipoAposentadoria.Reforma.getValor() +
                        " and i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "' " + search + " ORDER BY " + campo)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();

        Iterator result = list.iterator();
        List<AposentadoriaDTO> aposentadoriaDTOList = new ArrayList<>();
        convertResult(result, aposentadoriaDTOList);

        long totalRegistros = countReversaoAposentadoriaReserva();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        return new PaginacaoUtil<AposentadoriaDTO>(tamanho, pagina, totalPaginas, totalRegistros, aposentadoriaDTOList );
    }

    public Integer countReversaoAposentadoriaReserva() {
        Query query = getEntityManager().createNativeQuery("select count(*) from Aposentadoria a " +
                "join InfoRemessa i on a.chave = i.chave " +
                "where reversao = 1 and tipoAposentadoria != " + Aposentadoria.TipoAposentadoria.Reforma.getValor() +
                " and i.idUnidadeGestora= '" + User.getUser(super.request).getUnidadeGestora().getId() + "'");
        return (Integer) query.getSingleResult();
    }

    public List<Aposentadoria> buscarReversaoAposentadoriaReserva() {
        return getEntityManager().createNativeQuery(
                        "select * from Aposentadoria a " +
                                "join InfoRemessa i on a.chave = i.chave " +
                                "where reversao = 1 and tipoAposentadoria != " + Aposentadoria.TipoAposentadoria.Reforma.getValor() +
                                " and i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "'", Aposentadoria.class)
                .getResultList();
    }
    // -------------------------------------------------------------------------------------------------------------- //


    // -------------------------------------------------------------------------------------------------------------- //
    // ------------------------------ Converte resultado para a classe AposentadoriaDTO ----------------------------- //
    // -------------------------------------------------------------------------------------------------------------- //
    private List<AposentadoriaDTO> convertResult(Iterator result, List<AposentadoriaDTO> aposentadoriaDTOList) {
        while (result.hasNext()) {
            Object[] obj = (Object[]) result.next();
            AposentadoriaDTO dto = new AposentadoriaDTO();
            dto.setCpfServidor(String.valueOf(obj[0]));
            dto.setNome(String.valueOf(obj[1]));
            dto.setCargo(String.valueOf(obj[2]));
            dto.setTipoAposentadoria(Integer.valueOf(String.valueOf(obj[3])));
            dto.setNumeroAto(String.valueOf(obj[4]));
            dto.setStatus(Integer.valueOf(String.valueOf(obj[5])));
            dto.setId(BigInteger.valueOf(Long.parseLong(String.valueOf(obj[6]))));
            aposentadoriaDTOList.add(dto);
        }
        return aposentadoriaDTOList;
    }
}
