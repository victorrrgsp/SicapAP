package com.example.sicapweb.repository.geral;

import br.gov.to.tce.model.ap.relacional.UnidadeAdministrativa;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;

@Repository
public class UnidadeAdministrativaRepository extends DefaultRepository<UnidadeAdministrativa, BigInteger> {

    public UnidadeAdministrativaRepository(EntityManager em) {
        super(em);
    }

    public UnidadeAdministrativa buscarUnidadePorcodigo(String codigo) {
        List<UnidadeAdministrativa> list = getEntityManager().createNativeQuery("select * from UnidadeAdministrativa ed" +
                " where codigoUnidadeAdministrativa = '" + codigo + "'    ", UnidadeAdministrativa.class).getResultList();
        return list.get(0);
    }

    public UnidadeAdministrativa buscarUnidadePorCnpj(String cnpj) {
        List<UnidadeAdministrativa> list = getEntityManager().createNativeQuery("select * from UnidadeAdministrativa ed" +
                " where cnpfEnpresaOrganizadora = '" + cnpj + "'    ", UnidadeAdministrativa.class).getResultList();
        return list.get(0);
    }

    public List<Object[]> buscarremessa(int ano, int mes) {
        List<Object[]> list = getEntityManager().createNativeQuery(
                " select 'TODOS' cnpj, " +
                "'1 - TODOS' nomeentidade "+
        " union all "+
        " select qr.cnpj, pj.nomeentidade from "+
                " (	select "+
                        " distinct idUnidadeGestora cnpj "+
                        " from SICAPAP21..vwFolhaPagamento f "+
                    " where f.exercicio = "+ ano +
                            " and f.remessa = "+ mes+
                ")qr "+
        "left join cadun..vwPessoaSimples pj on pj.CNPJ = rtrim(qr.cnpj) "+
        "order by nomeentidade asc").getResultList();
        return list;
    }
}
