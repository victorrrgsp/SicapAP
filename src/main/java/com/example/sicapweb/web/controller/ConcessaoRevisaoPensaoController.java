package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.pessoal.Pensao;
import com.example.sicapweb.repository.PensaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/documentoConcessaoRevisaoPensao")
public class ConcessaoRevisaoPensaoController {

    @Autowired
    private PensaoRepository pensaoRepository;

    @CrossOrigin
    @GetMapping()
    public ResponseEntity<List<Pensao>> buscarPensoesRevisao() {
        List<Pensao> list = pensaoRepository.buscarPensaoRevisao();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        Pensao list = pensaoRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }
}
