package com.example.sicapweb.repository;

import br.gov.to.tce.model.InfoRemessa;
import com.example.sicapweb.security.User;
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
                        " cnpj = '" + User.getUser().getUnidadeGestora().getId() + "' " +
                        " and nomeCargo = '"+ cargo +"'").getSingleResult();
    }

    public InfoRemessa buscarRemessaAberta() {
        return (InfoRemessa) entityManager.createNativeQuery(
                "select * from InfoRemessa i" +
                        " where i.chave not in (select a.chave from AdmAssinatura a where a.chave = i.chave)" +
                        " and i.idUnidadeGestora = '"+ User.getUser().getUnidadeGestora().getId() +"'", InfoRemessa.class).getSingleResult();
    }
}
