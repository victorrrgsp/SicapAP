package com.example.sicapweb.web.controller;

import com.example.sicapweb.repository.AposentadoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/documentoConcessaoRevisaoAposentadoria")
public class ConcessaoRevisaoAposentadoriaController {

    @Autowired
    private AposentadoriaRepository aposentadoriaRepository;

    @GetMapping("/")
    public String lista(ModelMap model) {
        model.addAttribute("revap", aposentadoriaRepository.buscarAposentadoriaRevisao());
        return "documentoConcessaoRevisaoAposentadoria";
    }
}
