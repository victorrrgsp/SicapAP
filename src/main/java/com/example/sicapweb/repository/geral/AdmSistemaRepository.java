package com.example.sicapweb.repository.geral;

import br.gov.to.tce.model.adm.AdmSistema;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;

@Repository
public class AdmSistemaRepository extends DefaultRepository<AdmSistema, BigInteger> {

    public AdmSistemaRepository(EntityManager em) {
        super(em);
    }

    public AdmSistema buscarAdmSistema(String cpf) {
        Query query = getEntityManager().createNativeQuery(
                "SELECT * FROM AdmSistema " +
                        "where cpf = :cpf and status = 1;", AdmSistema.class);
        query.setParameter("cpf", cpf);
        return (AdmSistema) query.getSingleResult();
    }
}
