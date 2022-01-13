package com.example.sicapweb.repository.servidores;

import br.gov.to.tce.model.ap.pessoal.Servidor;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;


  @Repository
    public class ServidorRepository extends DefaultRepository<Servidor, BigInteger> {

        public ServidorRepository(EntityManager em) {
            super(em);
        }
    }

