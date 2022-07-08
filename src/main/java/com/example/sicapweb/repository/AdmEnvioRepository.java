package com.example.sicapweb.repository;

import br.gov.to.tce.model.adm.AdmEnvio;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Repository
public class AdmEnvioRepository extends DefaultRepository<AdmEnvio, BigInteger> {

    public AdmEnvioRepository(EntityManager em) {
        super(em);
    }

    public PaginacaoUtil<AdmEnvio> buscaPaginada(Pageable pageable, String searchParams, Integer tipoParams) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";

        //monta pesquisa search
        if (searchParams.length() > 3) {

            if (tipoParams == 0) { //entra para tratar a string
                String arrayOfStrings[] = searchParams.split("=");
                search = " AND " + arrayOfStrings[0] + " LIKE  '%" + arrayOfStrings[1] + "%'  ";
            } else {
                search = " AND " + searchParams + "   ";
            }
        }

        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<AdmEnvio> list = getEntityManager().createNativeQuery("" +
                        "select * from SICAPAP21..AdmEnvio " +
                        "where status = 3 and unidadeGestora = '"
                        + User.getUser(request).getUnidadeGestora().getId() + "' " + search + " ORDER BY "
                        + campo, AdmEnvio.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();

        long totalRegistros = countEnvio();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;

        return new PaginacaoUtil<AdmEnvio>(tamanho, pagina, totalPaginas, totalRegistros, list);
    }

    public List<HashMap<String, Object>> infoByRecibo(long idAdmissao) {
        List<Object[]> list = getEntityManager().createNativeQuery("select top 1  env.processo,env.unidadeGestora ,ug.nome as nomeEntidade ,CAST(se.nome AS varchar(500)) as nomeServidor ,env.tipoRegistro\n" +
                        "    from SICAPAP21w.dbo.AdmEnvio env\n" +
                        "    join SICAPAP21w.dbo.unidadegestora ug on env.unidadeGestora = ug.id\n" +
                        "    join SICAPAP21w.dbo.Admissao ad on env.idAdmissao = ad.id\n" +
                        "    join SICAPAP21w.dbo.Servidor se on ad.idServidor = se.id\n" +
                        "      where status = 3 and\n" +
                        "        env.idAdmissao = ?")
                .setParameter(1, idAdmissao)
                .getResultList();

        List<HashMap<String, Object>> resutSet = new ArrayList<>();
        list.forEach(admEnvio -> {
            var aux = new HashMap<String, Object>();
            aux.put("processo", (String) admEnvio[0]);
            aux.put("UnidadeGestora", (String) admEnvio[1]);
            aux.put("nomeEntidade", (String) admEnvio[2]);
            aux.put("nomeServidor", (String) admEnvio[3]);
            var tipoLabel = Arrays.stream(AdmEnvio.TipoRegistro.values()).filter(a -> a.getValor() == (int) admEnvio[4]).findFirst().get().getLabel();
            aux.put("tipoRegistro", tipoLabel.toUpperCase());
            resutSet.add(aux);
        });
        return resutSet;
    }

    public Integer countEnvio() {
        Query query = getEntityManager().createNativeQuery("" +
                "select count(*) from SICAPAP21..AdmEnvio " +
                "where status = 3 and unidadeGestora = '"
                + User.getUser(request).getUnidadeGestora().getId() + "' ");
        return (Integer) query.getSingleResult();
    }
}
