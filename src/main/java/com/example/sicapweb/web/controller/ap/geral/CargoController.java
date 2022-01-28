package com.example.sicapweb.web.controller.ap.geral;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.relacional.Cargo;
import com.example.sicapweb.repository.geral.CargoNomeRepository;
import com.example.sicapweb.repository.geral.CargoRepository;
import com.example.sicapweb.repository.geral.LeiRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping({"/cargo"})
public class CargoController  extends DefaultController<Cargo> {


    @Autowired
    private CargoRepository cargoRepository;

    @Autowired
    private CargoNomeRepository cargoNomeRepository;
    @Autowired
    private LeiRepository leiRepository;


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
    @GetMapping(path = {"findByCodigo/{codigo}"})
    public ResponseEntity<?> findById(@PathVariable String codigo) {
        Cargo list = cargoRepository.buscarCargoPorcodigo(codigo);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
    public ResponseEntity<Cargo> update(@RequestBody Cargo cargo, @PathVariable BigInteger id) {

        InfoRemessa chave = cargoRepository.findById(id).getChave();
        cargo.setLei(leiRepository.findById(cargo.getLei().getId()));
        cargo.setCargoNome(cargoNomeRepository.findById(cargo.getCargoNome().getId()));
        cargo.setId(id);
        cargo.setChave(chave);
        cargoRepository.update(cargo);
        return ResponseEntity.noContent().build();
    
    }

    @CrossOrigin
    @GetMapping(path = {"/todos"})
    public ResponseEntity<List<Cargo>> findTodos() {
        List<Cargo> list = cargoRepository.buscaTodosCargo();
        return ResponseEntity.ok().body(list);
    }
}
