package com.example.sicapweb.web.controller.ap.geral;

import br.gov.to.tce.model.UnidadeGestora;
import com.example.sicapweb.repository.geral.UnidadeGestoraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/avisos")
public class AvisosController {

    @Autowired
    UnidadeGestoraRepository unidadeGestoraRepository;

    @GetMapping("/")
    public String avisos(ModelMap model) {
        UnidadeGestora unidadeGestora = unidadeGestoraRepository.buscarDadosUnidadeTeste();
        model.addAttribute("unidade", unidadeGestora);
        return "avisos";
    }
}
