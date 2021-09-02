package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.concessoes.DocumentoReconducao;
import br.gov.to.tce.model.ap.pessoal.Reconducao;
import com.example.sicapweb.repository.DocumentoReconducaoRepository;
import com.example.sicapweb.repository.ReconducaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/documentoConcessaoReconducao")
public class ConcessaoReconducaoController extends DefaultController<Reconducao>{

    @Autowired
    private ReconducaoRepository reconducaoRepository;

    @Autowired
    private DocumentoReconducaoRepository documentoReconducaoRepository;

    @CrossOrigin
    @GetMapping()
    public ResponseEntity<List<Reconducao>> findAll() {
        List<Reconducao> list = reconducaoRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        Reconducao list = reconducaoRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping("/upload/{inciso}/{id}")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file, @PathVariable String inciso, @PathVariable BigInteger id) {
        DocumentoReconducao documentoReconducao = new DocumentoReconducao();
        documentoReconducao.setReconducao(reconducaoRepository.findById(id));
        documentoReconducao.setInciso(inciso);
        String idCastor = super.setCastorFile(file, "Reconducao");
        documentoReconducao.setIdCastorFile(idCastor);
        documentoReconducao.setStatus(DocumentoReconducao.Status.Informado.getValor());
        documentoReconducaoRepository.save(documentoReconducao);
        return ResponseEntity.ok().body(idCastor);
    }

    @CrossOrigin
    @GetMapping(path = {"anexos/{inciso}/{id}"})
    public ResponseEntity<?> findByDocumento(@PathVariable String inciso, @PathVariable BigInteger id) {
        DocumentoReconducao list = documentoReconducaoRepository.buscarDocumentooReconducao(inciso, id).get(0);
        return ResponseEntity.ok().body(list);
    }
}
