package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.concessoes.DocumentoAproveitamento;
import br.gov.to.tce.model.ap.pessoal.Aproveitamento;
import com.example.sicapweb.repository.AproveitamentoRepository;
import com.example.sicapweb.repository.DocumentoAproveitamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/documentoConcessaoAproveitamento")
public class ConcessaoAproveitamentoController extends DefaultController<Aproveitamento> {

    @Autowired
    private AproveitamentoRepository aproveitamentoRepository;

    @Autowired
    private DocumentoAproveitamentoRepository documentoAproveitamentoRepository;

    @CrossOrigin
    @GetMapping()
    public ResponseEntity<List<Aproveitamento>> findAll() {
        List<Aproveitamento> list = aproveitamentoRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        Aproveitamento list = aproveitamentoRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping("/upload/{inciso}/{id}")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file, @PathVariable String inciso, @PathVariable BigInteger id) {
        DocumentoAproveitamento documentoAproveitamento = new DocumentoAproveitamento();
        documentoAproveitamento.setAproveitamento(aproveitamentoRepository.findById(id));
        documentoAproveitamento.setInciso(inciso);
        String idCastor = super.setCastorFile(file, "Aposentadoria");
        documentoAproveitamento.setIdCastorFile(idCastor);
        documentoAproveitamento.setStatus(DocumentoAproveitamento.Status.Informado.getValor());
        documentoAproveitamentoRepository.save(documentoAproveitamento);
        return ResponseEntity.ok().body(idCastor);
    }

    @CrossOrigin
    @GetMapping(path = {"anexos/{inciso}/{id}"})
    public ResponseEntity<?> findByDocumento(@PathVariable String inciso, @PathVariable BigInteger id) {
        DocumentoAproveitamento list = documentoAproveitamentoRepository.buscarDocumentoAproveitamento(inciso, id).get(0);
        return ResponseEntity.ok().body(list);
    }
}
