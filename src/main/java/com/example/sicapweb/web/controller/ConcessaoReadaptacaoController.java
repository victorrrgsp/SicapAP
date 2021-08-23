package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.pessoal.Readaptacao;
import br.gov.to.tce.model.ap.pessoal.Reintegracao;
import com.example.sicapweb.repository.ReadaptacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/documentoConcessaoReadaptacao")
public class ConcessaoReadaptacaoController {

    @Autowired
    private ReadaptacaoRepository readaptacaoRepository;

    @CrossOrigin
    @GetMapping()
    public ResponseEntity<List<Readaptacao>> findAll() {
        List<Readaptacao> list = readaptacaoRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        Readaptacao list = readaptacaoRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }
}
