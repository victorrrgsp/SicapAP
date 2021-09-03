package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.concessoes.DocumentoPensao;
import br.gov.to.tce.model.ap.pessoal.Pensao;
import com.example.sicapweb.model.Inciso;
import com.example.sicapweb.repository.DocumentoPensaoRepository;
import com.example.sicapweb.repository.PensaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/documentoConcessaoRevisaoPensao")
public class ConcessaoRevisaoPensaoController extends DefaultController<Pensao>{

    @Autowired
    private PensaoRepository pensaoRepository;


    @Autowired
    private DocumentoPensaoRepository documentoPensaoRepository;

    @CrossOrigin
    @GetMapping()
    @Override
    public ResponseEntity<List<Pensao>> findAll() {
        List<Pensao> list = pensaoRepository.buscarPensaoRevisao();
        return ResponseEntity.ok().body(list);
    }


//
//    @CrossOrigin
//    @GetMapping(path = {"/{id}"})
//    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
//        Pensao list = pensaoRepository.findById(id);
//        return ResponseEntity.ok().body(list);
//    }

    @CrossOrigin
    @Transactional
    @PostMapping("/upload/{inciso}/{id}")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file, @PathVariable String inciso, @PathVariable BigInteger id) {
        DocumentoPensao documentoPensao = new DocumentoPensao();
        documentoPensao.setPensao(pensaoRepository.findById(id));
        documentoPensao.setInciso(inciso);
        String idCastor = super.setCastorFile(file, "PensaoRevisao");
        documentoPensao.setIdCastorFile(idCastor);
        documentoPensao.setStatus(DocumentoPensao.Status.Informado.getValor());
        documentoPensao.setRevisao("S");
        documentoPensaoRepository.save(documentoPensao);
        return ResponseEntity.ok().body(idCastor);
    }


    @CrossOrigin
    @GetMapping(path = {"getInciso/{id}"})
    public ResponseEntity<?> findInciso(@PathVariable BigInteger id) {
        List<Inciso> list = new ArrayList<>();
        list.add(new Inciso("I", "Ofício da autoridade competente",
                "Ofício da autoridade competente", "", "Sim"));
        list.add(new Inciso("II", "Requerimento de aposentadoria",
                "Requerimento de aposentadoria", "", "Sim"));
        list.add(new Inciso("III", "Certidão comprobatória de preenchimento de requisitos",
                "Certidão comprobatória de preenchimento de requisitos para a percepção dos proventos e/ou espécies remuneratórias previstos na revisão pretendida", "", "Sim"));
        list.add(new Inciso("IV", "Demonstrativo de cálculo da revisão dos proventos",
                "Demonstrativo de cálculo da revisão dos proventos", "", "Sim"));
        list.add(new Inciso("V", "Parecer jurídico atestando a legalidade da concessão do benefício",
                "Parecer jurídico atestando a legalidade da concessão do benefício", "", "Sim"));
        list.add(new Inciso("VI", "Ato de concessão da revisão de proventos",
                "Ato de concessão da revisão de proventos constando o documento revisado, o nome do servidor e a fundamentação legal, acompanhado da respectiva publicação", "", "Sim"));
        list.add(new Inciso("", "Outros",
                "Outros", "", "Sim"));

        for (int i = 0; i < list.size(); i++){
            Integer existeArquivo = documentoPensaoRepository.findAllInciso("documentoPensao","idPensao",id, list.get(i).getInciso());
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
        DocumentoPensao list = documentoPensaoRepository.buscarDocumentoPensaoRevisao(inciso, id).get(0);
        return ResponseEntity.ok().body(list);
    }
}
