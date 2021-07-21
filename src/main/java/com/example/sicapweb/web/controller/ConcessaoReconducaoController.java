package com.example.sicapweb.web.controller;

import com.example.sicapweb.repository.ReconducaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/documentoConcessaoReconducao")
public class ConcessaoReconducaoController {

    @Autowired
    private ReconducaoRepository reconducaoRepository;

    @GetMapping("/")
    public String lista(ModelMap model) {
        model.addAttribute("reconducoes", reconducaoRepository.findAll());
        return "documentoConcessaoReconducao";
    }
}
