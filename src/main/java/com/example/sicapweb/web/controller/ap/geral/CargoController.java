package com.example.sicapweb.web.controller.ap.geral;

import br.gov.to.tce.model.UnidadeGestora;
import br.gov.to.tce.model.ap.relacional.Ato;
import br.gov.to.tce.model.ap.relacional.Cargo;
import com.example.sicapweb.repository.geral.CargoRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping({"/cargo"})
public class CargoController {


    @Autowired
    private CargoRepository cargoRepository;

    @CrossOrigin
    @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<Cargo>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<Cargo> paginacaoUtil = cargoRepository.buscaPaginada(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }


    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<Cargo>> findAll() {
        List<Cargo> list = cargoRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/todos"})
    public ResponseEntity<List<Cargo>> findTodos() {
        List<Cargo> list = cargoRepository.buscaTodosCargo();
        return ResponseEntity.ok().body(list);
    }
}
