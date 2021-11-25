package com.example.sicapweb.repository.externo;

import br.gov.to.tce.model.InfoRemessa;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

@Repository
public class FilaProcessamentosRepository extends DefaultRepository<String, String> {
    @PersistenceContext
    private EntityManager entityManager;

    public FilaProcessamentosRepository(EntityManager em) {
        super(em);
    }

    public FilaProcessamentosRepository() {

    }

    public Object buscarResponsavelAssinatura(Integer tipoCargo, InfoRemessa infoRemessa) {
        try {
            Query query = entityManager.createNativeQuery(
                    "select  nome, cpf, max(status) status, max(data) data, CodigoCargo " +
                            "from (" +
                            "         select distinct pf.nome, pf.cpf, null status, null data, upc.CodigoCargo" +
                            "         from cadun.dbo.UnidadePessoaCargo upc" +
                            "                  join cadun.dbo.vwpessoa pf on pf.id = upc.CodigoPessoaFisica" +
                            "                  join AutenticacaoAssinatura..UsuarioAplicacao ua on ua.Usuario = pf.cpf" +
                            "         join Cadun.dbo.PessoaJuridica pj on upc.CodigoPessoaJuridica = pj.Codigo" +
                            "         where upc.CodigoCargo in (:tipo)" +
                            "           and (dataInicio <= :date and (datafim is null or datafim >= :date))" +
                            "           and pj.CNPJ = :unidade and ua.Aplicacao = 29" +
                            "         union" +
                            "         select distinct pf.nome, pf.cpf, b.DataAssinatura, b.DataAssinatura, upc.CodigoCargo" +
                            "         from cadun.dbo.UnidadePessoaCargo upc" +
                            "                  join cadun.dbo.vwpessoa pf on pf.id = upc.CodigoPessoaFisica" +
                            "                  join AutenticacaoAssinatura..UsuarioAplicacao ua on ua.Usuario = pf.cpf" +
                            "                  join AutenticacaoAssinatura..Assinatura b on ua.Usuario = b.Usuario" +
                            "                  join AutenticacaoAssinatura..InfoAssinatura c on c.Assinatura = b.OID and ua.Aplicacao = c.Aplicacao" +
                            "         join Cadun.dbo.PessoaJuridica pj on upc.CodigoPessoaJuridica = pj.Codigo" +
                            "         where upc.CodigoCargo in (:tipo)" +
                            "           and (dataInicio <= :date and (datafim is null or datafim >= :date))" +
                            "           and pj.CNPJ = :unidade" +
                            "           and c.Exercicio = :exercicio" +
                            "           and c.Bimestre = :remessa" +
                            "           and ua.Aplicacao = 29" +
                            "     ) as v " +
                            "group by nome, cpf, CodigoCargo;");
            query.setParameter("tipo", tipoCargo);
            query.setParameter("date", new Date());
            query.setParameter("unidade", User.getUser(super.request).getUnidadeGestora().getId());
            query.setParameter("exercicio", infoRemessa.getExercicio());
            query.setParameter("remessa", infoRemessa.getRemessa());
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }


    public List<Map<String,Object>> filaProcessamentos() {
        try {
            List<Object[]> queryResut = entityManager.createNativeQuery("" +
                    "SELECT u.nome, f.exercicio, f.remessa, dataEnvio, ROW_NUMBER ( )  " +
                    "    OVER ( order by  dataEnvio asc) posicao, " +
                    "(case when f.status = 0 then 'Aguardando' else 'Processando' end) status " +
                    "FROM admfilarecebimento f join " +
                    "AdmAutenticacao a on a.id = f.idAdmAutenticacao join " +
                    "UnidadeGestora u on u.id = idunidadegestora " +
                    "where f.status = 0 or f.status >= 90 " +
                    "order by dataEnvio asc;").getResultList();
            List<Map<String,Object>> retorno = new ArrayList<Map<String,Object>>();
            queryResut.forEach(r ->{
                Map<String,Object> aux = new HashMap<String,Object>();

                aux.put("nome",      (String)r[0]);
                aux.put("exercicio", (Integer)r[1]);
                aux.put("remessa",   (Integer)r[2]);
                aux.put("dataEnvio", (String)r[3]);
                aux.put("posicao", r[4]);
                aux.put("status",    (String)r[5]);
                retorno.add(aux);
            });
            return retorno;
        } catch (Exception e) {
            return null;
        }
    }


    public List<Object> filaProcess() {
        try {
            List<Object> queryResut = entityManager.createNativeQuery("" +
                    "SELECT u.nome, f.exercicio, f.remessa, dataEnvio, ROW_NUMBER ( )  " +
                    "    OVER ( order by  dataEnvio asc) posicao, " +
                    "(case when f.status = 0 then 'Aguardando' else 'Processando' end) status " +
                    "FROM admfilarecebimento f join " +
                    "AdmAutenticacao a on a.id = f.idAdmAutenticacao join " +
                    "UnidadeGestora u on u.id = idunidadegestora " +
                    "where f.status = 0 or f.status >= 90 " +
                    "order by dataEnvio asc;").getResultList();
            return queryResut;

        } catch (Exception e) {
            return null;
        }
    }



    public List<Map<String,Object>> processo() {
        try {
            List<Object[]> queryResut = entityManager.createNativeQuery("" +
                    "SELECT u.nome, f.exercicio, dataEnvio,  dataprocessamento, f.remessa, (case when f.status = 2  then 'ok' else 'mapear erro' end) status " +
                    "FROM admfilarecebimento f join AdmAutenticacao a on a.id = f.idAdmAutenticacao " +
                    "join UnidadeGestora u on u.id = idunidadegestora " +
                    " where  f.status between 2 and 10 " +
                    "order by dataEnvio desc;").getResultList();
            List<Map<String,Object>> retorno = new ArrayList<Map<String,Object>>();
            queryResut.forEach(r ->{
                Map<String,Object> aux = new HashMap<String,Object>();
                aux.put("nome",             (String)r[0]);
                aux.put("exercicio",        (Integer)r[1]);
                aux.put("dataEnvio",        (String)r[2]);
                aux.put("dataProcessamento",(String)r[3]);
                aux.put("remessa",          (Integer)r[4]);
                aux.put("status",           (String)r[5]);
                retorno.add(aux);
            });
            return retorno;
        } catch (Exception e) {
            return null;
        }
    }


}