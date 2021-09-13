package com.example.sicapweb.repository;



import br.gov.to.tce.model.InfoRemessa;
import com.example.sicapweb.security.User;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public class InfoRemessaRepository extends DefaultRepository<InfoRemessa, String> {
    @Override
    public List<InfoRemessa> findAll() {
        return getEntityManager()
                .createNativeQuery("select * from "+
                        "InfoRemessa info where info.idUnidadeGestora = '"
                        + User.getUser().getUnidadeGestora().getId() + "'", InfoRemessa.class)
                .getResultList();
    }


    public InfoRemessa findById(String chave) {
        return (InfoRemessa) getEntityManager()
                .createNativeQuery("select * from "+
                        "InfoRemessa info where info.chave = '"
                        + chave + "'", InfoRemessa.class)
                .getSingleResult();
    }
}
