package com.example.sicapweb.repository.concessao;

import br.gov.to.tce.model.ap.pessoal.Reintegracao;

import com.example.sicapweb.exception.NonRPPSAccessException;
import com.example.sicapweb.model.dto.ReintegracaoDTO;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
@Repository
public class ReintegracaoRepository extends DefaultRepository<Reintegracao, BigInteger> {
    public ReintegracaoRepository(EntityManager em) {
        super(em);
    }

    public PaginacaoUtil<ReintegracaoDTO> buscaPaginadaReintegracao(Pageable pageable, String searchParams, Integer tipoParams) {
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

        List<Object[]> list = getEntityManager()
                .createNativeQuery("select distinct s.cpfServidor," +
                        "       s.nome, "+
                        "       c.nomeCargo, "+
                        "       ato.numeroAto,  " +
                        " (CASE WHEN ae.status IS NULL THEN 1 ELSE ae.status END) as status, ae.processo,"+
                        "       a.id, " +
                            " a.dataExercicio " +
                        " from Reintegracao a " +
                        " left join AdmEnvio ae on ae.idMovimentacao = a.id" +
                        " join InfoRemessa i on a.chave = i.chave " +
                        " join Admissao ad on ad.id = a.id " +
                        " join Cargo c on c.id = ad.idCargo " +
                        " join Servidor s on s.id = ad.idServidor " +
                        " join Ato ato on ato.id = a.idAto " +
                        " where i.idUnidadeGestora = '" + redisConnect.getUser(super.request).getUnidadeGestora().getId() + "' " + search + " ORDER BY " + campo)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();

        List<ReintegracaoDTO> ReitegracaoDTOList = new ArrayList<>();
        list.forEach(a -> {
            var aux = new ReintegracaoDTO();
            aux.setCpfServidor((String) a[0]);
            aux.setNome((String)a[1]);
            aux.setCargo((String)a[2]);
            aux.setNumeroAto((String)a[3]);
            aux.setStatus((Integer)a[4]);
            aux.setProcesso((String)a[5]);
            aux.setId((BigDecimal) a[6]);
            aux.setDataExertcio(new Date(((Timestamp)a[7]).getTime()) );
            ReitegracaoDTOList.add(aux);
        });

        long totalRegistros = count();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        return new PaginacaoUtil<ReintegracaoDTO>(tamanho, pagina, totalPaginas, totalRegistros, ReitegracaoDTOList);
    }

    public List<Reintegracao> buscarReintegracao() {
        return getEntityManager().createNativeQuery(
                "select a.* from Reintegracao a " +
                        "join InfoRemessa i on a.chave = i.chave " +
                        "where i.idUnidadeGestora = '" + redisConnect.getUser(super.request).getUnidadeGestora().getId() + "'", Reintegracao.class)
                .getResultList();
    }
}
