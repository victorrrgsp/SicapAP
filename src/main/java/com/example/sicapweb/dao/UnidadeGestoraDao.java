package com.example.sicapweb.dao;

import br.gov.to.tce.model.UnidadeGestora;

import java.util.List;

public interface UnidadeGestoraDao {

    void save(UnidadeGestora unidade);

    void update(UnidadeGestora unidade);

    void delete(String id);

    UnidadeGestora findById(String id);

    List<UnidadeGestora> findAll();
}
