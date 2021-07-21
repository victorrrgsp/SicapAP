package com.example.sicapweb.web.controller;

import com.example.sicapweb.repository.ReadaptacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/documentoConcessaoReadaptacao")
public class ConcessaoReadaptacaoController {

    @Autowired
    private ReadaptacaoRepository readaptacaoRepository;

    @GetMapping("/")
    public String lista(ModelMap model) {
        model.addAttribute("readaptacoes", readaptacaoRepository.findAll());
        return "documentoConcessaoReadaptacao";
    }
}
