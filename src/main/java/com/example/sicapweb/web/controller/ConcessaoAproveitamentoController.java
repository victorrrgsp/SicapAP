package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.concurso.Edital;
import br.gov.to.tce.model.ap.pessoal.Aproveitamento;
import com.example.sicapweb.repository.AproveitamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/documentoConcessaoAproveitamento")
public class ConcessaoAproveitamentoController {

    @Autowired
    private AproveitamentoRepository aproveitamentoRepository;

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
}
