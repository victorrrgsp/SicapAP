package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.InfoRemessa;
import com.example.sicapweb.repository.AssinarRemessaRepository;
import com.example.sicapweb.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
}
