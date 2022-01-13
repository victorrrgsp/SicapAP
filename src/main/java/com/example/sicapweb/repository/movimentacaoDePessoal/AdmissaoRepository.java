package com.example.sicapweb.repository.movimentacaoDePessoal;

import br.gov.to.tce.model.ap.pessoal.Admissao;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;

    @Repository
    public class AdmissaoRepository extends DefaultRepository<Admissao, BigInteger> {

        public AdmissaoRepository(EntityManager em) {
            super(em);
        }
    }
