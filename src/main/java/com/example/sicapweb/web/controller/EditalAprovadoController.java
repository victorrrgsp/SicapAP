package com.example.sicapweb.web.controller;

import com.example.sicapweb.repository.EditalAprovadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/concursoAprovado")
public class EditalAprovadoController {

    @Autowired
    private EditalAprovadoRepository editalAprovadoRepository;

    @GetMapping("/")
    public String listaEditalAprovado(ModelMap model) {
        model.addAttribute("aprovados", editalAprovadoRepository.findAll());
        return "concursoAprovado";
    }
}
