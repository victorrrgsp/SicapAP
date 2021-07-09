package com.example.sicapweb.web.controller;

import com.example.sicapweb.repository.AposentadoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/documentoConcessaoReversaoAposentadoriaReserva")
public class ConcessaoReversaoAposentadoriaReservaController {

    @Autowired
    private AposentadoriaRepository aposentadoriaRepository;

    @GetMapping("/")
    public String lista(ModelMap model) {
        model.addAttribute("revapres", aposentadoriaRepository.buscarReversaoAposentadoriaReserva());
        return "documentoConcessaoReversaoAposentadoriaReserva";
    }
}
