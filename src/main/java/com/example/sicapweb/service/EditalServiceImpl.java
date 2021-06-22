package com.example.sicapweb.service;

import com.example.sicapweb.dao.EditalDao;
import br.gov.to.tce.model.ap.concurso.Edital;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Service
@Transactional(readOnly = false)
public class EditalServiceImpl implements EditalService {

    @Autowired
    private EditalDao dao;

    @Override
    public void salvar(Edital edital) {
        dao.save(edital);
    }

    @Override
    public void editar(Edital edital) {
        dao.update(edital);
    }

    @Override
    public void excluir(BigInteger id) {
        dao.delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Edital buscarPorId(BigInteger id) {
        return dao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Edital> buscarTodos() {
        return dao.findAll();
    }
}
