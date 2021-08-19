package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.concurso.EditalAprovado;
import com.example.sicapweb.repository.EditalAprovadoRepository;
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
@RequestMapping("/concursoAprovado")
public class EditalAprovadoController {

    @Autowired
    private EditalAprovadoRepository editalAprovadoRepository;

    @Autowired
    private EditalVagaRepository editalVagaRepository;

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<EditalAprovado>> findAll() {
        List<EditalAprovado> list = editalAprovadoRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        EditalAprovado list = editalAprovadoRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping
    public ResponseEntity<EditalAprovado> create(@RequestBody EditalAprovado editalAprovado) {
        editalAprovado.setChave(editalAprovadoRepository.buscarPrimeiraRemessa());
        editalAprovado.setEditalVaga(editalVagaRepository.buscarVagasPorCodigo(editalAprovado.getCodigoVaga()));
        editalAprovadoRepository.save(editalAprovado);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(editalAprovado.getId()).toUri();
        return ResponseEntity.created(uri).body(editalAprovado);
    }

    @CrossOrigin
    @Transactional
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
    public ResponseEntity<EditalAprovado> update(@RequestBody EditalAprovado editalAprovado, @PathVariable BigInteger id){
        editalAprovado.setId(id);
        editalAprovado.setChave(editalAprovadoRepository.buscarPrimeiraRemessa());
        editalAprovado.setEditalVaga(editalVagaRepository.buscarVagasPorCodigo(editalAprovado.getCodigoVaga()));
        editalAprovadoRepository.update(editalAprovado);
        return ResponseEntity.noContent().build();
    }

    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public ResponseEntity<?> delete(@PathVariable BigInteger id) {
        editalAprovadoRepository.delete(id);
        return ResponseEntity.noContent().build();
    }
}
