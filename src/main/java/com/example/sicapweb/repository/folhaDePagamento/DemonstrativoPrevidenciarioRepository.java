package com.example.sicapweb.repository.folhaDePagamento;

import br.gov.to.tce.model.ap.folha.DemonstrativoPrevidenciario;

import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;


    @Repository
    public class DemonstrativoPrevidenciarioRepository extends DefaultRepository<DemonstrativoPrevidenciario, BigInteger> {

        public DemonstrativoPrevidenciarioRepository(EntityManager em) {
            super(em);
        }
    }


