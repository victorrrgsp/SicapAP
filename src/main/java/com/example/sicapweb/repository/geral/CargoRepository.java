package com.example.sicapweb.repository.geral;

import br.gov.to.tce.model.ap.relacional.Cargo;
import com.example.sicapweb.repository.DefaultRepository;
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
                " where codigoCargo = '" + codigo + "'", Cargo.class).getResultList();
        return list.get(0);
    }

    public List<Cargo> buscaTodosCargos() {
        List<Cargo> list = entityManager.createNativeQuery("select * from Cargo "
                , Cargo.class).getResultList();
        return list;
    }
  public List<Cargo> buscaTodosCargo() {
    List<Cargo> list = entityManager.createNativeQuery("select * from Cargo ORDER BY nomeCargo ASC"
      , Cargo.class).getResultList();
    return list;
  }
}
