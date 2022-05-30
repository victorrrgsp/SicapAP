package com.example.sicapweb.repository.folhaDePagamento;

import br.gov.to.tce.model.ap.folha.FolhaPagamento;
import br.gov.to.tce.model.ap.folha.RecolhimentoPrevidenciario;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;


    @Repository
    public class RecolhimentoPrevidenciarioRepository extends DefaultRepository<RecolhimentoPrevidenciario, BigInteger> {

        public RecolhimentoPrevidenciarioRepository(EntityManager em) {
            super(em);
        }
    }


