package com.example.sicapweb.web.controller;

import com.example.sicapweb.service.EmpresaOrganizadoraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/concursoEmpresaOrganizadora")
public class EmpresaOrganizadoraController {

    @Autowired
    private EmpresaOrganizadoraService empresaOrganizadoraService;

    @GetMapping("/")
    public String listaEmpresaOrganizadora(ModelMap model) {
        model.addAttribute("empresas", empresaOrganizadoraService.buscarTodos());
        return "concursoEmpresaOrganizadora";
    }
}
