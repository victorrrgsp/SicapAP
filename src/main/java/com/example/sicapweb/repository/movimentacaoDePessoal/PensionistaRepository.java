package com.example.sicapweb.repository.movimentacaoDePessoal;

import br.gov.to.tce.model.ap.pessoal.Desligamento;
import br.gov.to.tce.model.ap.pessoal.Pensionista;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;

    @Repository
    public class PensionistaRepository extends DefaultRepository<Pensionista, BigInteger> {

        public PensionistaRepository(EntityManager em) {
            super(em);
        }
    }




