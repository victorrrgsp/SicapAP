package com.example.sicapweb.repository.orgaosDeLotacoes;

import br.gov.to.tce.model.ap.relacional.Lotacao;

import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;

    @Repository
    public class LotacaoRepository extends DefaultRepository<Lotacao, BigInteger> {

        public LotacaoRepository(EntityManager em) {
            super(em);
        }
    }


