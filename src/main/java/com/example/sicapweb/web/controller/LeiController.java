package com.example.sicapweb.web.controller;

import com.example.sicapweb.repository.LeiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cadastrarArquivoLei")
public class LeiController {

    @Autowired
    private LeiRepository leiRepository;

    @GetMapping("/")
    public String lista(ModelMap model) {
        model.addAttribute("leis", leiRepository.findAll());
        return "cadastrarArquivoLei";
    }
}
