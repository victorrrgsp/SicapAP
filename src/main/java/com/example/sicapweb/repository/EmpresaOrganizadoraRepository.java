package com.example.sicapweb.repository;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.concurso.EmpresaOrganizadora;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;

@Repository
public class EmpresaOrganizadoraRepository extends DefaultRepository<EmpresaOrganizadora, BigInteger> {

    @PersistenceContext
    EntityManager entityManager;


    public EmpresaOrganizadora buscaEmpresaPorCnpj( String Cnpj) {
        List<EmpresaOrganizadora> list = entityManager.createNativeQuery("select * from EmpresaOrganizadora ed" +
                " where cnpjEmpresaOrganizadora = '" + Cnpj + "'    ", EmpresaOrganizadora.class).getResultList();
        return list.get(0);
    }

}
