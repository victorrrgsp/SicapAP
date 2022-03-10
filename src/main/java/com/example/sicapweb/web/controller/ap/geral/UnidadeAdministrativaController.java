package com.example.sicapweb.web.controller.ap.geral;

import br.gov.to.tce.model.ap.relacional.UnidadeAdministrativa;
import com.example.sicapweb.repository.geral.UnidadeAdministrativaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @CrossOrigin
    @GetMapping("/pesquisaPorRemessa/{mes}/{ano}")
    public ResponseEntity<List<Object[]>> findByRemessa(@PathVariable("mes") int mes,@PathVariable("ano") int ano) {
        List<Object[]> list = unidadeAdministrativaRepository.buscarremessa(ano,mes);
        return ResponseEntity.ok().body(list);
    }
}
