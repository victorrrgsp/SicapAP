package com.example.sicapweb.service;

import br.gov.to.tce.model.UnidadeGestora;

import java.util.List;

public interface UnidadeGestoraService {

    void salvar(UnidadeGestora unidade);

    void editar(UnidadeGestora unidade);

    void excluir(String id);

    UnidadeGestora buscarPorId(String id);

    List<UnidadeGestora> buscarTodos();
}
