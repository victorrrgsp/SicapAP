package com.example.sicapweb.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class HashMessenger {

    private String text;

    private String Texthashed;

    public HashMessenger(String text) {
        try {
            this.text = text;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] byteOfTextToHash = text.getBytes(StandardCharsets.UTF_8);
            byte[] hashedByetArray = digest.digest(byteOfTextToHash);
            this.Texthashed = Base64.getEncoder().encodeToString(hashedByetArray);
        } catch (Exception e ){
            e.printStackTrace();
        }
    }

    public String getText() {
        return text;
    }

    public String getTexthashed() {
        return Texthashed;
    }
}
