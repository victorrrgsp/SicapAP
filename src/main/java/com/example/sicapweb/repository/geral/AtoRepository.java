package com.example.sicapweb.repository.geral;

import br.gov.to.tce.model.ap.relacional.Ato;
import br.gov.to.tce.model.ap.relacional.Lotacao;
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

    public Ato buscarAtoPorNumero(String numero, int tipoAto) {
        List<Ato> list = getEntityManager().createNativeQuery("select top 1* from Ato a " +
                "where a.numeroAto = '"+numero+"' " +
                "and a.tipoAto = '"+tipoAto+" ' " +
                " order by id desc", Ato.class).getResultList();
        return list.get(0);
    }
}
