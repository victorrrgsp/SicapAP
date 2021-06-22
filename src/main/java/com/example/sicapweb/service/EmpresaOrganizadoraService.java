package com.example.sicapweb.service;

import br.gov.to.tce.model.ap.concurso.EmpresaOrganizadora;

import java.math.BigInteger;
import java.util.List;

public interface EmpresaOrganizadoraService {

    void salvar(EmpresaOrganizadora empresaOrganizadora);

    void editar(EmpresaOrganizadora empresaOrganizadora);

    void excluir(BigInteger id);

    EmpresaOrganizadora buscarPorId(BigInteger id);

    List<EmpresaOrganizadora> buscarTodos();
}
