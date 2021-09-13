package com.example.sicapweb.web.controller;


import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.folha.documento.Gfip;
import com.example.sicapweb.repository.GfipRepository;
import com.example.sicapweb.repository.InfoRemessaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/cadastrarGfip")
public class GfipController extends DefaultController<InfoRemessa>{

    @Autowired
    InfoRemessaRepository infoRemessaRepository;

    @Autowired
    private GfipRepository gfipRepository;

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<InfoRemessa>> findAll(){
        List<InfoRemessa> list = infoRemessaRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/find/{chave}"})
    public ResponseEntity<InfoRemessa> findById(@PathVariable String chave){
        InfoRemessa list = infoRemessaRepository.findById(chave);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping("/upload/{chave}/{tipo}")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file, @PathVariable String chave, @PathVariable String tipo) {
        Gfip gfip = new Gfip();
        Date hoje = new Date();
        gfip.setInfoRemessa(infoRemessaRepository.findById(chave));
        String idCastor = super.setCastorFile(file, tipo);
        gfip.setIdCastorFile(idCastor);
        gfip.setTipo(tipo);
        gfip.setData(hoje);
        gfipRepository.save(gfip);
        return ResponseEntity.ok().body(idCastor);
    }
    @CrossOrigin
    @GetMapping(path = {"anexos/{chave}/{tipo}"})
    public ResponseEntity<?> findByDocumento(@PathVariable String chave, @PathVariable String tipo) {
        Gfip list = gfipRepository.buscarDocumentoGfip(chave, tipo).get(0);
        return ResponseEntity.ok().body(list);
    }


}
