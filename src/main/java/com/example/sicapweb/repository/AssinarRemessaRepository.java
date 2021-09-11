package com.example.sicapweb.repository;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class AssinarRemessaRepository extends DefaultRepository<String, String> {
    @PersistenceContext
    private EntityManager entityManager;

    public AssinarRemessaRepository(EntityManager em) {
        super(em);
    }

    public AssinarRemessaRepository() {

    }

    public String buscarResponsavelAssinatura(String cargo) {
        return (String) entityManager.createNativeQuery(
                "select distinct nome_gestor from Cadun..vwunidadespessoascargos where dataFim is null and " +
                        " cnpj = '00001602000163' " +
                        " and nomeCargo = '"+ cargo +"'").getSingleResult();
    }
}
