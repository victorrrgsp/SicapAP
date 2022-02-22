package com.example.sicapweb.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.io.Closeable;

@RestController
@RequestMapping("/index")
public class IndexController {

    private static final Logger log = Logger.getLogger(IndexController.class.getName());

    @GetMapping("/")
    public String index() {
        return "index";
    }

    //############################################ API REINICIAR SEVER #####################################################
    @GetMapping("/script")
    public void reiniciaSicapAP(final String command) throws IOException {
        this.executeCommand("ls ~");
    }
    public void executeCommand(final String command) throws IOException {

        final ArrayList<String> commands = new ArrayList<String>();
        commands.add("#!/bin/bash");
        commands.add("ps -ef | grep -v grep | grep sicapweb-0.0.1-SNAPSHOT");
        commands.add("echo 'Matando sicapapweb'");
        commands.add("pkill -f sicapweb-0.0.1-SNAPSHOT.jar");
        commands.add("sleep 5");
        commands.add("echo 'Iniciando o sicapapweb'");
        commands.add("/scripts/SicapApWeb.sh");
        commands.add(command);

        BufferedReader br = null;

        try {
            final ProcessBuilder p = new ProcessBuilder(commands);
            final Process process = p.start();
            final InputStream is = process.getInputStream();
            final InputStreamReader isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            String line;
            while((line = br.readLine()) != null) {
                System.out.println("Retorno do comando = [" + line + "]");
            }
        } catch (IOException ioe) {
            log.severe("Erro ao executar comando shell" + ioe.getMessage());
            throw ioe;
        } finally {
            secureClose(br);
        }
    }



    private void secureClose(final Closeable resource) {
        try {
            if (resource != null) {
                resource.close();
            }
        } catch (IOException ex) {
            log.severe("Erro = " + ex.getMessage());
        }
    }

}
