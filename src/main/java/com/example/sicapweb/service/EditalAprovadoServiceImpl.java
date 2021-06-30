package com.example.sicapweb.service;

import br.gov.to.tce.model.ap.concurso.EditalAprovado;
import com.example.sicapweb.dao.EditalAprovadoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Service @Transactional(readOnly = false)
public class EditalAprovadoServiceImpl implements EditalAprovadoService {

    @Autowired
    private EditalAprovadoDao dao;


    @Override
    public void salvar(EditalAprovado editalAprovado) {
        dao.save(editalAprovado);
    }

    @Override
    public void editar(EditalAprovado editalAprovado) {
        dao.update(editalAprovado);
    }

    @Override
    public void excluir(BigInteger id) {
        dao.delete(id);
    }

    @Override @Transactional(readOnly = true)
    public EditalAprovado buscarPorId(BigInteger id) {
        return dao.findById(id);
    }

    @Override @Transactional(readOnly = true)
    public List<EditalAprovado> buscarTodos() {
        return dao.findAll();
    }
}
