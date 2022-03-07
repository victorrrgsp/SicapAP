package com.example.sicapweb.repository.geral;

import br.gov.to.tce.model.ap.relacional.Cargo;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<Object> buscarCargoPorUnidade(String cnpj) {
        List<Object> list = getEntityManager().createNativeQuery("SELECT DISTINCT wfp.cargo "+
                "FROM vwFolhaPagamento wfp "+
                "WHERE wfp.idUnidadeGestora = "+ cnpj ).getResultList();
        /*
        List<Map<String,Object>> retorno = new ArrayList<Map<String,Object>>();

        list.forEach(obj -> {
            Map<String,Object> aux = new HashMap<>();
            aux.put()
        });
        */
        return list;
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
