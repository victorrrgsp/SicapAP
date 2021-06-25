package com.example.sicapweb.web.controller;

import com.example.sicapweb.dao.UnidadeGestoraDao;
import com.example.sicapweb.service.UnidadeGestoraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/unidadeGestora")
@RestController
public class UnidadeGestoraController {

    private UnidadeGestoraDao dao;

    @Autowired
    public UnidadeGestoraController(UnidadeGestoraDao dao) {
        this.dao = dao;
    }

    @Autowired
    private UnidadeGestoraService unidadeGestoraService;

    @GetMapping("/")
    public String listarunidadeGestora(ModelMap model) {
        model.addAttribute("unidades", unidadeGestoraService.buscarTodos());
        return "unidadeGestora";
    }


}
