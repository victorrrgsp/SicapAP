package com.example.sicapweb.service;

import br.gov.to.tce.model.ap.concurso.EditalAprovado;

import java.math.BigInteger;
import java.util.List;

public interface EditalAprovadoService {

    void salvar(EditalAprovado editalHomologacao);

    void editar(EditalAprovado editalHomologacao);

    void excluir(BigInteger id);

    EditalAprovado buscarPorId(BigInteger id);

    List<EditalAprovado> buscarTodos();
}
