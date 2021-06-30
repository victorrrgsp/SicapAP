package com.example.sicapweb.service;

import br.gov.to.tce.model.ap.concurso.EditalHomologacao;
import com.example.sicapweb.dao.EditalHomologacaoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Service @Transactional(readOnly = false)
public class EditalHomologacaoServiceImpl implements EditalHomologacaoService {

    @Autowired
    private EditalHomologacaoDao dao;


    @Override
    public void salvar(EditalHomologacao editalHomologacao) {
        dao.save(editalHomologacao);
    }

    @Override
    public void editar(EditalHomologacao editalHomologacao) {
        dao.update(editalHomologacao);
    }

    @Override
    public void excluir(BigInteger id) {
        dao.delete(id);
    }

    @Override @Transactional(readOnly = true)
    public EditalHomologacao buscarPorId(BigInteger id) {
        return dao.findById(id);
    }

    @Override @Transactional(readOnly = true)
    public List<EditalHomologacao> buscarTodos() {
        return dao.findAll();
    }
}
