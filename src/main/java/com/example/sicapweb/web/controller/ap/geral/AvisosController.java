package com.example.sicapweb.web.controller.ap.geral;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/avisos")
public class AvisosController {

    @GetMapping("/")
    public String avisos() {
        return "avisos";
    }
}
