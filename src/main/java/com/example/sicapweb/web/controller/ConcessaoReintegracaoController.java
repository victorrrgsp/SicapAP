package com.example.sicapweb.web.controller;

import com.example.sicapweb.repository.ReintegracaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/documentoConcessaoReintegracao")
public class ConcessaoReintegracaoController {

    @Autowired
    private ReintegracaoRepository reintegracaoRepository;

    @GetMapping("/")
    public String lista(ModelMap model) {
        model.addAttribute("reintegracoes", reintegracaoRepository.findAll());
        return "documentoConcessaoReintegracao";
    }
}
