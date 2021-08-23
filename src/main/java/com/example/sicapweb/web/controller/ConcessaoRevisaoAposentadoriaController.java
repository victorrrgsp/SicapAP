package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.pessoal.Aposentadoria;
import com.example.sicapweb.repository.AposentadoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/documentoConcessaoRevisaoAposentadoria")
public class ConcessaoRevisaoAposentadoriaController {

    @Autowired
    private AposentadoriaRepository aposentadoriaRepository;


    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<Aposentadoria>> findAposentadoriaRevisao() {
        List<Aposentadoria> list = aposentadoriaRepository.buscarAposentadoriaRevisao();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        Aposentadoria list = aposentadoriaRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }
}
