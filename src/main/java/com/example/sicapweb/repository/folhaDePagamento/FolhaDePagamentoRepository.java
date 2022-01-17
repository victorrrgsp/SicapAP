package com.example.sicapweb.repository.folhaDePagamento;

import br.gov.to.tce.model.ap.folha.FolhaPagamento;
import br.gov.to.tce.model.ap.relacional.FolhaItem;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;

   @Repository
    public class FolhaDePagamentoRepository extends DefaultRepository<FolhaPagamento, BigInteger> {

        public FolhaDePagamentoRepository(EntityManager em) {
            super(em);
        }
    }

