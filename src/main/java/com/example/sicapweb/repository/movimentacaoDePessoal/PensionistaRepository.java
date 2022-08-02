package com.example.sicapweb.repository.movimentacaoDePessoal;

import br.gov.to.tce.application.ApplicationException;
import br.gov.to.tce.model.ap.pessoal.Desligamento;
import br.gov.to.tce.model.ap.pessoal.Pensionista;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;

@Repository
    public class PensionistaRepository extends DefaultRepository<Pensionista, BigInteger> {

        public PensionistaRepository(EntityManager em) {
            super(em);
        }

        public List<Object> getDependentesPensao(String cpfServidorPensao) throws ApplicationException {
            Query query = getEntityManager().createNativeQuery("" +
                    "select distinct p.cpfPensionista as cpf, p.nome " +
                    "from SICAPAP21..Pensionista p " +
                    "         join SICAPAP21..Pensao pe on p.cpfServidor = pe.cpfServidor " +
                    "where pe.cpfServidor = '" + cpfServidorPensao +"'");
            List<Object> result = buscarSQL(query, "p.cpfPensionista as cpf, p.nome");
            return result;
        }
    }




