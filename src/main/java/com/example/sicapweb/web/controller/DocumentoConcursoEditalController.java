package com.example.sicapweb.web.controller;

import com.example.sicapweb.repository.EditalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/documentoConcursoEdital")
public class DocumentoConcursoEditalController {

    @Autowired
    private EditalRepository editalRepository;

    @GetMapping("/")
    public String lista(ModelMap model) {
        model.addAttribute("editais", editalRepository.findAll());
        return "documentoConcursoEdital";
    }
}
