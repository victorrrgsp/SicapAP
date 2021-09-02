package com.example.sicapweb.web.controller;


import br.gov.to.tce.model.ap.concessoes.DocumentoReintegracao;
import br.gov.to.tce.model.ap.pessoal.Reintegracao;
import com.example.sicapweb.repository.DocumentoReintegracaoRepository;
import com.example.sicapweb.repository.ReintegracaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/documentoConcessaoReintegracao")
public class ConcessaoReintegracaoController extends DefaultController<Reintegracao>{

    @Autowired
    private ReintegracaoRepository reintegracaoRepository;

    @Autowired
    private DocumentoReintegracaoRepository documentoReintegracaoRepository;

    @CrossOrigin
    @GetMapping()
    public ResponseEntity<List<Reintegracao>> findAll() {
        List<Reintegracao> list = reintegracaoRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        Reintegracao list = reintegracaoRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping("/upload/{inciso}/{id}")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file, @PathVariable String inciso, @PathVariable BigInteger id) {
        DocumentoReintegracao documentoReintegracao = new DocumentoReintegracao();
        documentoReintegracao.setReintegracao(reintegracaoRepository.findById(id));
        documentoReintegracao.setInciso(inciso);
        String idCastor = super.setCastorFile(file, "Reintegracao");
        documentoReintegracao.setIdCastorFile(idCastor);
        documentoReintegracao.setStatus(DocumentoReintegracao.Status.Informado.getValor());
        documentoReintegracaoRepository.save(documentoReintegracao);
        return ResponseEntity.ok().body(idCastor);
    }

    @CrossOrigin
    @GetMapping(path = {"anexos/{inciso}/{id}"})
    public ResponseEntity<?> findByDocumento(@PathVariable String inciso, @PathVariable BigInteger id) {
        DocumentoReintegracao list = documentoReintegracaoRepository.buscarDocumentoReintegracao(inciso, id).get(0);
        return ResponseEntity.ok().body(list);
    }
}
