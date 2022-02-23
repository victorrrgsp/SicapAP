package com.example.sicapweb.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@RequestMapping("/script")
public class ServerController {

    @GetMapping("/sicapap")
    public void reiniciaSicapAP() throws IOException, InterruptedException {

        String comando = "sh /usr/local/bin/reiniciarSicapWeb.sh";
        Runtime.getRuntime().exec(comando).waitFor();   //espera até que a tarefa esteja complera
        Runtime.getRuntime().exec(comando).destroy();

        String comando2 = "sh /usr/local/bin/SicapApWeb.sh";
        Runtime.getRuntime().exec(comando2).waitFor();   //espera até que a tarefa esteja complera
        Runtime.getRuntime().exec(comando2).destroy();
    }


    @GetMapping("/sicapapanalisador")
    public void reiniciaSicapApAnalisador() throws IOException, InterruptedException {

        String comando = "sh /usr/local/bin/SicapApImportador.sh";
        Runtime.getRuntime().exec(comando).waitFor();   //espera até que a tarefa esteja complera
        Runtime.getRuntime().exec(comando).destroy();
    }



    @GetMapping("/sicapestadoanalisador")
    public void reiniciaSicapEstado() throws IOException, InterruptedException {

        String comando = "sh /usr/local/bin/SicapEstadoImportador.sh";
        Runtime.getRuntime().exec(comando).waitFor();   //espera até que a tarefa esteja complera
        Runtime.getRuntime().exec(comando).destroy();
    }

}
