package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.ap.concurso.Edital;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;

@Repository
public class EditalRepository extends DefaultRepository<Edital, BigInteger> {

    public EditalRepository(EntityManager em) {
        super(em);
    }

    public Edital buscarEditalPorNumero(String numeroEdital) {
        List<Edital> list = getEntityManager().createNativeQuery("select * from Edital ed" +
                " where numeroEdital = " + numeroEdital, Edital.class).getResultList();
        return list.get(0);
    }

    public List<Edital> buscarEditaisNaoHomologados() {
        List<Edital> list = getEntityManager().createNativeQuery("select * from Edital ed where " +
                "not exists (select * from EditalHomologacao eh where ed.id = eh.idEdital)", Edital.class).getResultList();
        return list;
    }

}
