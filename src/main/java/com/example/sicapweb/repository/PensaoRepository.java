package com.example.sicapweb.repository;

import br.gov.to.tce.model.ap.pessoal.Aposentadoria;
import br.gov.to.tce.model.ap.pessoal.Pensao;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public class PensaoRepository extends DefaultRepository<Pensao, BigInteger>{

    public List<Pensao> buscarPensaoRevisao() {
        return getEntityManager().createNativeQuery(
                "select * from Pensao where revisao = 1", Pensao.class)
                .getResultList();
    }
}
