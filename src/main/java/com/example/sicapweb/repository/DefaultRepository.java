package com.example.sicapweb.repository;

import br.gov.to.tce.application.ApplicationException;
import br.gov.to.tce.model.DefaultEntity;
import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.util.ResultMapList;
import br.gov.to.tce.util.Util;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;

public abstract class DefaultRepository<T, PK extends Serializable> {

    @SuppressWarnings("unchecked")
    private final Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    @PersistenceContext
    @Autowired
    public EntityManager entityManager;

    @Autowired
    protected HttpServletRequest request;

    public DefaultRepository(EntityManager em) {
        entityManager = em;
    }

    public DefaultRepository() {
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public EntityManager getEntityManager() {
//        if(entityManager == null) {
//            entityManager = new Configuration().configure().buildSessionFactory().openSession()
//                    .getEntityManagerFactory().createEntityManager();
//        }
        return entityManager;
    }

    @Transactional(rollbackFor = {SQLException.class})
    public void save(T entity) {
        getEntityManager().persist(entity);
    }

    //@Transactional(rollbackFor = { SQLException.class },  propagation = Propagation.NESTED )
    public void update(T entity) {
        try {
            getEntityManager().merge(entity);
            //getEntityManager().getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean isRPPS(String idUnidade ){
        return entityManager.createNativeQuery("select * from UnidadeGestoraRpps where cnpjRpps like '%"+User.getUser(getRequest()).getUnidadeGestora().getId()+"%'").getResultList().size() > 0;
    }
    public <F extends  DefaultEntity > void updateVinculo( T pai,F filho ) {
        if(filho == null || pai == null||filho.getId() == null || entityClass.getSimpleName() == null) throw new NullPointerException();
        try {
            entityManager = getEntityManager();
            //cria um comando sql para atualizar o cargo em admissao
            Query query = entityManager.createNativeQuery("update "+entityClass.getSimpleName()+" set id" + filho.getClass().getSimpleName() +" = :idfilho where id = :id");
            query.setParameter("idfilho",filho.getId());
            query.setParameter("id",entityClass.getMethod("getId").invoke(pai));
            query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //@Transactional(rollbackFor = { SQLException.class },  propagation = Propagation.NESTED )
    public void deleteRestrito(BigInteger id) {
        try {
            DefaultEntity objeto = (DefaultEntity)getEntityManager().find(entityClass, id);
            objeto.setId(id);
            if(objeto.getChave().getIdUnidadeGestora().equals(User.getUser(request).getUnidadeGestora().getId())){
                getEntityManager().remove(objeto);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void delete(BigInteger id) {
        getEntityManager().remove(getEntityManager().find(entityClass, id));
    }
    //@Transactional(rollbackFor = { SQLException.class },  propagation = Propagation.NESTED )
    public void delete(String id) {
        entityManager = getEntityManager();
        entityManager.getTransaction().begin();
        entityManager.remove(entityManager.find(entityClass, id));
        entityManager.getTransaction().commit();
    }

    //@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public T findById(PK id) {
        return getEntityManager().find(entityClass, id);
    }

    //@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<T> findAll() {

        return getEntityManager()
                .createQuery("select a from " + entityClass.getSimpleName() +
                        " a, InfoRemessa info where a.infoRemessa.chave = info.chave and info.idUnidadeGestora = '"
                        + User.getUser(request).getUnidadeGestora().getId() + "'", entityClass)
                .getResultList();
    }

    public Integer findAllInciso(String entidade, String pk, BigInteger id, String inciso) {
        return (Integer) getEntityManager().createNativeQuery("select count(*) from " + entidade +
                " where " + pk + " = " + id + " and inciso = '" + inciso + "'").getSingleResult();

    }

    public Integer findSituacao(String entidade, String pk, BigInteger id, String incisos) {
        return (Integer) getEntityManager().createNativeQuery("select count(*) \n" +
                " Situacao from " + entidade +
                " where " + pk + " = " + id + " and inciso in (" + incisos + ")").getSingleResult();
    }

    protected List<T> createQuery(String jpql, Object... params) {
        TypedQuery<T> query = getEntityManager().createQuery(jpql, entityClass);
        for (int i = 0; i < params.length; i++) {
            query.setParameter(i + 1, params[i]);
        }
        return query.getResultList();
    }

    //public PaginacaoUtil<T> buscaPaginada(int pagina, int tamanho, String direcao, String campo) {
    //@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public PaginacaoUtil<T> buscaPaginada(Pageable pageable, String searchParams, Integer tipoParams) {

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

        var query = getEntityManager()
                .createNativeQuery("select a.* from " + entityClass.getSimpleName() + " a " +
                        " join InfoRemessa info on info.chave = a.chave and info.idUnidadeGestora = '"
                        + User.getUser(request).getUnidadeGestora().getId() + "' " + search + " ORDER BY " + campo, entityClass)
                .setFirstResult(pagina)
                .setMaxResults(tamanho);
        List<T> list = query.getResultList();

        long totalRegistros = count();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;

        return new PaginacaoUtil<T>(tamanho, pagina, totalPaginas, totalRegistros, list);
    }

    public Integer count() {
        Query query = getEntityManager().createNativeQuery("select count(*) from " + entityClass.getSimpleName()
                + " a join InfoRemessa i on a.chave = i.chave where i.idUnidadeGestora= '"+ User.getUser(request).getUnidadeGestora().getId()+ "'");
        return (Integer) query.getSingleResult();
    }

    public InfoRemessa buscarPrimeiraRemessa() {
        List<InfoRemessa> list = getEntityManager().createNativeQuery("select * from infoRemessa " +
                "where remessa = 1 and exercicio = 2021 and idUnidadeGestora = '" + User.getUser(request).getUnidadeGestora().getId() + "'", InfoRemessa.class).getResultList();
        return list.get(0);
    }

    public InfoRemessa buscarPrimeiraRemessa(String cnpj) {
        List<InfoRemessa> list = getEntityManager().createNativeQuery("select * from infoRemessa " +
                "where remessa = 1 and exercicio = 2021 and idUnidadeGestora = '" + cnpj + "'", InfoRemessa.class).getResultList();
        return list.get(0);
    }

    public Object buscarResultadoUnico(Query query) throws ApplicationException {
        try{
            query.setMaxResults(1);
            return findSingleResult(query);
        } catch (IllegalStateException | DatabaseException | IllegalArgumentException | PersistenceException | ApplicationException e) {
            if(e.getClass() == ApplicationException.class)
                return null;

            throw new ApplicationException(Util.removeEnters(e.getMessage()));
        }
    }

    @SuppressWarnings("unchecked")
    public Object findSingleResult(Query query) throws ApplicationException {
        try {

            return query.getSingleResult();
        } catch (IllegalStateException | DatabaseException | IllegalArgumentException | PersistenceException  e) {
            throw new ApplicationException(Util.removeEnters(e.getMessage()));
        }
    }

    public List<Object> buscarSQL(Query query, String colunas) throws ApplicationException {
        try{
            List<Object> lista = findSQL(query);
            ResultMapList result = null;
            if (!lista.isEmpty())
                result = new ResultMapList(colunas, lista);
            return result;
        } catch (IllegalStateException | DatabaseException | IllegalArgumentException | PersistenceException  e) {
            throw new ApplicationException(Util.removeEnters(e.getMessage()));
        }
    }

    public List<Object> findSQL(Query query) throws ApplicationException {
        try {
            return query.getResultList();
        } catch (IllegalStateException | DatabaseException | IllegalArgumentException | PersistenceException e) {
            throw new ApplicationException(Util.removeEnters(e.getMessage()));
        }
    }

    public List<Object> getProcessoApEcontas(String tipo, String cpf, String cnpj) {
        var papel = tipo.equals("77") ? 14 : 2;

        try {
            Query query = getEntityManager().createNativeQuery("" +
                    "SELECT p.processo_numero, " +
                    "       p.processo_ano " +
                    "FROM SCP..PESSOAS_PROCESSO pp " +
                    "         INNER JOIN SCP..processo p ON " +
                    "    pp.NUM_PROC = p.processo_numero AND pp.ANO_PROC = p.processo_ano AND pp.ID_PAPEL = :papel " +
                    "         INNER JOIN SCP..assunto a on a.assunto_classe_assunto = p.processo_assunto_classe_assunto " +
                    "    and a.assunto_codigo = p.processo_assunto_codigo " +
                    "    and a.id = :tipo " +
                    "         INNER JOIN Cadun..vwPessoaGeral pg ON pg.idpessoa = pp.ID_PESSOA " +
                    "         INNER JOIN Cadun..vwPessoaGeral pj ON (pj.id = p.id_entidade_origem) OR (pj.id = p.id_entidade_vinc) " +
                    "WHERE pg.cpf = :cpf " +
                    "  AND pj.cnpj = :cnpj");

            query.setParameter("tipo", tipo);
            query.setParameter("cpf", cpf);
            query.setParameter("cnpj", cnpj);
            query.setParameter("papel", papel);

            return buscarSQL(query, "p.processo_numero, p.processo_ano");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
