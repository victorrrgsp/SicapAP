package com.example.sicapweb.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/index")
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    //############################################ API REINICIAR SEVER #####################################################
    @GetMapping("/script")
    public void executeCommand() {
        String[] env = {"PATH=/bin:/usr/local/bin/"};

        String cmd = "reiniciarSicapWeb.sh";  //e.g test.sh -dparam1 -oout.txt
        //tratamento de erro e execução do script

        try {
            System.out.println(env);

            Process process = Runtime.getRuntime().exec(cmd, env);
            System.out.println("tste:"+process);

        } catch (IOException ex) {
            System.out.println(ex);
            //  Logger.getLogger(TecMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
