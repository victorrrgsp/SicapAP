package com.example.sicapweb.web.controller;
import br.gov.to.tce.model.UnidadeGestora;
import com.example.sicapweb.repository.UnidadeGestoraRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping({"/unidadeGestora"})
public class UnidadeGestoraController {

    @Autowired
    private UnidadeGestoraRepository unidadeGestoraRepository;

    @CrossOrigin
    @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<UnidadeGestora>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<UnidadeGestora> paginacaoUtil = unidadeGestoraRepository.buscaPaginada(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<UnidadeGestora>> findAll() {
        List<UnidadeGestora> list = unidadeGestoraRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/{Cnpj}"})
    public ResponseEntity<?> findById(@PathVariable String Cnpj) {
        UnidadeGestora list = unidadeGestoraRepository.buscaUnidadeGestoraPorCnpj(Cnpj);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/vigencia/{Cnpj}/{Exercicio}/{Remessa}"})
    public ResponseEntity<List<Integer>>  buscaVigenciaUnidadeGestoraPorCnpj(@PathVariable String Cnpj, @PathVariable Integer Exercicio, @PathVariable Integer Remessa) {
        List<Integer> resposta = unidadeGestoraRepository.buscaVigenciaUnidadeGestoraPorCnpj(Cnpj, Exercicio, Remessa);
        return ResponseEntity.ok().body(resposta);
    }



}
