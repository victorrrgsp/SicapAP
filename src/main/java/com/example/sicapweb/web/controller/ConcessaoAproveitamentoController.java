package com.example.sicapweb.web.controller;

import com.example.sicapweb.repository.AproveitamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/documentoConcessaoAproveitamento")
public class ConcessaoAproveitamentoController {

    @Autowired
    private AproveitamentoRepository aproveitamentoRepository;

    @GetMapping("/")
    public String lista(ModelMap model) {
        model.addAttribute("aproveitamentos", aproveitamentoRepository.findAll());
        return "documentoConcessaoAproveitamento";
    }
}
