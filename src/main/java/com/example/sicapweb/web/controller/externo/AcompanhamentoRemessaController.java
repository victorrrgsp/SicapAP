package com.example.sicapweb.web.controller.externo;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.relacional.Cargo;
import com.example.sicapweb.repository.externo.AcompanhamentoRemessaRepository;
import com.example.sicapweb.repository.remessa.GfipRepository;
import com.example.sicapweb.repository.remessa.InfoRemessaRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(value = "/externo/acompanhamentoRemessa")
public class AcompanhamentoRemessaController {


    @Autowired
    private AcompanhamentoRemessaRepository acompanhamentoRemessaRepository;

    @Autowired
    private GfipRepository gfipRepository;

    @Autowired
    private InfoRemessaRepository infoRemessaRepository;


    @CrossOrigin
    @GetMapping(path = {"/all"})
    public ResponseEntity<?> findTodos() {
        List<Map<String, Object>> infoRemessa = acompanhamentoRemessaRepository.buscarRemessaFechada(2021,1);
        return ResponseEntity.ok().body(Objects.requireNonNullElse(infoRemessa, "semRemessa"));
    }


    @CrossOrigin
    @GetMapping(path = {"/getRemessa/{exercicio}/{remessa}"})
    public ResponseEntity<?> findByRemessa(@PathVariable Integer exercicio, @PathVariable Integer remessa) {

      //  System.out.println(exercicio+"remessa: "+remessa);

        List<Map<String, Object>> infoRemessa = acompanhamentoRemessaRepository.buscarRemessaFechada(exercicio, remessa);
        return ResponseEntity.ok().body(Objects.requireNonNullElse(infoRemessa, "semRemessa"));
    }








}
