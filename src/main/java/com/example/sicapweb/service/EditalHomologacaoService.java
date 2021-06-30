package com.example.sicapweb.service;

import br.gov.to.tce.model.ap.concurso.EditalHomologacao;

import java.math.BigInteger;
import java.util.List;

public interface EditalHomologacaoService {

    void salvar(EditalHomologacao editalHomologacao);

    void editar(EditalHomologacao editalHomologacao);

    void excluir(BigInteger id);

    EditalHomologacao buscarPorId(BigInteger id);

    List<EditalHomologacao> buscarTodos();
}
