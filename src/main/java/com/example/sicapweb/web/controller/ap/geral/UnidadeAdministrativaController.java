package com.example.sicapweb.web.controller.ap.geral;

import br.gov.to.tce.model.ap.relacional.UnidadeAdministrativa;
import com.example.sicapweb.repository.UnidadeAdministrativaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/unidadeAdministrativa"})
public class UnidadeAdministrativaController {

    @Autowired
    private UnidadeAdministrativaRepository unidadeAdministrativaRepository;

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<UnidadeAdministrativa>> findAll() {
        List<UnidadeAdministrativa> list = unidadeAdministrativaRepository.findAll();
        return ResponseEntity.ok().body(list);
    }
}
