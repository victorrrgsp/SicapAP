package com.example.sicapweb.dao;

import br.gov.to.tce.model.ap.concurso.EmpresaOrganizadora;

import java.math.BigInteger;
import java.util.List;

public interface EmpresaOrganizadoraDao {

    void save(EmpresaOrganizadora empresaOrganizadora);

    void update(EmpresaOrganizadora empresaOrganizadora);

    void delete(BigInteger id);

    EmpresaOrganizadora findById(BigInteger id);

    List<EmpresaOrganizadora> findAll();
}
