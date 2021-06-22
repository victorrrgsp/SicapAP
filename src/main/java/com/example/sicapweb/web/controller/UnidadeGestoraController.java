package com.example.sicapweb.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/unidadeGestora")
public class UnidadeGestoraController {

    @GetMapping("/")
    public String unidadeGestora() {
        return "unidadeGestora";
    }
}
