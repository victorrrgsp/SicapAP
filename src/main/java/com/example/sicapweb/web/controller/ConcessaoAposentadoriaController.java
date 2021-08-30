package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.concessoes.DocumentoAposentadoria;
import br.gov.to.tce.model.ap.pessoal.Aposentadoria;
import com.example.sicapweb.repository.AposentadoriaRepository;
import com.example.sicapweb.repository.DocumentoAposentadoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/documentoConcessaoAposentadoria")
public class ConcessaoAposentadoriaController extends DefaultController<Aposentadoria> {

    @Autowired
    private AposentadoriaRepository aposentadoriaRepository;

    @Autowired
    private DocumentoAposentadoriaRepository documentoAposentadoriaRepository;

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<Aposentadoria>> findAll() {
        List<Aposentadoria> list = aposentadoriaRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping("/upload")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok().body(super.setCastorFile(file, "Aposentadoria"));
    }

    @CrossOrigin
    @Transactional
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
    public ResponseEntity<DocumentoAposentadoria> update(@RequestBody DocumentoAposentadoria documentoAposentadoria, @PathVariable BigInteger id) {
        documentoAposentadoria.setId(id);
        documentoAposentadoriaRepository.update(documentoAposentadoria);
        return ResponseEntity.noContent().build();
    }
}
