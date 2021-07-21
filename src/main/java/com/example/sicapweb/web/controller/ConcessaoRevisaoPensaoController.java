package com.example.sicapweb.web.controller;

import com.example.sicapweb.repository.PensaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/documentoConcessaoRevisaoPensao")
public class ConcessaoRevisaoPensaoController {

    @Autowired
    private PensaoRepository pensaoRepository;

    @GetMapping("/")
    public String lista(ModelMap model) {
        model.addAttribute("revpen", pensaoRepository.buscarPensaoRevisao());
        return "documentoConcessaoRevisaoPensao";
    }
}
