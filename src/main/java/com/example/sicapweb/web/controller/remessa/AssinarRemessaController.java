package com.example.sicapweb.web.controller.remessa;

import br.gov.to.tce.model.InfoRemessa;
import com.example.sicapweb.repository.remessa.AssinarRemessaRepository;
import com.example.sicapweb.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;


@RestController
@RequestMapping(value = "/assinarRemessa")
public class AssinarRemessaController {

    public InfoRemessa info;

    @Autowired
    private AssinarRemessaRepository assinarRemessaRepository;

    @CrossOrigin
    @GetMapping(path = {"/{cargo}"})
    public ResponseEntity<?> findResponsavel(@PathVariable String cargo) {
        Integer tipoCargo;
        switch (cargo) {
            case "Gestor":
                tipoCargo = User.Cargo.Gestor.getValor();
                break;
            case "Responsável R.H.":
                tipoCargo = User.Cargo.ResponsavelRH.getValor();
                break;
            case "Controle Interno":
                tipoCargo = User.Cargo.ControleInterno.getValor();
                break;
            default:
                tipoCargo = 0;
                break;
        }
        Object resp = assinarRemessaRepository.buscarResponsavelAssinatura(tipoCargo, info);
        return ResponseEntity.ok().body(Objects.requireNonNullElse(resp, "semPermissao"));
    }

    @CrossOrigin
    @GetMapping
    public ResponseEntity<?> findRemessaOpen() {
        info = assinarRemessaRepository.buscarRemessaAberta();
        return ResponseEntity.ok().body(Objects.requireNonNullElse(info, "semRemessa"));
    }

    @CrossOrigin
    @GetMapping(path = {"/autenticacao"})
    public ResponseEntity<User> findeUserAutenticacao() {
        User user = User.getUser();
        return ResponseEntity.ok().body(user);
    }

    @CrossOrigin
    @Transactional
    @GetMapping(path = {"/insertDados"})
    public ResponseEntity<?> insertDados() {
        assinarRemessaRepository.insertArquivo();
        assinarRemessaRepository.insertAssinatura();
        assinarRemessaRepository.insertInfoAssinatura(info);
        assinarRemessaRepository.insertAdmAssinatura(info.getChave());
        return ResponseEntity.ok().body("Ok");
    }
}

