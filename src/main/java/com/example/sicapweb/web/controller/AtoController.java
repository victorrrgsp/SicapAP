package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.relacional.Ato;
import com.example.sicapweb.repository.AtoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/ato")
public class AtoController {

    @Autowired
    private AtoRepository atoRepository;

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<Ato>> findAll() {
        List<Ato> list = atoRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        Ato list = atoRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

}
