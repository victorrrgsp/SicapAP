package com.example.sicapweb.repository.folhaDePagamento;

import br.gov.to.tce.model.ap.estatico.FolhaItemESocial;
import br.gov.to.tce.model.ap.relacional.Cargo;
import br.gov.to.tce.model.ap.relacional.FolhaItem;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;

@Repository
    public class FolhaItemEsocialRepository extends DefaultRepository<FolhaItemESocial, BigInteger> {

        public FolhaItemEsocialRepository(EntityManager em) {
            super(em);
        }
        public FolhaItemESocial findByCodigo(String codigo) {
            List<FolhaItemESocial> list = getEntityManager().createNativeQuery(" select * from FolhaItemESocial FI where codigoFolhaItemESocial  = \'"+codigo+"\'", FolhaItemESocial.class).getResultList();
            return list.get(0);
        }
    }



