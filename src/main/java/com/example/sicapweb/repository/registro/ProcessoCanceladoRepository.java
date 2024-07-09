package com.example.sicapweb.repository.registro;

import br.gov.to.tce.model.ProcessoCancelado;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;

@Repository
public class ProcessoCanceladoRepository extends DefaultRepository<ProcessoCancelado, BigInteger> {

    public ProcessoCanceladoRepository(EntityManager em) {
        super(em);
    }

    public ProcessoCancelado buscaProcessoCancelado(String processo){
        List<ProcessoCancelado> list = entityManager.createNativeQuery("select * from ProcessoCancelado " +
                " where processo = '" + processo + "' ", ProcessoCancelado.class).getResultList();
        return list.get(0);
    }

}
