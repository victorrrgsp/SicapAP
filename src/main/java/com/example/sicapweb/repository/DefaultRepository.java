package com.example.sicapweb.repository;

import br.gov.to.tce.model.InfoRemessa;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.util.List;

public abstract class DefaultRepository<T, PK extends Serializable> {

    @SuppressWarnings("unchecked")
    private final Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    @PersistenceContext
    @Autowired
    public EntityManager entityManager;

    public DefaultRepository(EntityManager em){
        entityManager = em;
    }
    public DefaultRepository(){
    }

    public EntityManager getEntityManager() {
//        if(entityManager == null) {
//            entityManager = new Configuration().configure().buildSessionFactory().openSession()
//                    .getEntityManagerFactory().createEntityManager();
//        }
        return entityManager;
    }

    public void save(T entity) {
        getEntityManager().persist(entity);
    }

    public void update(T entity) {

        try {
            getEntityManager().merge(entity);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void delete(BigInteger id) {
        entityManager = getEntityManager();
        entityManager.getTransaction().begin();
        entityManager.remove(entityManager.find(entityClass, id));
        entityManager.getTransaction().commit();
    }

    public void delete(String id) {
        entityManager = getEntityManager();
        entityManager.getTransaction().begin();
        entityManager.remove(entityManager.find(entityClass, id));
        entityManager.getTransaction().commit();
    }

    public T findById(PK id) {
        return getEntityManager().find(entityClass, id);
    }

    public List<T> findAll() {
        return getEntityManager()
                .createQuery("from " + entityClass.getSimpleName(), entityClass)
                .getResultList();
    }

    protected List<T> createQuery(String jpql, Object... params) {
        TypedQuery<T> query = getEntityManager().createQuery(jpql, entityClass);
        for (int i = 0; i < params.length; i++) {
            query.setParameter(i + 1, params[i]);
        }
        return query.getResultList();
    }

    public PaginacaoUtil<T> buscaPaginada(int pagina, String direcao, String campo) {
        int tamanho = 10;
        int inicio = (pagina -1) * tamanho;
        List<T> list = getEntityManager()
                .createNativeQuery("select * from " + entityClass.getSimpleName() + " order by " + campo + " " + direcao, entityClass)
                .setFirstResult(inicio)
                .setMaxResults(tamanho)
                .getResultList();

        long totalRegistros = count();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        return new PaginacaoUtil<T>(tamanho, pagina, totalPaginas, direcao, list);
    }

    public long count() {
        return getEntityManager().createQuery("select count(*) from " + entityClass.getSimpleName(), Long.class).getSingleResult();
    }

    public InfoRemessa buscarPrimeiraRemessa() {
        List<InfoRemessa> list = getEntityManager().createNativeQuery("select * from infoRemessa " +
                "where remessa = 1 and exercicio = 2021", InfoRemessa.class).getResultList();
        return list.get(0);
    }
}
