package com.example.sicapweb.dao;

import br.gov.to.tce.model.ap.concurso.EditalVaga;

import java.math.BigInteger;
import java.util.List;

public interface EditalVagaDao {

    void save(EditalVaga editalVaga);

    void update(EditalVaga editalVaga);

    void delete(BigInteger id);

    EditalVaga findById(BigInteger id);

    List<EditalVaga> findAll();
}
