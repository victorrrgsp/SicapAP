package com.example.sicapweb.repository.movimentacaoDePessoal;

import br.gov.to.tce.model.ap.pessoal.Disponibilidade;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;


    @Repository
    public class DisponibilidadeRepository extends DefaultRepository<Disponibilidade, BigInteger> {

        public DisponibilidadeRepository(EntityManager em) {
            super(em);
        }
    }


