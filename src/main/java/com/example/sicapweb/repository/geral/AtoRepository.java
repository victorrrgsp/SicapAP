package com.example.sicapweb.repository.geral;

import br.gov.to.tce.model.ap.relacional.Ato;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;

@Repository
public class AtoRepository extends DefaultRepository<Ato, BigInteger> {
    public AtoRepository(EntityManager em) {
        super(em);
    }

    public List<Ato> findAll() {
        return getEntityManager()
                .createQuery("select distinct a from Ato" +
                        " a, InfoRemessa info where a.infoRemessa.chave = info.chave and info.idUnidadeGestora = '"
                        + User.getUser(request).getUnidadeGestora().getId() + "'", Ato.class)
                .getResultList();
    }
}
