package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.relacional.Cargo;
import com.example.sicapweb.repository.CargoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/cargo"})
public class CargoController {

    @Autowired
    private CargoRepository cargoRepository;

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<Cargo>> findAll() {
        List<Cargo> list = cargoRepository.findAll();
        return ResponseEntity.ok().body(list);
    }
}
