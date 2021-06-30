package com.example.sicapweb.service;

import br.gov.to.tce.model.ap.concurso.EditalVaga;
import com.example.sicapweb.dao.EditalVagaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Service @Transactional(readOnly = false)
public class EditalVagaServiceImpl implements EditalVagaService {

    @Autowired
    private EditalVagaDao dao;


    @Override
    public void salvar(EditalVaga editalVaga) {
        dao.save(editalVaga);
    }

    @Override
    public void editar(EditalVaga editalVaga) {
        dao.update(editalVaga);
    }

    @Override
    public void excluir(BigInteger id) {
        dao.delete(id);
    }

    @Override @Transactional(readOnly = true)
    public EditalVaga buscarPorId(BigInteger id) {
        return dao.findById(id);
    }

    @Override @Transactional(readOnly = true)
    public List<EditalVaga> buscarTodos() {
        return dao.findAll();
    }
}
