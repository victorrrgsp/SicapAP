package com.example.sicapweb.web.controller;

import com.example.sicapweb.service.EditalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/concursoEdital")
public class EditalController {

    @Autowired
    private EditalService editalService;

    @GetMapping("/")
    public String listaEdital(ModelMap model) {
        model.addAttribute("editais", editalService.buscarTodos());
        return "concursoEdital";
    }
}
