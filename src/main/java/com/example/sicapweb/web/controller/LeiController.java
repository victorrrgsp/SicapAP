package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.CastorFile;
import br.gov.to.tce.model.ap.relacional.Lei;
import com.example.sicapweb.repository.AtoRepository;
import com.example.sicapweb.repository.LeiRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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

    @CrossOrigin
    @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<Lei>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<Lei> paginacaoUtil = leiRepository.buscaPaginada(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
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
    @PostMapping("/upload/{id}")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file, @PathVariable BigInteger id) {
        Lei lei = new Lei();
        lei = leiRepository.findById(id);
        CastorFile castorFile = super.getCastorFile(file, "Lei");
        lei.setCastorFile(castorFile);
        leiRepository.update(lei);
        return ResponseEntity.ok().body(castorFile.getId());
    }

    @CrossOrigin
    @GetMapping(path = {"anexos/{id}"})
    public ResponseEntity<?> findByDocumento(@PathVariable BigInteger id) {
        Lei list = leiRepository.buscarDocumentoLei(id).get(0);
        return ResponseEntity.ok().body(list);
    }
}
