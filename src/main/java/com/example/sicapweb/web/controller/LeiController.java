package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.relacional.Ato;
import br.gov.to.tce.model.ap.relacional.Lei;
import com.example.sicapweb.repository.AtoRepository;
import com.example.sicapweb.repository.LeiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigInteger;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lei")
public class LeiController extends DefaultController<Lei> {

    @Autowired
    private LeiRepository leiRepository; 
    @Autowired
    private AtoRepository atoRepository;

    @ModelAttribute("atos")
    public List<Ato> editalList() {
        return atoRepository.findAll();
    }

    @ModelAttribute("atos")
    public List<Ato> atoList() {
        return atoRepository.findAll();
    }

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<Lei>> findAll() {
        return ResponseEntity.ok().body(leiRepository.findAll());
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        Lei list = leiRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping
    public ResponseEntity<Lei> create(@RequestBody Lei lei) {
        lei.setChave(leiRepository.buscarPrimeiraRemessa());

        leiRepository.save(lei);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(lei.getId()).toUri();
        return ResponseEntity.created(uri).body(lei);
    }

    @CrossOrigin
    @Transactional
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
    public ResponseEntity<Lei> update(@RequestBody Lei lei, @PathVariable BigInteger id) {
        lei.setChave(atoRepository.buscarPrimeiraRemessa());
        lei.setAto(atoRepository.findById(lei.getAto().getId()));
        lei.setId(id);

        leiRepository.update(lei);
        return ResponseEntity.noContent().build();
    }

    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public ResponseEntity<?> delete(@PathVariable BigInteger id) {
        leiRepository.delete(id);
        return ResponseEntity.noContent().build();
    }


    @CrossOrigin
    @Transactional
    @PostMapping("/upload")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok().body(super.setCastorFile(file, "Lei"));
    }
}
