package com.example.sicapweb.repository.geral;

import br.gov.to.tce.model.ap.estatico.CargoNome;
import br.gov.to.tce.model.ap.relacional.Cargo;
import br.gov.to.tce.model.ap.relacional.UnidadeAdministrativa;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
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
        List<Cargo> list = getEntityManager().createNativeQuery("select ed.* from Cargo ed join InfoRemessa i on ed.chave = i.chave and i.idUnidadeGestora='"+ User.getUser(super.request).getUnidadeGestora().getId()+"'"+
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

    public List<Cargo> buscarCargoPorUG(String cnpj) {

        var query = getEntityManager().createNativeQuery(
                "with ids_cargo as " +
                        "(select t.codigoCargo,i.idUnidadeGestora, max(t.id) max_id_por_chave from SICAPAP21.dbo.Cargo t  join SICAPAP21.dbo.InfoRemessa  i on  t.chave = i.chave and i.idUnidadeGestora='"+cnpj+"'  group by t.codigoCargo,i.idUnidadeGestora ) " +
                        "select t.* from SICAPAP21.dbo.Cargo t  join SICAPAP21.dbo.InfoRemessa  i  on t.chave =  i.chave join ids_cargo ie on t.id = ie.max_id_por_chave and i.idUnidadeGestora=ie.idUnidadeGestora order by t.nomeCargo",Cargo.class);
        List<Cargo> list = query.getResultList();
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
