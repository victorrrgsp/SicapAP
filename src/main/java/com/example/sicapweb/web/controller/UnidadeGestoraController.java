package com.example.sicapweb.web.controller;

import com.example.sicapweb.repository.UnidadeGestoraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/unidadeGestora")
public class UnidadeGestoraController {

    @Autowired
    private UnidadeGestoraRepository unidadeGestoraRepository;

    @GetMapping("/")
    public String lista(ModelMap model) {
        model.addAttribute("unidades", unidadeGestoraRepository.findAll());
        return "unidadeGestora";
    }


}
