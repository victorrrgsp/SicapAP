package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.concurso.EmpresaOrganizadora;
import com.example.sicapweb.repository.EmpresaOrganizadoraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/concursoEmpresaOrganizadora")
public class EmpresaOrganizadoraController {

    @Autowired
    private EmpresaOrganizadoraRepository empresaOrganizadoraRepository;

    @GetMapping("/")
    public String lista(ModelMap model, EmpresaOrganizadora empresaOrganizadora) {
        model.addAttribute("empresas", empresaOrganizadoraRepository.findAll());
        return "concursoEmpresaOrganizadora";
    }

    @Transactional
    @PostMapping("salvar")
    public String salvar(EmpresaOrganizadora empresaOrganizadora) {
        empresaOrganizadoraRepository.save(empresaOrganizadora);
        return "redirect:/concursoEmpresaOrganizadora/";
    }
}
