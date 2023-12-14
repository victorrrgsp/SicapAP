package com.example.sicapweb.repository.concessao;

import br.gov.to.tce.model.ap.pessoal.Pensao;

import com.example.sicapweb.exception.NonRPPSAccessException;
import com.example.sicapweb.model.dto.AposentadoriaDTO;
import com.example.sicapweb.model.dto.PensaoDTO;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Repository
public class PensaoRepository extends DefaultRepository<Pensao, BigInteger> {

    public PensaoRepository(EntityManager em) {
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


    public PaginacaoUtil<PensaoDTO> buscaPaginadaPensao(Pageable pageable, String searchParams, Integer tipoParams) {
        
        if (!isRPPS()) {
            throw new NonRPPSAccessException();
        }
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";
        search = getSearch(searchParams, tipoParams);
        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        Query query = getEntityManager()
                .createNativeQuery("select distinct a.cpfServidor, ser.nome, car.nomeCargo, ato.numeroAto, " +
                        " (CASE WHEN ae.status IS NULL THEN 1 ELSE ae.status END) as status, ae.processo, a.id from Pensao a " +
                        "join Admissao ad on ad.id = a.id " +
                        "join Servidor ser on ser.id = ad.idServidor " +
                        "join Cargo car on car.id = ad.idCargo " +
                        "join Ato ato on ato.id = a.idAto " +
                        "left join AdmEnvio ae on ae.idMovimentacao = a.id " +
                        "join InfoRemessa i on a.chave = i.chave " +
                        "where a.revisao = 0 " +
                        "and i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "' " + search + " ORDER BY " + campo)
                .setFirstResult(pagina)
                .setMaxResults(tamanho);

        List<Object> list = (List<Object>) query.getResultList();
        Iterator result = list.iterator();
        List<PensaoDTO> pensaoDTOList = new ArrayList<>();
        convertResult(result, pensaoDTOList);
        long totalRegistros = countPensao();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        return new PaginacaoUtil<PensaoDTO>(tamanho, pagina, totalPaginas, totalRegistros, pensaoDTOList);
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

    public PaginacaoUtil<PensaoDTO> buscaPaginadaPensaoRevisao(Pageable pageable, String searchParams, Integer tipoParams) {
        
        if (!isRPPS()) {
            throw new NonRPPSAccessException();
        }
        
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";
        search = getSearch(searchParams, tipoParams);
        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        Query query = getEntityManager()
                .createNativeQuery("select distinct a.cpfServidor, ser.nome, car.nomeCargo, ato.numeroAto, " +
                        " (CASE WHEN ae.status IS NULL THEN 1 ELSE ae.status END) as status, ae.processo, a.id from Pensao a " +
                        "join Admissao ad on ad.id = a.id " +
                        "join Servidor ser on ser.id = ad.idServidor " +
                        "join Cargo car on car.id = ad.idCargo " +
                        "join Ato ato on ato.id = a.idAto " +
                        "left join AdmEnvio ae on ae.idMovimentacao = a.id " +
                        "join InfoRemessa i on a.chave = i.chave " +
                        "where a.revisao = 1 " +
                        "and i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "' " + search + " ORDER BY " + campo)
                .setFirstResult(pagina)
                .setMaxResults(tamanho);
        List<Object> list = (List<Object>) query.getResultList();
        Iterator result = list.iterator();
        List<PensaoDTO> pensaoDTOList = new ArrayList<>();
        convertResult(result, pensaoDTOList);
        long totalRegistros = countPensaoRevisao();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        return new PaginacaoUtil<PensaoDTO>(tamanho, pagina, totalPaginas, totalRegistros, pensaoDTOList);
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

    private List<PensaoDTO> convertResult(Iterator result, List<PensaoDTO> pensaoDTOList) {
        while (result.hasNext()) {
            Object[] obj = (Object[]) result.next();
            PensaoDTO dto = new PensaoDTO();
            dto.setCpfServidor(String.valueOf(obj[0]));
            dto.setNome(String.valueOf(obj[1]));
            dto.setCargo(String.valueOf(obj[2]));
            dto.setNumeroAto(String.valueOf(obj[3]));
            dto.setStatus(Integer.valueOf(String.valueOf(obj[4])));
            dto.setProcesso(String.valueOf(obj[5]));
            dto.setId(BigInteger.valueOf(Long.parseLong(String.valueOf(obj[6]))));
            pensaoDTOList.add(dto);
        }
        return pensaoDTOList;
    }
}
