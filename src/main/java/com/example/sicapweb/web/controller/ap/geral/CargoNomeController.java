package com.example.sicapweb.web.controller.ap.geral;

import java.math.BigInteger;
import java.util.List;

import br.gov.to.tce.model.ap.estatico.CargoNome;
import br.gov.to.tce.model.ap.relacional.Cargo;
import br.gov.to.tce.model.ap.relacional.Lei;
import com.example.sicapweb.repository.geral.CargoNomeRepository;
import com.example.sicapweb.repository.geral.CargoRepository;

import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping({"/cargoNome"})
public class CargoNomeController  {
    @Autowired
    private CargoNomeRepository cargoNomeRepository;
    @Autowired
    private CargoRepository cargoRepository;

    @CrossOrigin
    @GetMapping()
    public ResponseEntity<List<CargoNome>> findTodos() {
        List<CargoNome> list = cargoRepository.buscaTodosCargoNome();
        return ResponseEntity.ok().body(list);
    }
    @CrossOrigin
    @GetMapping(path = {"/all"})
    public ResponseEntity<?> all() {
        CargoNome list = cargoNomeRepository.listaTodos();
        return ResponseEntity.ok().body(list);
    }
}
