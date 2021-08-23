package com.example.sicapweb.repository;

import br.gov.to.tce.model.ap.pessoal.Aposentadoria;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AposentadoriaRepository extends DefaultRepository<Aposentadoria, String> {

    public List<Aposentadoria> buscarAposentadoriaTipoReserva(Integer tipoAposentadoria) {
        return getEntityManager().createNativeQuery(
                "select * from Aposentadoria where tipoAposentadoria = "
                        + tipoAposentadoria, Aposentadoria.class)
                .getResultList();
    }

    public List<Aposentadoria> buscarAposentadoriaRevisao() {
        return getEntityManager().createNativeQuery(
                "select * from Aposentadoria where revisao = 0", Aposentadoria.class)
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
                "select * from Aposentadoria where reversao = 1 and tipoAposentadoria = "
                        + Aposentadoria.TipoAposentadoria.Reserva.getValor(), Aposentadoria.class)
                .getResultList();
    }
}
