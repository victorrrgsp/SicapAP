package com.example.sicapweb.service;

import br.gov.to.tce.model.ap.concurso.EditalVaga;

import java.math.BigInteger;
import java.util.List;

public interface EditalVagaService {

    void salvar(EditalVaga editalVaga);

    void editar(EditalVaga editalVaga);

    void excluir(BigInteger id);

    EditalVaga buscarPorId(BigInteger id);

    List<EditalVaga> buscarTodos();
}
