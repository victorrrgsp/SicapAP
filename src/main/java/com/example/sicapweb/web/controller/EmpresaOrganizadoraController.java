package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.concurso.EmpresaOrganizadora;
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
@RequestMapping({"/concursoEmpresaOrganizadora"})
public class EmpresaOrganizadoraController {

    @Autowired
    private EmpresaOrganizadoraRepository empresaOrganizadoraRepository;

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<EmpresaOrganizadora>> findAll() {
        List<EmpresaOrganizadora> list = empresaOrganizadoraRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        EmpresaOrganizadora list = empresaOrganizadoraRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping
    public ResponseEntity<EmpresaOrganizadora> create(@RequestBody EmpresaOrganizadora empresaOrganizadora) {
        empresaOrganizadora.setChave(empresaOrganizadoraRepository.buscarPrimeiraRemessa());
        empresaOrganizadoraRepository.save(empresaOrganizadora);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(empresaOrganizadora.getId()).toUri();
        return ResponseEntity.created(uri).body(empresaOrganizadora);
    }

    @CrossOrigin
    @Transactional
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
    public ResponseEntity<EmpresaOrganizadora> update(@RequestBody EmpresaOrganizadora empresaOrganizadora, @PathVariable BigInteger id){
        empresaOrganizadora.setId(id);
        empresaOrganizadoraRepository.update(empresaOrganizadora);
        return ResponseEntity.noContent().build();
    }

    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public ResponseEntity<?> delete(@PathVariable BigInteger id) {
        empresaOrganizadoraRepository.delete(id);
        return ResponseEntity.noContent().build();
    }
}
