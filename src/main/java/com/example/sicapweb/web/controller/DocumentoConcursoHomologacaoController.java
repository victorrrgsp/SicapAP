package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.concurso.EditalHomologacao;
import br.gov.to.tce.model.ap.pessoal.Aposentadoria;
import com.example.sicapweb.repository.EditalHomologacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/documentoConcursoHomologacao")
public class DocumentoConcursoHomologacaoController {

    @Autowired
    private EditalHomologacaoRepository editalHomologacaoRepository;

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<EditalHomologacao>> findAll() {
        List<EditalHomologacao> list = editalHomologacaoRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        EditalHomologacao list = editalHomologacaoRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }
}
