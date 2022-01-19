package com.example.sicapweb.web.controller.ap.geral;

import java.util.List;

import com.example.sicapweb.repository.geral.CargoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.to.tce.model.ap.estatico.CargoNome;

@RestController
@RequestMapping({"/cargoNome"})
public class cargoNomeController {

    @Autowired
    private CargoRepository cargoRepository;

    @CrossOrigin
    @GetMapping()
    public ResponseEntity<List<CargoNome>> findTodos() {
        List<CargoNome> list = cargoRepository.buscaTodosCargoNome();
        return ResponseEntity.ok().body(list);
    }
}
