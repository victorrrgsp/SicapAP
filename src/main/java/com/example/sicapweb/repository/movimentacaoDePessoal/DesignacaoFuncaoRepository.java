package com.example.sicapweb.repository.movimentacaoDePessoal;

import br.gov.to.tce.model.ap.pessoal.DesignacaoFuncao;
import br.gov.to.tce.model.ap.pessoal.Desligamento;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;


    @Repository
    public class DesignacaoFuncaoRepository extends DefaultRepository<DesignacaoFuncao, BigInteger> {

        public DesignacaoFuncaoRepository(EntityManager em) {
            super(em);
        }
    }

