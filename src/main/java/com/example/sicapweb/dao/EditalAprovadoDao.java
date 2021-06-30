package com.example.sicapweb.dao;

import br.gov.to.tce.model.ap.concurso.EditalAprovado;

import java.math.BigInteger;
import java.util.List;

public interface EditalAprovadoDao {

    void save(EditalAprovado editalHomologacao);

    void update(EditalAprovado editalHomologacao);

    void delete(BigInteger id);

    EditalAprovado findById(BigInteger id);

    List<EditalAprovado> findAll();
}
