package com.example.sicapweb.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/avisos")
public class AvisosController {

    @GetMapping("/")
    public String avisos() {
        return "avisos";
    }
}
