package com.example.sicapweb.repository.concessao;

import br.gov.to.tce.model.ap.pessoal.Readaptacao;
import com.example.sicapweb.model.dto.ReadaptacaoDTO;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReadaptacaoRepository extends DefaultRepository<Readaptacao, BigInteger> {
    public ReadaptacaoRepository(EntityManager em) {
        super(em);
    }

    public PaginacaoUtil<ReadaptacaoDTO> buscaPaginadaReadaptacao(Pageable pageable, String searchParams, Integer tipoParams) {
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
                .createNativeQuery(
                        "select distinct s.cpfServidor, " +
                                "       s.nome, " +
                                "       c.nomeCargo, " +
                                "       ato.numeroAto, " +
                                "       (CASE WHEN ae.status IS NULL THEN 1 ELSE ae.status END) as status, " +
                                "       a.id," +
                                "       a.dataInicio " +
                                "    from Readaptacao a " +
                                "        join InfoRemessa i on a.chave = i.chave " +
                                "        join Admissao ad on ad.id = a.id " +
                                "        join Cargo c on c.id = ad.idCargo " +
                                "        join Servidor s on s.id = ad.idServidor " +
                                "        join Ato ato on ato.id = a.idAto " +
                                "        left join AdmEnvio ae on ae.idMovimentacao = a.id " +
                                "where i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "' " + search + " ORDER BY " + campo)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();
        List<ReadaptacaoDTO> ReadaptacaoDTOList = new ArrayList<>();
        list.forEach(a -> {
            var aux = new ReadaptacaoDTO();
            aux.setCpfServidor((String) a[0]);
            aux.setNome((String)a[1]);
            aux.setCargo((String)a[2]);
            aux.setNumeroAto((String)a[3]);
            aux.setStatus((Integer)a[4]);
            aux.setId((BigDecimal) a[5]);
            aux.setDataInicial(new Date(((Timestamp)a[6]).getTime()));
            ReadaptacaoDTOList.add(aux);
        });
        long totalRegistros = count();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        return new PaginacaoUtil<ReadaptacaoDTO>(tamanho, pagina, totalPaginas, totalRegistros, ReadaptacaoDTOList );
    }

    public List<Readaptacao> buscarReadaptacao() {
        return getEntityManager().createNativeQuery(
                "select a.* from Readaptacao a " +
                        "join InfoRemessa i on a.chave = i.chave " +
                        "where i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "'", Readaptacao.class)
                .getResultList();
    }
}
