package com.example.sicapweb.web.controller.ap.geral;

import br.gov.to.tce.model.UnidadeGestora;
import br.gov.to.tce.model.ap.relacional.Cargo;
import com.example.sicapweb.repository.geral.CargoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @CrossOrigin
    @GetMapping("/{unidade}")
    public ResponseEntity<List<Object>> findByunidade( @PathVariable String unidade ) {
        List<Object> list = cargoRepository.buscarCargoPorUnidade(unidade);
        return ResponseEntity.ok().body(list);
    }
    @CrossOrigin
    @GetMapping("/{unidade}/{mes}/{ano}")
    public ResponseEntity<List<Object>> findByunidade( @PathVariable String unidade,@PathVariable int mes,@PathVariable int ano ) {
        List<Object> list = cargoRepository.buscarCargoPorRemessa(unidade,ano,mes);
        return ResponseEntity.ok().body(list);
    }
    @CrossOrigin
    @GetMapping(path = {"/todos"})
    public ResponseEntity<List<Cargo>> findTodos() {
        List<Cargo> list = cargoRepository.buscaTodosCargo();
        return ResponseEntity.ok().body(list);
    }
}
