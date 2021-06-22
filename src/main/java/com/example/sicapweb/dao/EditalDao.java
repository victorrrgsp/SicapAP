package com.example.sicapweb.dao;

import br.gov.to.tce.model.ap.concurso.Edital;

import java.math.BigInteger;
import java.util.List;

public interface EditalDao {

    void save(Edital edital);

    void update(Edital edital);

    void delete(BigInteger id);

    Edital findById(BigInteger id);

    List<Edital> findAll();
}
