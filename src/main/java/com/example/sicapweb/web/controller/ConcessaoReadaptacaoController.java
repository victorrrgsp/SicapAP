package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.concessoes.DocumentoReadaptacao;
import br.gov.to.tce.model.ap.pessoal.Readaptacao;
import com.example.sicapweb.model.Inciso;
import com.example.sicapweb.repository.DocumentoReadaptacaoRepository;
import com.example.sicapweb.repository.ReadaptacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.ArrayList;
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
    @GetMapping(path = {"getInciso/{id}"})
    public ResponseEntity<?> findInciso(@PathVariable BigInteger id) {
        List<Inciso> list = new ArrayList<>();
        list.add(new Inciso("I - Seção V", "Ofício subscrito pela autoridade competente",
                "Ofício subscrito pela autoridade competente dirigido ao Presidente do TCE/TO dando ciência do fato", "", "Sim"));
        list.add(new Inciso("II - Seção V", "Ato da concessão acompanhado da respectiva publicação",
                "Ato da concessão acompanhado da respectiva publicação", "", "Sim"));
        list.add(new Inciso("VI - Seção V", "Parecer jurídico atestando a legalidade do ato",
                "Parecer jurídico atestando a legalidade do ato", "", "Não"));
        list.add(new Inciso("VI - Seção V", "Parecer jurídico atestando a legalidade do ato",
                "Parecer jurídico atestando a legalidade do ato", "", "Sim"));
        list.add(new Inciso("", "Outros",
                "Outros", "", "Não"));

        for (int i = 0; i < list.size(); i++){
            Integer existeArquivo = documentoReadaptacaoRepository.findAllInciso("documentoReadaptacao","idReadaptacao",id, list.get(i).getInciso());
            if (existeArquivo > 0){
                list.get(i).setStatus("Informado");
            }else{
                list.get(i).setStatus("Não informado");
            }
        }
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"anexos/{inciso}/{id}"})
    public ResponseEntity<?> findByDocumento(@PathVariable String inciso, @PathVariable BigInteger id) {
        DocumentoReadaptacao list = documentoReadaptacaoRepository.buscarDocumentoReadaptacao(inciso, id).get(0);
        return ResponseEntity.ok().body(list);
    }
}
