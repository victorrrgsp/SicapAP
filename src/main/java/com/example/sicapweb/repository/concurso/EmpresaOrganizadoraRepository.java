package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.ap.concurso.EmpresaOrganizadora;
import com.example.sicapweb.repository.DefaultRepository;
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
        List<EmpresaOrganizadora> listaDeEmpresasPorCnpj = getEntityManager().createNativeQuery("select * from EmpresaOrganizadora ed" +
                " where cnpjEmpresaOrganizadora = '" + Cnpj + "'    ", EmpresaOrganizadora.class).getResultList();
        return (listaDeEmpresasPorCnpj.size()>0) ? listaDeEmpresasPorCnpj.get(0) : null ;
    }

}
