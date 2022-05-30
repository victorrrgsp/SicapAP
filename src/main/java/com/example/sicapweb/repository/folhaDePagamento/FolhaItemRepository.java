package com.example.sicapweb.repository.folhaDePagamento;

import br.gov.to.tce.model.ap.relacional.FolhaItem;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;

    @Repository
    public class FolhaItemRepository extends DefaultRepository<FolhaItem, BigInteger> {

        public FolhaItemRepository(EntityManager em) {
            super(em);
        }
    }



