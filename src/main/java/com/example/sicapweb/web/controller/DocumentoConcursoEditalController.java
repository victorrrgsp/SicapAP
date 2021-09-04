package com.example.sicapweb.web.controller;


import br.gov.to.tce.model.ap.concurso.documento.DocumentoEdital;
import br.gov.to.tce.model.ap.concurso.Edital;
import com.example.sicapweb.model.Inciso;
import com.example.sicapweb.repository.DocumentoEditalRepository;
import com.example.sicapweb.repository.EditalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/documentoConcursoEdital")
public class DocumentoConcursoEditalController extends DefaultController<Edital>{

    @Autowired
    private EditalRepository editalRepository;

    @Autowired
    private DocumentoEditalRepository documentoEditalRepository;

    @GetMapping("/")
    public String lista(ModelMap model) {
        model.addAttribute("editais", editalRepository.findAll());
        return "documentoConcursoEdital";
    }

    @CrossOrigin
    @Transactional
    @PostMapping("/upload/{inciso}/{id}")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file, @PathVariable String inciso, @PathVariable BigInteger id) {
        DocumentoEdital documentoEdital = new DocumentoEdital();
        documentoEdital.setEdital(editalRepository.findById(id));
        documentoEdital.setInciso(inciso);
        String idCastor = super.setCastorFile(file, "Edital");
        documentoEdital.setIdCastorFile(idCastor);
        documentoEdital.setStatus(DocumentoEdital.Status.Informado.getValor());
        documentoEditalRepository.save(documentoEdital);
        return ResponseEntity.ok().body(idCastor);
    }

    @CrossOrigin
    @GetMapping(path = {"getInciso/{id}"})
    public ResponseEntity<?> findInciso(@PathVariable BigInteger id) {
        List<Inciso> list = new ArrayList<>();
        list.add(new Inciso("I", "Ofício subscrito pela autoridade competente",
                "Ofício subscrito pela autoridade competente", "", "Sim"));
        list.add(new Inciso("II ", "Justificativa para a abertura do concurso público",
                "Justificativa para a abertura do concurso público", "", "Sim"));
        list.add(new Inciso("III", "Demonstrativo de despesa do impacto orçamentário-financeiro",
                "Demonstrativo de despesa do impacto orçamentário-financeiro", "", "Sim"));
        list.add(new Inciso("IV", "Declaração de despesa da autorização para realização do consurso público",
                "Declaração de despesa da autorização para realização do consurso público", "", "Sim"));
        list.add(new Inciso("V", "Demonstrativo informando o percentual da despesa total com pessoal",
                "Demonstrativo informando o percentual da despesa total com pessoal", "", "Sim"));
        list.add(new Inciso("VI", "Ato expedido pela autoridade competente designando a comissão examinadora/julgadora",
                "Ato expedido pela autoridade competente designando a comissão examinadora/julgadora", "", "Sim"));
        list.add(new Inciso("VII", "Demonstrativo do quadro de pessoal efetivo e quantidade de vagas criadas por lei",
                "Demonstrativo do quadro de pessoal efetivo e quantidade de vagas criadas por lei", "", "Sim"));
        list.add(new Inciso("VIII", "Lei(s) de criação e/ou alteração dos cargos disponibilizados no Edital",
                "Lei(s) de criação e/ou alteração dos cargos disponibilizados no Edital", "", "Sim"));
        list.add(new Inciso("IX", "Documentos da contratação da entidade pomotora do certame",
                "Documentos da contratação da entidade pomotora do certame", "", "Sim"));
        list.add(new Inciso("IX.I", "Comprovante de cadastramento no SICAP-LCO",
                "Comprovante de cadastramento no SICAP-LCO", "", "Sim"));
        list.add(new Inciso("X", "Edital de abertura do consurso público e o respectivo comprovante de publicação",
                "Edital de abertura do consurso público e o respectivo comprovante de publicação", "", "Sim"));
        list.add(new Inciso("XI", "Demais editais do consurso público, quando houver",
                "Demais editais do consurso público, quando houver", "", "Não"));
        list.add(new Inciso("", "Outros",
                "Outros", "", "Não"));

        for (int i = 0; i < list.size(); i++){
            Integer existeArquivo = documentoEditalRepository.findAllInciso("documentoEdital","idEdital",id, list.get(i).getInciso());
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
        DocumentoEdital list = documentoEditalRepository.buscarDocumentoEdital(inciso, id).get(0);
        return ResponseEntity.ok().body(list);
    }
}
