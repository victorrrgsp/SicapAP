package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.concessoes.DocumentoReconducao;
import br.gov.to.tce.model.ap.pessoal.Reconducao;
import com.example.sicapweb.model.Inciso;
import com.example.sicapweb.repository.DocumentoReconducaoRepository;
import com.example.sicapweb.repository.ReconducaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.ArrayList;
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
    @GetMapping(path = {"getInciso/{id}"})
    public ResponseEntity<?> findInciso(@PathVariable BigInteger id) {
        List<Inciso> list = new ArrayList<>();
        list.add(new Inciso("I - Seção V", "Ofício subscrito pela autoridade competente",
                "Ofício subscrito pela autoridade competente dirigido ao Presidente do TCE/TO dando ciência do fato", "", "Sim"));
        list.add(new Inciso("II - Seção V", "Ato da concessão acompanhado da respectiva publicação",
                "Ato da concessão acompanhado da respectiva publicação", "", "Sim"));
        list.add(new Inciso("V - Seção V", "Declaração do órgão competente da existência de vaga no cargo em que se der a reintegração",
                "Declaração do órgão competente da existência de vaga no cargo em que se der a reintegração", "", "Sim"));
        list.add(new Inciso("VI - Seção V", "Parecer jurídico atestando a legalidade do ato",
                "Parecer jurídico atestando a legalidade do ato", "", "Sim"));
        list.add(new Inciso("IX - Seção V", "Cópia dos atos que declararam os resultados da avaliação de desempenho",
                "Cópia dos atos que declararam os resultados da avaliação de desempenho, nos casos de recondução por inabilitação em estágio probatório", "", "Não"));
        list.add(new Inciso("", "Outros",
                "Outros", "", "Não"));

        for (int i = 0; i < list.size(); i++){
            Integer existeArquivo = documentoReconducaoRepository.findAllInciso("documentoReconducao","idReconducao",id, list.get(i).getInciso());
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
        DocumentoReconducao list = documentoReconducaoRepository.buscarDocumentooReconducao(inciso, id).get(0);
        return ResponseEntity.ok().body(list);
    }
}
