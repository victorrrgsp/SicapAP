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
    @PostMapping("/upload/{inciso}/{id}")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file, @PathVariable String inciso, @PathVariable BigInteger id) {
        DocumentoAposentadoria documentoAposentadoria = new DocumentoAposentadoria();
        documentoAposentadoria.setAposentadoria(aposentadoriaRepository.findById(id));
        documentoAposentadoria.setInciso(inciso);
        String idCastor = super.setCastorFile(file, "Aposentadoria");
        documentoAposentadoria.setIdCastorFile(idCastor);
        documentoAposentadoria.setStatus(DocumentoAposentadoria.Status.Informado.getValor());
        documentoAposentadoriaRepository.save(documentoAposentadoria);
        return ResponseEntity.ok().body(idCastor);
    }

    @CrossOrigin
    @GetMapping(path = {"anexos/{inciso}/{id}"})
    public ResponseEntity<?> findByDocumento(@PathVariable String inciso, @PathVariable BigInteger id) {
        DocumentoAposentadoria list = documentoAposentadoriaRepository.buscarDocumentoAposentadoria(inciso, id).get(0);
        return ResponseEntity.ok().body(list);
    }
}
