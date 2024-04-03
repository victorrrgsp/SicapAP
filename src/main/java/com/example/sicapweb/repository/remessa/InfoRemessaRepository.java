package com.example.sicapweb.repository.remessa;



import br.gov.to.tce.model.InfoRemessa;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InfoRemessaRepository extends DefaultRepository<InfoRemessa, String> {
    @Override
    public List<InfoRemessa> findAll() {
        try {
            return getEntityManager()
                    .createNativeQuery("select * from " +
                            "InfoRemessa info where info.idUnidadeGestora = " +
                            "'" + user.getUser(super.request).getUnidadeGestora().getId() + "'"
//                            "'01613619000134'"
                            , InfoRemessa.class)
                    .getResultList();
        }catch (Exception e)
        {
            return null;
        }
    }


    public InfoRemessa findById(String chave) {
        return (InfoRemessa) getEntityManager()
                .createNativeQuery("select * from "+
                        "InfoRemessa info where info.chave = '"
                        + chave + "'", InfoRemessa.class)
                .getSingleResult();
    }
}
