package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.pessoal.Aposentadoria;
import com.example.sicapweb.repository.AposentadoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/documentoConcessaoAposentadoria")
public class ConcessaoAposentadoriaController extends DefaultController<Aposentadoria> {

    @Autowired
    private AposentadoriaRepository aposentadoriaRepository;

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

        return ResponseEntity.ok().body(super.setCastorFile(file, "AposentadoriaArquivos"));
    }
}
