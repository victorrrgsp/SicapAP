package com.example.sicapweb.repository.geral;

import br.gov.to.tce.model.ap.estatico.CargoNome;
import br.gov.to.tce.model.ap.relacional.UnidadeAdministrativa;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;


    @Repository
    public class CargoNomeRepository extends DefaultRepository<CargoNome, BigInteger> {

        public CargoNomeRepository(EntityManager em) {
            super(em);
        }

        public CargoNome buscarCargoNomePorId(String id) {
            List<CargoNome> list = getEntityManager().createNativeQuery("select top 1 * from CargoNome cn" +
                    " where idCargoNome = '" + id + "' order by id desc ", CargoNome.class).getResultList();
            return list.get(0);
        }

        public CargoNome buscarCargoNomeDistinct(String id) {
            List<CargoNome> list = getEntityManager().createNativeQuery("select top 1 * from CargoNome cn" +
                    " where idCargoNome = '" + id + "' order by id desc ", CargoNome.class).getResultList();
            return list.get(0);
        }
        public CargoNome listaTodos() {
            List<CargoNome> list = getEntityManager().createNativeQuery("select distinct idcargonome, nome" +
                    " from SICAPAP21..CargoNome", CargoNome.class).getResultList();
            return list.get(0);
        }
    }

