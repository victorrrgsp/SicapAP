package com.example.sicapweb.repository.concessao;

import br.gov.to.tce.model.ap.pessoal.Aproveitamento;
import com.example.sicapweb.model.dto.AproveitamentoDTO;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AproveitamentoRepository extends DefaultRepository<Aproveitamento, BigInteger> {
    public AproveitamentoRepository(EntityManager em) {
        super(em);
    }

    public PaginacaoUtil<AproveitamentoDTO> buscaPaginadaAproveitamento(Pageable pageable, String searchParams, Integer tipoParams) {
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
                .createNativeQuery("select distinct ser.cpfServidor, " +
                                "       ser.nome, " +
                                "       car.nomeCargo, " +
                                "       ato.numeroAto, " +
                                "       (CASE WHEN ae.status IS NULL THEN 1 ELSE ae.status END) as status, ae.processo, " +
                                "       a.id " +
                                "from Aproveitamento a " +
                                "    join Admissao ad on ad.id = a.id " +
                                "    join Servidor ser on ser.id = ad.idServidor " +
                                "    join Cargo car on car.id = ad.idCargo " +
                                "    join Ato ato on ato.id = a.idAto " +
                                "    left join AdmEnvio ae on ae.idMovimentacao = a.id " +
                                "    join InfoRemessa i on a.chave = i.chave "+
                        " where i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "' " + search + " ORDER BY " + campo)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();
        // mapeando a lista
        List<AproveitamentoDTO> aproveitamentoDTOArrayList = new ArrayList<>();
        list.forEach(a -> {
            var aux = new AproveitamentoDTO();
            aux.setCpfServidor((String) a[0]);
            aux.setNome((String)a[1]);
            aux.setCargo((String)a[2]);
            aux.setNumeroAto((String)a[3]);
            aux.setStatus((Integer)a[4]);
            aux.setProcesso((String)a[5]);
            aux.setId((BigDecimal) a[6]);
            aproveitamentoDTOArrayList.add(aux);
        });

        long totalRegistros = count();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        return new PaginacaoUtil<AproveitamentoDTO>(tamanho,pagina,totalPaginas,totalRegistros,aproveitamentoDTOArrayList);
    }
}
