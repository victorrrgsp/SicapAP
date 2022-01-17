package com.example.sicapweb.repository.movimentacaoDePessoal;

import br.gov.to.tce.model.ap.pessoal.Licenca;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;


    @Repository
    public class LicencaRepository extends DefaultRepository<Licenca, BigInteger> {

        public LicencaRepository(EntityManager em) {
            super(em);
        }
    }


