package com.example.sicapweb.web.controller;

import com.example.sicapweb.repository.EditalHomologacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/concursoHomologacao")
public class EditalHomologacaoController {

    @Autowired
    private EditalHomologacaoRepository editalHomologacaoRepository;

    @GetMapping("/")
    public String listaEditalHomologacao(ModelMap model) {
        model.addAttribute("homologacoes", editalHomologacaoRepository.findAll());
        return "concursoHomologacao";
    }
}
