package com.example.sicapweb.repository.geral;

import br.gov.to.tce.model.ap.relacional.Lei;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.sql.Date;
import java.util.List;

@Repository
public class LeiRepository extends DefaultRepository<Lei, BigInteger> {
    public LeiRepository(EntityManager em) {
        super(em);
    }

    public List<Lei> buscarDocumentoLei(BigInteger id) {
        return getEntityManager().createNativeQuery(
                        "select * from lei l where l.id = " + id, Lei.class)
                .getResultList();
    }

    public List<Lei> findAllLei(String ug) {
        return findAllLei(ug,null,null );
    }
    public List<Lei> findAllLei(String ug,Integer exercicio) {
        return findAllLei(ug,exercicio,null );
    }
    public List<Lei> findAllLei(String ug,Integer exercicio,Integer mes ) {
        var query = getEntityManager()
                .createNativeQuery("select  " +
                        "       l.id, " +
                        "       l.dataPublicacao," +
                        "       l.ementa," +
                        "       l.numeroLei," +
                        "       l.veiculoPublicacao," +
                        "       l.idAto," +
                        "       l.chave," +
                        "       l.idCastorFile " +
                        "from dbo.Lei l " +
                        "join dbo.InfoRemessa info on info.chave = l.chave  " +
                        "where  info.idUnidadeGestora = :UG " +
                        "  and (:exercicio is null or info.exercicio = :exercicio) " +
                        "  and (:mes is null or info.remessa = :mes)" , Lei.class);
                query.setParameter("UG", ug);
                query.setParameter("exercicio", exercicio);
                query.setParameter("mes", mes);

                return query.getResultList();
    }

    public Boolean ExistLeiIqual(String numeroLei, String numeroAto, Integer tipoAto, String veiculoPublicacao, Date dataPublicacao) {
        Query query = getEntityManager()
               .createNativeQuery("select DISTINCT a.* from    Lei   a " +
                       " join InfoRemessa info on info.chave = a.chave and info.idUnidadeGestora = '"
                       + user.getUser(request).getUnidadeGestora().getId()+"'      " +
                       " left join dbo.Ato b on a.idAto=b.id  " +
                       "  where   a.numeroLei = '"+numeroLei+"'  and b.numeroAto =  '"+ numeroAto+"' and  b.tipoAto = "+tipoAto+" ",Lei.class );
       List<Lei> llei = query.getResultList();
       if (llei.size() > 0) {
           return true;
       }
        return false;
    }

    public PaginacaoUtil<Lei> buscaPaginada(Pageable pageable, String searchParams, Integer tipoParams) {

        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";

        //monta pesquisa search
        if (searchParams.length() > 3) {

            if (tipoParams == 0) { //entra para tratar a string
                String arrayOfStrings[] = searchParams.split("=");
                search = " WHERE " + arrayOfStrings[0] + " LIKE  '%" + arrayOfStrings[1] + "%'  ";
            } else {
                search = " WHERE " + searchParams + "   ";
            }
        }

        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<Lei> list = getEntityManager()
                .createNativeQuery("select DISTINCT a.* " +
                        "from (select e.id," +
                        "             e.idcastorfile," +
                        "             e.dataPublicacao," +
                        "             e.ementa, " +
                        "             e.numerolei, " +
                        "             e.veiculoPublicacao, " +
                        "             e.idAto, " +
                        "             e.chave " +
                        "      from Lei e" +
                        "               inner join ato a on e.idAto = a.id " +
                        "      where " +
                        "          e.id = (select max(e1.id) " +
                        "                    from lei e1 join InfoRemessa af1 on e1.chave= af1.chave " +
                        "                             join ato a1     on e1.idAto = a1.id join InfoRemessa af2 on a1.chave= af1.chave  and af1.idUnidadeGestora= af2.idUnidadeGestora and af1.idUnidadeGestora = '" +user.getUser(request).getUnidadeGestora().getId()+ "' "+
                        "                    where e1.dataPublicacao = e.dataPublicacao " +
                        "                      and e1.ementa = e.ementa " +
                        "                      and e1.numeroLei = e.numeroLei " +
                        "                      and e1.veiculoPublicacao = e.veiculoPublicacao " +
                        "                      and a1.numeroAto = a.numeroAto and a1.tipoAto=a.tipoAto  ) " +
                        "      ) a " +
                        "         join InfoRemessa info on info.chave = a.chave and info.idUnidadeGestora = '"
                        + user.getUser(request).getUnidadeGestora().getId() + "' "  + search + " ORDER BY " + campo, Lei.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();

        long totalRegistros = countLeis();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;

        return new PaginacaoUtil<Lei>(tamanho, pagina, totalPaginas, totalRegistros, list);
    }

    public Integer countLeis() {
        Query query = getEntityManager()
                .createNativeQuery("select DISTINCT count(1) " +
                        "from (select e.id," +
                        "             e.idcastorfile," +
                        "             e.dataPublicacao," +
                        "             e.ementa, " +
                        "             e.numerolei, " +
                        "             e.veiculoPublicacao, " +
                        "             e.idAto, " +
                        "             e.chave " +
                        "      from Lei e" +
                        "               inner join ato a on e.idAto = a.id " +
                        "      where " +
                        "          e.id = (select max(e1.id) " +
                        "                    from lei e1 join InfoRemessa af1 on e1.chave= af1.chave " +
                        "                             join ato a1     on e1.idAto = a1.id join InfoRemessa af2 on a1.chave= af1.chave  and af1.idUnidadeGestora= af2.idUnidadeGestora and af1.idUnidadeGestora = '" +user.getUser(request).getUnidadeGestora().getId()+ "' "+
                        "                    where e1.dataPublicacao = e.dataPublicacao " +
                        "                      and e1.ementa = e.ementa " +
                        "                      and e1.numeroLei = e.numeroLei " +
                        "                      and e1.veiculoPublicacao = e.veiculoPublicacao " +
                        "                      and a1.numeroAto = a.numeroAto and a1.tipoAto=a.tipoAto  ) " +
                        "      ) a " +
                        "         join InfoRemessa info on info.chave = a.chave and info.idUnidadeGestora = '"
                        + user.getUser(request).getUnidadeGestora().getId() + "' " );
        return (Integer) query.getSingleResult();
    }
}
