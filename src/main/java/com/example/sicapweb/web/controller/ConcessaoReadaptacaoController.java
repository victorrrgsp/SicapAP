package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.concessoes.DocumentoReadaptacao;
import br.gov.to.tce.model.ap.pessoal.Readaptacao;
import com.example.sicapweb.repository.DocumentoReadaptacaoRepository;
import com.example.sicapweb.repository.ReadaptacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/documentoConcessaoReadaptacao")
public class ConcessaoReadaptacaoController extends DefaultController<Readaptacao>{

    @Autowired
    private ReadaptacaoRepository readaptacaoRepository;

    @Autowired
    private DocumentoReadaptacaoRepository documentoReadaptacaoRepository;

    @CrossOrigin
    @GetMapping()
    public ResponseEntity<List<Readaptacao>> findAll() {
        List<Readaptacao> list = readaptacaoRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        Readaptacao list = readaptacaoRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping("/upload/{inciso}/{id}")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file, @PathVariable String inciso, @PathVariable BigInteger id) {
        DocumentoReadaptacao documentoReadaptacao = new DocumentoReadaptacao();
        documentoReadaptacao.setReadaptacao(readaptacaoRepository.findById(id));
        documentoReadaptacao.setInciso(inciso);
        String idCastor = super.setCastorFile(file, "Readaptacao");
        documentoReadaptacao.setIdCastorFile(idCastor);
        documentoReadaptacao.setStatus(DocumentoReadaptacao.Status.Informado.getValor());
        documentoReadaptacaoRepository.save(documentoReadaptacao);
        return ResponseEntity.ok().body(idCastor);
    }

    @CrossOrigin
    @GetMapping(path = {"anexos/{inciso}/{id}"})
    public ResponseEntity<?> findByDocumento(@PathVariable String inciso, @PathVariable BigInteger id) {
        DocumentoReadaptacao list = documentoReadaptacaoRepository.buscarDocumentoReadaptacao(inciso, id).get(0);
        return ResponseEntity.ok().body(list);
    }
}
