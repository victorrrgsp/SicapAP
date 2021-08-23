package com.example.sicapweb.web.controller;


import br.gov.to.tce.model.ap.pessoal.Reintegracao;
import com.example.sicapweb.repository.ReintegracaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/documentoConcessaoReintegracao")
public class ConcessaoReintegracaoController {

    @Autowired
    private ReintegracaoRepository reintegracaoRepository;

    @CrossOrigin
    @GetMapping()
    public ResponseEntity<List<Reintegracao>> findAll() {
        List<Reintegracao> list = reintegracaoRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        Reintegracao list = reintegracaoRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }
}
