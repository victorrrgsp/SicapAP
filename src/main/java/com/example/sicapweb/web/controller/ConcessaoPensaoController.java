package com.example.sicapweb.web.controller;

import com.example.sicapweb.repository.PensaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/documentoConcessaoPensao")
public class ConcessaoPensaoController {

    @Autowired
    private PensaoRepository pensaoRepository;

    @GetMapping("/")
    public String lista(ModelMap model) {
        model.addAttribute("pensoes", pensaoRepository.findAll());
        return "documentoConcessaoPensao";
    }
}
