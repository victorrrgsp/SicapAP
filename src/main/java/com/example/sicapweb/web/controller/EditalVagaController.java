package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.concurso.EditalVaga;
import com.example.sicapweb.repository.EditalVagaRepository;
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
    @Transactional
    @PostMapping
    public ResponseEntity<EditalVaga> create(@RequestBody EditalVaga editalVaga) {
        editalVaga.setChave(editalVagaRepository.buscarPrimeiraRemessa());
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
