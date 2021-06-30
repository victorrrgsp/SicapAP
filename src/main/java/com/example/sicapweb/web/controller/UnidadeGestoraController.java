package com.example.sicapweb.web.controller;

import com.example.sicapweb.service.UnidadeGestoraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/unidadeGestora")
public class UnidadeGestoraController {

    @Autowired
    private UnidadeGestoraService unidadeGestoraService;

    @GetMapping("/")
    public String listarunidadeGestora(ModelMap model) {
        model.addAttribute("unidades", unidadeGestoraService.buscarTodos());
        return "unidadeGestora";
    }


}
