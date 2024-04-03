package com.example.sicapweb.repository.concessao;

import br.gov.to.tce.model.ap.pessoal.Reconducao;

import com.example.sicapweb.exception.NonRPPSAccessException;
import com.example.sicapweb.model.dto.AposentadoriaDTO;
import com.example.sicapweb.model.dto.ReconducaoDTO;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Repository
public class ReconducaoRepository extends DefaultRepository<Reconducao, BigInteger> {
    public ReconducaoRepository(EntityManager em) {
        super(em);
    }

    public PaginacaoUtil<ReconducaoDTO> buscaPaginadaReconducao(Pageable pageable, String searchParams, Integer tipoParams) {
        
        if (!isRPPS()) {
            throw new NonRPPSAccessException();
        }

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
                    search = " and s." + arrayOfStrings[0] + " LIKE '%" + arrayOfStrings[1] + "%'  ";
                else
                    search = " and " + arrayOfStrings[0] + " LIKE '%" + arrayOfStrings[1] + "%'  ";
            } else {
                search = " and " + searchParams + "   ";
            }
        }
        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        Query query = getEntityManager()
                .createNativeQuery("select distinct s.cpfServidor, s.nome, c.nomeCargo, ato.numeroAto, " +
                        " (CASE WHEN ae.status IS NULL THEN 1 ELSE ae.status END) as status, ae.processo, a.id from Reconducao a " +
                        "join InfoRemessa i on a.chave = i.chave " +
                        "join Admissao ad on ad.id = a.id " +
                        "join Cargo c on c.id = ad.idCargo " +
                        "join Servidor s on s.id = ad.idServidor " +
                        "join Ato ato on ato.id = a.idAto " +
                        "left join AdmEnvio ae on ae.idMovimentacao = a.id " +
                        "where i.idUnidadeGestora = '" + user.getUser(super.request).getUnidadeGestora().getId() + "' "
                        + search + " ORDER BY " + campo)
                .setFirstResult(pagina)
                .setMaxResults(tamanho);

        List<Object> list = (List<Object>) query.getResultList();
        Iterator result = list.iterator();
        List<ReconducaoDTO> reconducaoDTOList = new ArrayList<>();
        while (result.hasNext()) {
            Object[] obj = (Object[]) result.next();
            ReconducaoDTO dto = new ReconducaoDTO();
            dto.setCpfServidor(String.valueOf(obj[0]));
            dto.setNome(String.valueOf(obj[1]));
            dto.setCargo(String.valueOf(obj[2]));
            dto.setNumeroAto(String.valueOf(obj[3]));
            dto.setStatus(Integer.valueOf(String.valueOf(obj[4])));
            dto.setProcesso(String.valueOf(obj[5]));
            dto.setId(BigInteger.valueOf(Long.parseLong(String.valueOf(obj[6]))));
            reconducaoDTOList.add(dto);
        }

        long totalRegistros = count();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        return new PaginacaoUtil<ReconducaoDTO>(tamanho, pagina, totalPaginas, totalRegistros, reconducaoDTOList);
    }

    public List<Reconducao> buscarReconducao() {
        return getEntityManager().createNativeQuery(
                        "select a.* from Reconducao a " +
                                "join InfoRemessa i on a.chave = i.chave " +
                                "where i.idUnidadeGestora = '" + user.getUser(super.request).getUnidadeGestora().getId() + "'", Reconducao.class)
                .getResultList();
    }
}
