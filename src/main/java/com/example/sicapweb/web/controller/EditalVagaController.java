package com.example.sicapweb.web.controller;

import com.example.sicapweb.service.EditalVagaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/concursoVaga")
public class EditalVagaController {

    @Autowired
    private EditalVagaService editalVagaService;

    @GetMapping("/")
    public String listaEditalVaga(ModelMap model) {
        model.addAttribute("vagas", editalVagaService.buscarTodos());
        return "concursoVaga";
    }
}
