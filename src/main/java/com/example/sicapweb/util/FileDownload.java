package com.example.sicapweb.util;

import br.gov.to.tce.castor.arquivo.ObjetoCastor;

import java.io.File;
import java.io.IOException;

public class FileDownload {
    public String name;
    public byte[] bytes;
    public String mime;

    public FileDownload(){

    }

    public FileDownload(String name, byte[] bytes, String mime){
        this.name = name;
        this.bytes = bytes;
        this.mime = mime;
    }

    public FileDownload(ObjetoCastor objeto) {
        this.name = objeto.getNomeArquivo();
        this.bytes = objeto.getBytes();
        try {
            this.mime = new File(objeto.getNomeArquivo()).toURL().openConnection().getContentType();
        } catch (IOException e) {
            e.printStackTrace();
            mime = "application/pdf";
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
