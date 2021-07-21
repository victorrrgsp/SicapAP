package com.example.sicapweb.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cadastrarGfip")
public class GfipController {

    @GetMapping("/")
    public String gfip() {
        return "cadastrarGfip";
    }
}
