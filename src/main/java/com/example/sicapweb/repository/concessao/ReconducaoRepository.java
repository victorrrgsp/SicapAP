package com.example.sicapweb.repository.concessao;

import br.gov.to.tce.model.ap.pessoal.Reconducao;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;

@Repository
public class ReconducaoRepository extends DefaultRepository<Reconducao, BigInteger> {
    public ReconducaoRepository(EntityManager em) {
        super(em);
    }

    public PaginacaoUtil<Reconducao> buscaPaginadaReconducao(Pageable pageable, String searchParams, Integer tipoParams) {
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

        List<Reconducao> list = getEntityManager()
                .createNativeQuery("select a.* from Reconducao a " +
                        "join InfoRemessa i on a.chave = i.chave " +
                        "join Admissao ad on ad.id = a.id " +
                        "join Cargo c on c.id = ad.idCargo " +
                        "join Servidor s on s.id = ad.idServidor " +
                        "join Ato ato on ato.id = a.idAto " +
                        "where i.idUnidadeGestora = '" + User.getUser().getUnidadeGestora().getId() + "' " + search + " ORDER BY " + campo, Reconducao.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();
        long totalRegistros = count();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        return new PaginacaoUtil<Reconducao>(tamanho, pagina, totalPaginas, totalRegistros, list);
    }

    public List<Reconducao> buscarReconducao() {
        return getEntityManager().createNativeQuery(
                "select a.* from Reconducao a " +
                        "join InfoRemessa i on a.chave = i.chave " +
                        "where i.idUnidadeGestora = '" + User.getUser().getUnidadeGestora().getId() + "'", Reconducao.class)
                .getResultList();
    }
}
