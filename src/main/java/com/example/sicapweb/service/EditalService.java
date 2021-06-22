package com.example.sicapweb.service;

import br.gov.to.tce.model.ap.concurso.Edital;

import java.math.BigInteger;
import java.util.List;

public interface EditalService {

    void salvar(Edital edital);

    void editar(Edital edital);

    void excluir(BigInteger id);

    Edital buscarPorId(BigInteger id);

    List<Edital> buscarTodos();
}
