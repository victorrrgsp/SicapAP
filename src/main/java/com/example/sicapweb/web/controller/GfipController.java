package com.example.sicapweb.web.controller;


import br.gov.to.tce.model.InfoRemessa;
import com.example.sicapweb.repository.InfoRemessaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/cadastrarGfip")
public class GfipController {

    @Autowired
    InfoRemessaRepository infoRemessaRepository;

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<InfoRemessa>> findAll(){
        List<InfoRemessa> list = infoRemessaRepository.findAll();
        return ResponseEntity.ok().body(list);
    }
//
//    @CrossOrigin
//    @GetMapping(path = {"/{id}"})
//    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
//        InfoRemessa list = infoRemessaRepository.buscar(id);
//        return ResponseEntity.ok().body(list);
//    }

}
