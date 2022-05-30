package com.example.sicapweb.repository.movimentacaoDePessoal;

import br.gov.to.tce.model.ap.pessoal.Desligamento;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;

    @Repository
    public class DesligamentoRepository extends DefaultRepository<Desligamento, BigInteger> {

        public DesligamentoRepository(EntityManager em) {
            super(em);
        }
    }


