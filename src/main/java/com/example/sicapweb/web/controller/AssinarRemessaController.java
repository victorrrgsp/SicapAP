package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.InfoRemessa;
import com.example.sicapweb.repository.AssinarRemessaRepository;
import com.example.sicapweb.security.User;
import okhttp3.RequestBody;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping(value = "/assinarRemessa")
public class AssinarRemessaController {

    @Autowired
    private AssinarRemessaRepository assinarRemessaRepository;

    @CrossOrigin
    @GetMapping(path = {"/{cargo}"})
    public ResponseEntity<String> findResponsavel(@PathVariable String cargo) {
        String resp = assinarRemessaRepository.buscarResponsavelAssinatura(cargo);
        return ResponseEntity.ok().body(resp);
    }

    @CrossOrigin
    @GetMapping
    public ResponseEntity<InfoRemessa> findRemessaOpen() {
        InfoRemessa info = assinarRemessaRepository.buscarRemessaAberta();
        return ResponseEntity.ok().body(info);
    }

    @CrossOrigin
    @GetMapping(path = {"/autenticacao"})
    public ResponseEntity<User> findeUserAutenticacao() {
        User user = User.getUser();
        return ResponseEntity.ok().body(user);
    }

    @CrossOrigin
    @Transactional
    @PostMapping(path = {"/inicializarAssinatura"})
    public ResponseEntity<String> inicializarAssinatura(@org.springframework.web.bind.annotation.RequestBody String postdata) throws IOException {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
        RequestBody body = RequestBody.create(mediaType, postdata);
        Request request = new Request.Builder()
                .url("https://dev2.tce.to.gov.br/assinador/app/controllers/?&c=TCE_Assinador_AssinadorWeb&m=inicializarAssinatura")
                .method("POST", body)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Referer", "https://app.tce.to.gov.br/")
                .build();

        Response response = client.newCall(request).execute();
        String resposta = response.body().string();

        return ResponseEntity.ok().body(resposta);
    }

    @CrossOrigin
    @Transactional
    @PostMapping(path = {"/assinadorWebfinalizarAssinatura"})
    public ResponseEntity<String> assinadorWebfinalizarAssinatura(@org.springframework.web.bind.annotation.RequestBody String postdata) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
        RequestBody body = RequestBody.create(mediaType, postdata);
        Request request = new Request.Builder()
                .url("https://dev2.tce.to.gov.br/assinador/app/controllers/?&c=TCE_Assinador_AssinadorWeb&m=finalizarAssinatura")
                .method("POST", body)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Referer", "https://app.tce.to.gov.br/")
                .build();

        Response response = client.newCall(request).execute();
        String resposta = response.body().string();

        return ResponseEntity.ok().body(resposta);

    }

    @CrossOrigin
    @Transactional
    @PostMapping(path = {"/assinadorSicapApFinalizaAssinatura"})
    public ResponseEntity<String> assinadorSicapApFinalizaAssinatura(@org.springframework.web.bind.annotation.RequestBody String postdata) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
        RequestBody body = RequestBody.create(mediaType, postdata + "&cpf="+User.getUser().getCpf()+"&ug="+User.getUser().getUnidadeGestora().getId());
        Request request = new Request.Builder()
                .url("https://dev2.tce.to.gov.br/sicapap/app/controllers/?&c=TCE_SicapAp_ApiProcessoEletronico&m=finalizaAssinatura")
                .method("POST", body)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Referer", "https://app.tce.to.gov.br/")
                .build();
        Response response = client.newCall(request).execute();
        String resposta = response.body().string();
        return ResponseEntity.ok().body(resposta);
    }

}

