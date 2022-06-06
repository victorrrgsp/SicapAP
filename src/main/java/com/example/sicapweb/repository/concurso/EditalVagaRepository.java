package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.ap.concurso.EditalVaga;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;

@Repository
public class EditalVagaRepository extends DefaultRepository<EditalVaga, BigInteger> {

    public EditalVagaRepository(EntityManager em) {
        super(em);
    }

    public List<EditalVaga> buscarVagas() {
        return  getEntityManager().createNativeQuery(
                        "select a.* from EditalVaga a"+
                        " join InfoRemessa i on a.chave = i.chave " +
                                "where  i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "' ", EditalVaga.class)
                .getResultList();
    }

    public List<EditalVaga> buscarVagasPorEdital(Integer idEdital) {
        return getEntityManager().createNativeQuery(
                "select * from EditalVaga where idEdital = "
                        + idEdital, EditalVaga.class)
                .getResultList();
    }

    public EditalVaga buscarVagasPorCodigo(String codigo) {
        List<EditalVaga> list = getEntityManager().createNativeQuery("select * from EditalVaga ed" +
                " where codigoVaga = '" + codigo + "'    ", EditalVaga.class).getResultList();
        return list.get(0);
    }
}
