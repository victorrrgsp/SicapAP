package com.example.sicapweb.web.controller;

import com.example.sicapweb.repository.AssinarRemessaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/assinarRemessa")
public class AssinarRemessaController {

    @Autowired
    private AssinarRemessaRepository assinarRemessaRepository;

    @CrossOrigin
    @GetMapping(path = {"/{cargo}"})
    public ResponseEntity<String> findResponsavel(@PathVariable String cargo) {
        String resp = assinarRemessaRepository.buscarResponsavelAssinatura(cargo);
        return ResponseEntity.ok().body(resp);
    }
}
