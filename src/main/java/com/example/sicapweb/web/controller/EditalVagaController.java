package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.concurso.EditalVaga;
import com.example.sicapweb.repository.CargoRepository;
import com.example.sicapweb.repository.EditalRepository;
import com.example.sicapweb.repository.EditalVagaRepository;
import com.example.sicapweb.repository.UnidadeAdministrativaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigInteger;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/concursoVaga")
public class EditalVagaController {

    @Autowired
    private EditalVagaRepository editalVagaRepository;

    @Autowired
    private EditalRepository editalRepository;

    @Autowired
    private UnidadeAdministrativaRepository unidadeAdministrativaRepository;

    @Autowired
    private CargoRepository cargoRepository;

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<EditalVaga>> findAll() {
        List<EditalVaga> list = editalVagaRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        EditalVaga list = editalVagaRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping("/buscar/{id}")
    public ResponseEntity<List<EditalVaga>> findVagasByIdEdital(@PathVariable Integer id) {
        List<EditalVaga> list = editalVagaRepository.buscarVagasPorEdital(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping
    public ResponseEntity<EditalVaga> create(@RequestBody EditalVaga editalVaga) {
        editalVaga.setChave(editalVagaRepository.buscarPrimeiraRemessa());
        editalVaga.setEdital(editalRepository.buscarEditalPorNumero(editalVaga.getNumeroEdital()));
        editalVaga.setUnidadeAdministrativa(unidadeAdministrativaRepository.buscarUnidadePorcodigo(editalVaga.codigoUnidadeAdministrativa));
        editalVaga.setCargo(cargoRepository.buscarCargoPorcodigo(editalVaga.codigoCargo));
        editalVagaRepository.save(editalVaga);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(editalVaga.getId()).toUri();
        return ResponseEntity.created(uri).body(editalVaga);
    }

    @CrossOrigin
    @Transactional
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
    public ResponseEntity<EditalVaga> update(@RequestBody EditalVaga editalVaga, @PathVariable BigInteger id){
        editalVaga.setId(id);
        editalVaga.setChave(editalVagaRepository.buscarPrimeiraRemessa());
        editalVaga.setEdital(editalRepository.buscarEditalPorNumero(editalVaga.getNumeroEdital()));
        editalVaga.setUnidadeAdministrativa(unidadeAdministrativaRepository.buscarUnidadePorcodigo(editalVaga.codigoUnidadeAdministrativa));
        editalVaga.setCargo(cargoRepository.buscarCargoPorcodigo(editalVaga.codigoCargo));
        editalVagaRepository.update(editalVaga);
        return ResponseEntity.noContent().build();
    }

    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public ResponseEntity<?> delete(@PathVariable BigInteger id) {
        editalVagaRepository.delete(id);
        return ResponseEntity.noContent().build();
    }
}
