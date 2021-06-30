package com.example.sicapweb.web.controller;

import com.example.sicapweb.service.EditalAprovadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/concursoAprovado")
public class EditalAprovadoController {

    @Autowired
    private EditalAprovadoService editalAprovadoService;

    @GetMapping("/")
    public String listaEditalAprovado(ModelMap model) {
        model.addAttribute("aprovados", editalAprovadoService.buscarTodos());
        return "concursoAprovado";
    }
}
