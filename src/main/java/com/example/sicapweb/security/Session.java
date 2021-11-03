package com.example.sicapweb.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import java.io.Serializable;

public class Session implements Serializable {

    public static User usuarioLogado = null;

    @Bean
    @Scope(
            value = WebApplicationContext.SCOPE_SESSION,
            proxyMode = ScopedProxyMode.TARGET_CLASS)
    public static void setUsuario(User userLogado) {
        usuarioLogado = userLogado;
    }
}
