package com.example.sicapweb.web.controller;


import br.gov.to.tce.model.ap.concessoes.DocumentoReintegracao;
import br.gov.to.tce.model.ap.pessoal.Reintegracao;
import com.example.sicapweb.model.Inciso;
import com.example.sicapweb.repository.DocumentoReintegracaoRepository;
import com.example.sicapweb.repository.ReintegracaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.ArrayList;
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
    @GetMapping(path = {"getInciso/{id}"})
    public ResponseEntity<?> findInciso(@PathVariable BigInteger id) {
        List<Inciso> list = new ArrayList<>();
        list.add(new Inciso("I - Seção V", "Ofício subscrito pela autoridade competente",
                "Ofício subscrito pela autoridade competente dirigido ao Presidente do TCE/TO dando ciência do fato", "", "Sim"));
        list.add(new Inciso("II - Seção V", "Ato da concessão acompanhado da respectiva publicação",
                "Ato da concessão acompanhado da respectiva publicação", "", "Sim"));
        list.add(new Inciso("III - Seção V", "Cópia autêntica da decisão judicial",
                "Cópia autêntica da decisão judicial, se dela decorrer a motivação, acompanhada da respectiva certidão de trânsito em julgado", "", "Não"));
        list.add(new Inciso("IV - Seção V", "Justificativa para a reintegração que se der em razão de processo administrativo",
                "Justificativa para a reintegração que se der em razão de processo administrativo", "", "Não"));
        list.add(new Inciso("V - Seção V", "Declaração do órgão competente da existência de vaga no cargo em que se der a reintegração",
                "Declaração do órgão competente da existência de vaga no cargo em que se der a reintegração", "", "Sim"));
        list.add(new Inciso("VI -  Seção V", "Parecer jurídico atestando a legalidade do ato",
                "Parecer jurídico atestando a legalidade do ato", "", "Sim"));
        list.add(new Inciso("", "Outros",
                "Outros", "", "Não"));

        for (int i = 0; i < list.size(); i++){
            Integer existeArquivo = documentoReintegracaoRepository.findAllInciso("documentoReintegracao","idReintegracao",id, list.get(i).getInciso());
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
        DocumentoReintegracao list = documentoReintegracaoRepository.buscarDocumentoReintegracao(inciso, id).get(0);
        return ResponseEntity.ok().body(list);
    }
}
