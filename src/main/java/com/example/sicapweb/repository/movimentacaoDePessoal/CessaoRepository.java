package com.example.sicapweb.repository.movimentacaoDePessoal;

import br.gov.to.tce.model.ap.pessoal.Cessao;
import br.gov.to.tce.model.ap.pessoal.DesignacaoFuncao;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;

    @Repository
    public class CessaoRepository extends DefaultRepository<Cessao, BigInteger> {

        public CessaoRepository(EntityManager em) {
            super(em);
        }
    }


