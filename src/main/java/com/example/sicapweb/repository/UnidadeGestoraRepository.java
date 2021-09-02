package com.example.sicapweb.repository;

import br.gov.to.tce.model.UnidadeGestora;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class UnidadeGestoraRepository extends DefaultRepository<UnidadeGestora, String> {
    @PersistenceContext
    EntityManager entityManager;

    public UnidadeGestoraRepository(EntityManager em) {
        super(em);
    }

    public UnidadeGestora buscaUnidadeGestoraPorCnpj(String Cnpj) {
        List<UnidadeGestora> list = entityManager.createNativeQuery("select * from UnidadeGestora " +
                " where id = '" + Cnpj + "'    ", UnidadeGestora.class).getResultList();
        return list.get(0);
    }

    public List<Integer> buscaVigenciaUnidadeGestoraPorCnpj(String Cnpj, Integer Exercicio, Integer Remessa) {

        return entityManager.createNativeQuery("select 1 as resposta where EXISTS("+
                "SELECT vug.id AS codvigencia, pj.codunidadegestora, vug.remessaini, vug.remessafim, vug.exercicioini, vug.exerciciofim " +
                "FROM cadun.dbo.VigenciaUnidadeGestora AS vug INNER JOIN cadun.dbo.vwUnidadeGestora AS pj ON vug.CodigoPessoaJuridica = pj.idPessoa " +
                "WHERE (vug.exercicioini = "+Exercicio+") AND (vug.remessaini <= "+Remessa+") AND (vug.exerciciofim IS NULL) AND (pj.codunidadegestora = '"+ Cnpj +"') AND (vug.idSistema = 29) OR " +
                "(vug.exercicioini = "+Exercicio+") AND (vug.remessaini <= "+Remessa+") AND (vug.exerciciofim = "+Exercicio+") AND (pj.codunidadegestora = '"+ Cnpj +"') AND (vug.idSistema = 29) AND " +
                "(vug.remessafim >= "+Remessa+") OR (vug.exercicioini = "+Exercicio+") AND (vug.remessaini <= "+Remessa+") AND (vug.exerciciofim > "+Exercicio+") AND (pj.codunidadegestora = '"+Cnpj+"') AND (vug.idSistema = 29) OR " +
                "(vug.exercicioini < "+Exercicio+") AND (vug.exerciciofim IS NULL) AND (pj.codunidadegestora = '"+Cnpj+"') AND (vug.idSistema = 29) OR " +
                "(vug.exercicioini < "+Exercicio+") AND (vug.exerciciofim = "+Exercicio+") AND (pj.codunidadegestora = '"+Cnpj+"') AND (vug.idSistema = 29) AND (vug.remessafim >= "+Remessa+" ) OR " +
                "(vug.exercicioini < "+Exercicio+") AND (vug.exerciciofim > "+Exercicio+") AND (pj.codunidadegestora = '"+Cnpj+"') AND (vug.idSistema = 29))").getResultList();
    }

}
