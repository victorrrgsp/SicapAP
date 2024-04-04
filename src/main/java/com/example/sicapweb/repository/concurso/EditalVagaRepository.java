package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.ap.concurso.Edital;
import br.gov.to.tce.model.ap.concurso.EditalVaga;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;

@Repository
public class EditalVagaRepository extends DefaultRepository<EditalVaga, BigInteger> {

    public EditalVagaRepository(EntityManager em) {
        super(em);
    }

    public PaginacaoUtil<EditalVaga> buscaPaginada(Pageable pageable, String searchParams, Integer tipoParams) {

        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";

        //monta pesquisa search
        //search = "";

        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        var query = getEntityManager()
                .createNativeQuery("\n" +
                                "select a.*\n" +
                                "from EditalVaga a\n" +
                                "         join infoRemessa i on a.chave = i.chave and i.idUnidadeGestora = '" +redisConnect.getUser(super.request).getUnidadeGestora().getId()+"'"+
                //"where 1=1 " + search + 
                " ORDER BY " + campo, EditalVaga.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho);
        List<EditalVaga> list =  query.getResultList();

        long totalRegistros = countEditaisvaga( search);
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;

        return new PaginacaoUtil<EditalVaga>(tamanho, pagina, totalPaginas, totalRegistros, list);
    }

    public Integer countEditaisvaga(String search) {
        Query query = getEntityManager().createNativeQuery("select count(1)\n" +
                "from EditalVaga a\n" +
                "         join infoRemessa i on a.chave = i.chave and i.idUnidadeGestora = '"+redisConnect.getUser(super.request).getUnidadeGestora().getId()+"' \n" +
                "where 1 = 1" + search);
        return (Integer) query.getSingleResult();
    }

    public List<EditalVaga> buscarVagas() {
        return  getEntityManager().createNativeQuery(
                        "select a.* from EditalVaga a"+
                        " join InfoRemessa i on a.chave = i.chave " +
                                "where  i.idUnidadeGestora = '" + redisConnect.getUser(super.request).getUnidadeGestora().getId() + "' ", EditalVaga.class)
                .getResultList();
    }

    public List<EditalVaga> buscarVagasPorEdital(Integer idEdital) {
        return getEntityManager().createNativeQuery(
                        "with edt as ( " +
                                "        select a.codigoVaga, i.idUnidadeGestora ,max(a.id)  max_id " +
                                "             from EditalVaga a  join infoRemessa i on a.chave = i.chave  and i.idUnidadeGestora = '"+redisConnect.getUser(super.request).getUnidadeGestora().getId()+"'  group by " +
                                "                a.codigoVaga, i.idUnidadeGestora " +
                                "                         ) " +
                                "select   a.* from EditalVaga a join infoRemessa i on a.chave = i.chave join edt b on a.id= b.max_id and i.idUnidadeGestora=b.idUnidadeGestora where 1=1 "+
                        "  and  a.idEdital = " + idEdital, EditalVaga.class)
                .getResultList();
    }

    public EditalVaga buscarVagasPorCodigo(String codigo) {
        try{
            var query =  getEntityManager().createNativeQuery(
                    "with edt as ( " +
                            "        select a.codigoVaga, i.idUnidadeGestora ,max(a.id)  max_id " +
                            "             from EditalVaga a  join infoRemessa i on a.chave = i.chave  and i.idUnidadeGestora = '"+redisConnect.getUser(super.request).getUnidadeGestora().getId()+"'  group by " +
                            "                a.codigoVaga, i.idUnidadeGestora " +
                            "                         ) " +
                            "select   a.* from EditalVaga a join infoRemessa i on a.chave = i.chave join edt b on a.id= b.max_id and i.idUnidadeGestora=b.idUnidadeGestora where 1=1 "+
                            " and a.codigoVaga = '" + codigo + "' ", EditalVaga.class).setMaxResults(1);
            return  (EditalVaga) query.getSingleResult();
        }catch (NoResultException e){
            return null;
        }
    }

    public EditalVaga buscarVagasPorCodigoEEdital(String codigo,String numeroedital) {
        try{
            var query =  getEntityManager().createNativeQuery(
                    "with edt as (select a.codigoVaga, i.idUnidadeGestora, max(a.id) max_id\r\n" + //
                            "             from EditalVaga a\r\n" + //
                            "                      join Edital ed on a.idEdital = ed.id\r\n" + //
                            "                      join infoRemessa i on a.chave = i.chave and i.idUnidadeGestora = :idUG\r\n" + //
                            "             group by a.codigoVaga, i.idUnidadeGestora,ed.numeroEdital)\r\n" + //
                            "select a.*\r\n" + //
                            "from EditalVaga a\r\n" + //
                            "         join infoRemessa i on a.chave = i.chave\r\n" + //
                            "         join edt b on a.id = b.max_id and i.idUnidadeGestora = b.idUnidadeGestora\r\n" + //
                            "         join Edital ed on a.idEdital = ed.id\r\n" + //
                            "where a.codigoVaga = :codigoVaga and ed.numeroEdital = :numeroedital order by a.id", EditalVaga.class)
                            .setParameter("codigoVaga", codigo)
                            .setParameter("numeroedital",numeroedital)
                            .setParameter("idUG",redisConnect.getUser(super.request).getUnidadeGestora().getId())
                            .setMaxResults(1);
            return  (EditalVaga) query.getSingleResult();
        }catch (NoResultException e){
            return null;
        }
    }

    public EditalVaga buscarVagasPorCodigoTipo(String codigo,Integer tipo) {
        try{
            var query =  getEntityManager().createNativeQuery(
                    "select a.*\n" +
                            "from EditalVaga a\n" +
                            "         join infoRemessa i on a.chave = i.chave\n" +
                            "where 1 = 1\n" +
                            " and a.codigoVaga = '" + codigo + "' and a.tipoConcorrencia = "+ tipo + " and i.idUnidadeGestora = '" +redisConnect.getUser(super.request).getUnidadeGestora().getId()+"'", EditalVaga.class).setMaxResults(1);
            return  (EditalVaga) query.getSingleResult();
        }catch (NoResultException e){
            return null;
        }
    }

}
