package com.example.sicapweb.repository.remessa;

import br.gov.to.tce.model.ap.folha.JustificativaGfip;
import br.gov.to.tce.model.ap.folha.documento.Gfip;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.math.BigInteger;

@Repository
public class JustificativaGfipRepository extends DefaultRepository<JustificativaGfip, BigInteger> {

    public JustificativaGfipRepository(EntityManager em) {
        super(em);
    }



    public JustificativaGfip buscaPorRemessa(Integer remessa,Integer exercicio){
        try{
            return  this.getEntityManager().createQuery("Select j from JustificativaGfip j where j.infoRemessa.exercicio = "+exercicio+"  and j.infoRemessa.remessa="+remessa, JustificativaGfip.class).setMaxResults(1).getSingleResult();
        }catch ( NoResultException e ){
            return null;
        }
    }

}
