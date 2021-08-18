package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.concurso.Edital;
import com.example.sicapweb.repository.EditalRepository;
import com.example.sicapweb.repository.EmpresaOrganizadoraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigInteger;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/concursoEdital")
public class EditalController {

    @Autowired
    private EditalRepository editalRepository;

    @Autowired
    private EmpresaOrganizadoraRepository empresaOrganizadoraRepository;

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<Edital>> findAll() {
        List<Edital> list = editalRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        Edital list = editalRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping
    public ResponseEntity<Edital> create(@RequestBody Edital edital) {
        edital.setInfoRemessa(editalRepository.buscarPrimeiraRemessa());
        editalRepository.save(edital);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(edital.getId()).toUri();
        return ResponseEntity.created(uri).body(edital);
    }

    @CrossOrigin
    @Transactional
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
    public ResponseEntity<Edital> update(@RequestBody Edital edital, @PathVariable BigInteger id) {
        edital.setInfoRemessa(editalRepository.buscarPrimeiraRemessa());
        edital.setId(id);
        edital.setEmpresaOrganizadora(empresaOrganizadoraRepository.buscaEmpresaPorCnpj(edital.getCnpjEmpresaOrganizadora()));
        editalRepository.update(edital);
        return ResponseEntity.noContent().build();
    }

    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public ResponseEntity<?> delete(@PathVariable BigInteger id) {
        editalRepository.delete(id);
        return ResponseEntity.noContent().build();
    }

}
