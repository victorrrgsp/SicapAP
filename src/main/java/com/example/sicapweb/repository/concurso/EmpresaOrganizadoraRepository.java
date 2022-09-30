package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.ap.concurso.EmpresaOrganizadora;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.math.BigInteger;
import java.util.List;

@Repository
public class EmpresaOrganizadoraRepository extends DefaultRepository<EmpresaOrganizadora, BigInteger> {


    public EmpresaOrganizadoraRepository(EntityManager em) {
        super(em);
    }

    public EmpresaOrganizadora buscaEmpresaPorCnpj(String Cnpj) {
        try{
            return (EmpresaOrganizadora) getEntityManager().createNativeQuery("select a.* from EmpresaOrganizadora a " +
                    " where a.cnpjEmpresaOrganizadora = :cnpj     ", EmpresaOrganizadora.class).setMaxResults(1).setParameter("cnpj",Cnpj).setMaxResults(1).getSingleResult();
        }catch (
                NoResultException e){
            return null;
        }
    }

}
