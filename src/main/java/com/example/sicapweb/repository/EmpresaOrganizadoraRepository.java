package com.example.sicapweb.repository;

import br.gov.to.tce.model.ap.concurso.EmpresaOrganizadora;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;

@Repository
public class EmpresaOrganizadoraRepository extends DefaultRepository<EmpresaOrganizadora, BigInteger> {


    public EmpresaOrganizadoraRepository(EntityManager em) {
        super(em);
    }

    public EmpresaOrganizadora buscaEmpresaPorCnpj(String Cnpj) {
        List<EmpresaOrganizadora> list = entityManager.createNativeQuery("select * from EmpresaOrganizadora ed" +
                " where cnpjEmpresaOrganizadora = '" + Cnpj + "'    ", EmpresaOrganizadora.class).getResultList();
        return list.get(0);
    }

}
