package com.example.sicapweb.web.controller;
import br.gov.to.tce.model.adm.AdmAutenticacao;

import com.example.sicapweb.repository.ChavesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value="/chaves")

public class ChavesController {

    @Autowired
    private ChavesRepository chavesRepository;

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<AdmAutenticacao>> findAll() {
        List<AdmAutenticacao> list = chavesRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

   // @ApiOperation(value="Salva uma Chave de Autorizacao para envio do Sicap AP")
   @CrossOrigin
   @PostMapping
   public ResponseEntity<AdmAutenticacao> create(@RequestBody  AdmAutenticacao autenticacao) {
       chavesRepository.save(autenticacao);
       URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(autenticacao.getId()).toUri();
       return ResponseEntity.created(uri).body(autenticacao);
    }

//@ApiOperation(value="Retorna uma chave")
//    @GetMapping("/listar/{id}")
//    public AdmAutenticacao listaProdutoUnco(@PathVariable(value="id") long id){
//        return chavesRepository.findById(id);
//    }

}
