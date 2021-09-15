package com.example.sicapweb.repository.concessao;

import br.gov.to.tce.model.ap.pessoal.Aposentadoria;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;

@Repository
public class AposentadoriaRepository extends DefaultRepository<Aposentadoria, BigInteger> {

    public AposentadoriaRepository(EntityManager em) {
        super(em);
    }

    public List<Aposentadoria> buscarAposentadorias() {
        return getEntityManager().createNativeQuery(
                "select * from Aposentadoria where reversao = 0 and revisao = 0 and tipoAposentadoria not in (6,7)", Aposentadoria.class)
                .getResultList();
    }

    public List<Aposentadoria> buscarAposentadoriaPorTipo(Integer tipoAposentadoria) {
        return getEntityManager().createNativeQuery(
                "select * from Aposentadoria where reversao = 0 and revisao = 0 and tipoAposentadoria = "
                        + tipoAposentadoria, Aposentadoria.class)
                .getResultList();
    }

    public List<Aposentadoria> buscarAposentadoriaRevisao() {
        return getEntityManager().createNativeQuery(
                "select * from Aposentadoria where revisao = 1 and tipoAposentadoria not in (6,7)", Aposentadoria.class)
                .getResultList();
    }

    public List<Aposentadoria> buscarAposentadoriaRevisaoReserva() {
        return getEntityManager().createNativeQuery(
                "select * from Aposentadoria where revisao = 1 and tipoAposentadoria = "
                        + Aposentadoria.TipoAposentadoria.Reserva.getValor(), Aposentadoria.class)
                .getResultList();
    }

    public List<Aposentadoria> buscarAposentadoriaRevisaoReforma() {
        return getEntityManager().createNativeQuery(
                "select * from Aposentadoria where revisao = 1 and tipoAposentadoria = "
                        + Aposentadoria.TipoAposentadoria.Reforma.getValor(), Aposentadoria.class)
                .getResultList();
    }

    public List<Aposentadoria> buscarReversaoAposentadoriaReserva() {
        return getEntityManager().createNativeQuery(
                "select * from Aposentadoria where reversao = 1 and tipoAposentadoria != 7", Aposentadoria.class)
                .getResultList();
    }
}
