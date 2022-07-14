package com.example.sicapweb.repository.geral;

import br.gov.to.tce.model.ap.estatico.CargoNome;
import br.gov.to.tce.model.ap.relacional.Cargo;
import br.gov.to.tce.model.ap.relacional.UnidadeAdministrativa;
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

        var query = getEntityManager().createNativeQuery(
                "SELECT DISTINCT wfp.cargo "+
                "FROM vwFolhaPagamento wfp "+
                "WHERE wfp.idUnidadeGestora = '"+cnpj+ "'"+
                "order by wfp.cargo");
        List<Object> list = query.getResultList();
        /*
        List<Map<String,Object>> retorno = new ArrayList<Map<String,Object>>();

        list.forEach(obj -> {
            Map<String,Object> aux = new HashMap<>();
            aux.put()
        });
        */
        return list;
    }
    public List<Object> buscarCargoPorRemessa(String cnpj,int ano,int mes ) {

        var query = getEntityManager().createNativeQuery(
                " SELECT DISTINCT wfp.cargo "+
                " FROM vwFolhaPagamento wfp "+
                " WHERE wfp.idUnidadeGestora = '"+cnpj+"'"+
                " and wfp.remessa ="+mes+
                " and wfp.exercicio ="+ano+
                " order by wfp.cargo");
        List<Object> list = query.getResultList();
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
  public List<CargoNome> buscaTodosCargoNome() {
    List<CargoNome> list = entityManager.createNativeQuery("select * from CargoNome "
      , CargoNome.class).getResultList();
    return list;
  }

    public List<Object> buscarCargoPorUG(String cnpj) {

        var query = getEntityManager().createNativeQuery(
                "SELECT DISTINCT c.nomeCargo "+
                        "FROM Cargo c "+
                        " join InfoRemessa  i on c.chave=i.chave " +
                        "WHERE i.idUnidadeGestora = '"+cnpj+ "'"+
                        "order by c.nomeCargo");
        List<Object> list = query.getResultList();
        /*
        List<Map<String,Object>> retorno = new ArrayList<Map<String,Object>>();

        list.forEach(obj -> {
            Map<String,Object> aux = new HashMap<>();
            aux.put()
        });
        */
        return list;
    }


}
