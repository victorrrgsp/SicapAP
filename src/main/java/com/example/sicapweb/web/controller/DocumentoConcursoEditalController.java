package com.example.sicapweb.web.controller;


import br.gov.to.tce.model.ap.concurso.documento.DocumentoEdital;
import br.gov.to.tce.model.ap.concurso.Edital;
import com.example.sicapweb.repository.DocumentoEditalRepository;
import com.example.sicapweb.repository.EditalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;

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
    @GetMapping(path = {"anexos/{inciso}/{id}"})
    public ResponseEntity<?> findByDocumento(@PathVariable String inciso, @PathVariable BigInteger id) {
        DocumentoEdital list = documentoEditalRepository.buscarDocumentoEdital(inciso, id).get(0);
        return ResponseEntity.ok().body(list);
    }
}
