package com.example.sicapweb.repository.concessao;

import br.gov.to.tce.model.adm.AdmEnvioAssinatura;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;

@Repository
public class AdmEnvioAssinaturaRepository extends DefaultRepository<AdmEnvioAssinatura, BigInteger> {

    public AdmEnvioAssinaturaRepository(EntityManager em) {
        super(em);
    }
}
