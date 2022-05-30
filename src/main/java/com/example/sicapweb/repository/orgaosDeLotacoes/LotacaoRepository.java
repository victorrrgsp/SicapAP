package com.example.sicapweb.repository.orgaosDeLotacoes;

import br.gov.to.tce.model.ap.relacional.Lotacao;

import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;

@Repository
    public class LotacaoRepository extends DefaultRepository<Lotacao, BigInteger> {

        public LotacaoRepository(EntityManager em) {
            super(em);
        }

    public Lotacao buscarLotacaoPorcodigo(String codigo) {
        List<Lotacao> list = getEntityManager().createNativeQuery("select * from Lotacao l" +
                " where codigoLotacao = '" + codigo + "'    ", Lotacao.class).getResultList();
        return list.get(0);
    }

    }


