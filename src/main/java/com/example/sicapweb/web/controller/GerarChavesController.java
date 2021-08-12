package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.adm.AdmAutenticacao;
import com.example.sicapweb.repository.ChavesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(value="/chaves")

public class GerarChavesController {

    @Autowired
    ChavesRepository chavesRepository;

   // @ApiOperation(value="Salva uma Chave de Autorizacao para envio do Sicap AP")
    @PostMapping("/salvar")
    public AdmAutenticacao salvar(@RequestBody  @Valid AdmAutenticacao autenticacao) {
        return chavesRepository.save(autenticacao);
    }

    //@ApiOperation(value="Retorna uma chave")
    @GetMapping("/listar/{id}")
    public AdmAutenticacao listaProdutoUnco(@PathVariable(value="id") long id){
        return chavesRepository.findById(id);
    }

    //@ApiOperation(value="Retorna uma lista de chaves")
    @GetMapping("/listar")
    public List<AdmAutenticacao> listaProdutos(){
        return chavesRepository.findAll();
    }


}
