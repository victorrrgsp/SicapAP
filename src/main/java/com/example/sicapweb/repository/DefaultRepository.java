package com.example.sicapweb.repository;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.concessoes.DocumentoAposentadoria;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.util.List;

import java.lang.*;

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


    public Integer findAllInciso(String entidade, String pk ,BigInteger id, String inciso ) {
        return (Integer) getEntityManager().createNativeQuery("select count(*) from "+ entidade +
                        " where "+ pk +" = "+ id +" and inciso = '"+ inciso +"'").getSingleResult();

    }

    public String findSituacao(String entidade, Integer quantidadeArtigos, String pk ,BigInteger id) {
        return (String) getEntityManager().createNativeQuery("select case \n" +
                "when count(*) = "+quantidadeArtigos+" then 'Concluido'\n" +
                "when count(*) between 1 and "+quantidadeArtigos+" then 'Incompleto'\n" +
                "else  'Pendente' end Situacao from "+ entidade +
                " where "+ pk +" = "+ id ).getSingleResult();

    }

    protected List<T> createQuery(String jpql, Object... params) {
        TypedQuery<T> query = getEntityManager().createQuery(jpql, entityClass);
        for (int i = 0; i < params.length; i++) {
            query.setParameter(i + 1, params[i]);
        }
        return query.getResultList();
    }

    //public PaginacaoUtil<T> buscaPaginada(int pagina, int tamanho, String direcao, String campo) {
    public PaginacaoUtil<T> buscaPaginada(Pageable pageable, String searchParams, Integer tipoParams) {

        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search= "";

        System.out.println("pagina:"+pagina+"tamanho"+tamanho);

       //monta pesquisa search
        if(searchParams.length() > 3){

                if(tipoParams==0){ //entra para tratar a string
                    String arrayOfStrings[]  = searchParams.split("=");
                    search = " WHERE " +arrayOfStrings[0] + " LIKE  '%"+arrayOfStrings[1]+"%'  ";
                }
                else{
                    search = " WHERE " + searchParams + "   ";
                }
            }

            //retirar os : do Sort pageable
            String campo = String.valueOf(pageable.getSort()).replace(":", "");

            List<T> list = getEntityManager()
                    .createNativeQuery("select * from " + entityClass.getSimpleName() + " "+search+" ORDER BY " + campo, entityClass)
                    .setFirstResult(pagina)
                    .setMaxResults(tamanho)
                    .getResultList();

            long totalRegistros = count();
            long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;

        return new PaginacaoUtil<T>(tamanho, pagina, totalPaginas, totalRegistros, list);
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
