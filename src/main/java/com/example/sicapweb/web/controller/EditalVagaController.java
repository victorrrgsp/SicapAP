package com.example.sicapweb.web.controller;

import com.example.sicapweb.repository.EditalVagaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/concursoVaga")
public class EditalVagaController {

    @Autowired
    private EditalVagaRepository editalVagaRepository;

    @GetMapping("/")
    public String listaEditalVaga(ModelMap model) {
        model.addAttribute("vagas", editalVagaRepository.findAll());
        return "concursoVaga";
    }
}
