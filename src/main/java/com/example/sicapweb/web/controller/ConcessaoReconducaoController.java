package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.pessoal.Reconducao;
import br.gov.to.tce.model.ap.pessoal.Reintegracao;
import com.example.sicapweb.repository.ReconducaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/documentoConcessaoReconducao")
public class ConcessaoReconducaoController {

    @Autowired
    private ReconducaoRepository reconducaoRepository;

    @CrossOrigin
    @GetMapping()
    public ResponseEntity<List<Reconducao>> findAll() {
        List<Reconducao> list = reconducaoRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        Reconducao list = reconducaoRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }
}
