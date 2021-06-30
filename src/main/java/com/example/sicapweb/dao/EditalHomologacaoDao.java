package com.example.sicapweb.dao;

import br.gov.to.tce.model.ap.concurso.EditalHomologacao;

import java.math.BigInteger;
import java.util.List;

public interface EditalHomologacaoDao {

    void save(EditalHomologacao editalHomologacao);

    void update(EditalHomologacao editalHomologacao);

    void delete(BigInteger id);

    EditalHomologacao findById(BigInteger id);

    List<EditalHomologacao> findAll();
}
