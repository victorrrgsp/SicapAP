package com.example.sicapweb.repository.servidores;

import br.gov.to.tce.model.UnidadeGestora;
import br.gov.to.tce.model.ap.pessoal.Servidor;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;


@Repository
    public class ServidorRepository extends DefaultRepository<Servidor, BigInteger> {

        public ServidorRepository(EntityManager em) {
            super(em);
        }

    public Servidor buscaServidorPorCpf(String cpf) {
        List<Servidor> list = entityManager.createNativeQuery("select top 1 * from Servidor " +
                " where cpfServidor = '" + cpf + "'    ", Servidor.class).getResultList();
        return list.get(0);
    }

}

