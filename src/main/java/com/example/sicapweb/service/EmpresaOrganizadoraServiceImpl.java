package com.example.sicapweb.service;

import com.example.sicapweb.dao.EmpresaOrganizadoraDao;
import br.gov.to.tce.model.ap.concurso.EmpresaOrganizadora;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Service @Transactional(readOnly = false)
public class EmpresaOrganizadoraServiceImpl implements EmpresaOrganizadoraService {

    @Autowired
    private EmpresaOrganizadoraDao dao;


    @Override
    public void salvar(EmpresaOrganizadora empresaOrganizadora) {
        dao.save(empresaOrganizadora);
    }

    @Override
    public void editar(EmpresaOrganizadora empresaOrganizadora) {
        dao.update(empresaOrganizadora);
    }

    @Override
    public void excluir(BigInteger id) {
        dao.delete(id);
    }

    @Override @Transactional(readOnly = true)
    public EmpresaOrganizadora buscarPorId(BigInteger id) {
        return dao.findById(id);
    }

    @Override @Transactional(readOnly = true)
    public List<EmpresaOrganizadora> buscarTodos() {
        return dao.findAll();
    }
}
