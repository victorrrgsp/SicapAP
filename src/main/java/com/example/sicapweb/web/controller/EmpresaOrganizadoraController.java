package com.example.sicapweb.web.controller;

import com.example.sicapweb.repository.EmpresaOrganizadoraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/concursoEmpresaOrganizadora")
public class EmpresaOrganizadoraController {

    @Autowired
    private EmpresaOrganizadoraRepository empresaOrganizadoraRepository;

    @GetMapping("/")
    public String listaEmpresaOrganizadora(ModelMap model) {
        model.addAttribute("empresas", empresaOrganizadoraRepository.findAll());
        return "concursoEmpresaOrganizadora";
    }
}
