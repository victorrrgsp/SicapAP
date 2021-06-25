package com.example.sicapweb.service;

import br.gov.to.tce.model.UnidadeGestora;
import com.example.sicapweb.dao.UnidadeGestoraDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = false)
public class UnidadeGestoraServiceImpl implements UnidadeGestoraService {

    @Autowired
    private UnidadeGestoraDao dao;

    @Override
    public void salvar(UnidadeGestora unidade) {
        dao.save(unidade);
    }

    @Override
    public void editar(UnidadeGestora unidade) {
        dao.update(unidade);
    }

    @Override
    public void excluir(String id) {
        dao.delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UnidadeGestora buscarPorId(String id) {
        return dao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnidadeGestora> buscarTodos() {
        return dao.findAll();
    }
}
