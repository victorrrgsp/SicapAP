package com.example.sicapweb.repository;

import br.gov.to.tce.model.ap.relacional.Cargo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;

@Repository
public class CargoRepository extends DefaultRepository<Cargo, BigInteger> {

    public CargoRepository(EntityManager em) {
        super(em);
    }

    public Cargo buscarCargoPorcodigo(String codigo) {
        List<Cargo> list = getEntityManager().createNativeQuery("select * from Cargo ed" +
                " where codigoCargo = " + codigo, Cargo.class).getResultList();
        return list.get(0);
    }
}
