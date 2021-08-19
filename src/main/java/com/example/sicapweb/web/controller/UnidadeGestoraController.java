package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.UnidadeGestora;
import com.example.sicapweb.repository.UnidadeGestoraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping({"/unidadeGestora"})
public class UnidadeGestoraController {

    @Autowired
    private UnidadeGestoraRepository unidadeGestoraRepository;

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<UnidadeGestora>> findAll() {
        List<UnidadeGestora> list = unidadeGestoraRepository.findAll();
        return ResponseEntity.ok().body(list);
    }
    @CrossOrigin
    @GetMapping(path = {"/{Cnpj}"})
    public ResponseEntity<?> findById(@PathVariable String Cnpj) {
        UnidadeGestora list = unidadeGestoraRepository.buscaUnidadeGestoraPorCnpj(Cnpj);
        return ResponseEntity.ok().body(list);
    }
}
