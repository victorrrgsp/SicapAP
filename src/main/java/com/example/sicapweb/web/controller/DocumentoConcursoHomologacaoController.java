package com.example.sicapweb.web.controller;


import br.gov.to.tce.model.ap.concurso.EditalHomologacao;
import br.gov.to.tce.model.ap.concurso.documento.DocumentoEditalHomologacao;
import com.example.sicapweb.repository.DocumentoEditalHomologacaoRepository;
import com.example.sicapweb.repository.EditalHomologacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/documentoConcursoHomologacao")
public class DocumentoConcursoHomologacaoController extends DefaultController<EditalHomologacao> {

    @Autowired
    private EditalHomologacaoRepository editalHomologacaoRepository;

    @Autowired
    private DocumentoEditalHomologacaoRepository documentoEditalHomologacaoRepository;

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<EditalHomologacao>> findAll() {
        List<EditalHomologacao> list = editalHomologacaoRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        EditalHomologacao list = editalHomologacaoRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping("/upload/{inciso}/{id}")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file, @PathVariable String inciso, @PathVariable BigInteger id) {
        DocumentoEditalHomologacao documentoEditalHomologacao = new DocumentoEditalHomologacao();
        documentoEditalHomologacao.setEditalHomologacao(editalHomologacaoRepository.findById(id));
        documentoEditalHomologacao.setInciso(inciso);
        String idCastor = super.setCastorFile(file, "EditalHomologacao");
        documentoEditalHomologacao.setIdCastorFile(idCastor);
        documentoEditalHomologacao.setStatus(DocumentoEditalHomologacao.Status.Informado.getValor());
        documentoEditalHomologacaoRepository.save(documentoEditalHomologacao);
        return ResponseEntity.ok().body(idCastor);
    }

    @CrossOrigin
    @GetMapping(path = {"anexos/{inciso}/{id}"})
    public ResponseEntity<?> findByDocumento(@PathVariable String inciso, @PathVariable BigInteger id) {
        DocumentoEditalHomologacao list = documentoEditalHomologacaoRepository.buscarDocumentoEditalHomologacao(inciso, id).get(0);
        return ResponseEntity.ok().body(list);
    }
}
