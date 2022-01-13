package com.example.sicapweb.web.controller.ap.geral;

import br.gov.to.tce.model.ap.relacional.Lei;
import br.gov.to.tce.model.ap.relacional.UnidadeAdministrativa;
import com.example.sicapweb.repository.geral.UnidadeAdministrativaRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/unidadeAdministrativa"})
public class UnidadeAdministrativaController {

    @Autowired
    private UnidadeAdministrativaRepository unidadeAdministrativaRepository;

    @CrossOrigin
    @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<UnidadeAdministrativa>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<UnidadeAdministrativa> paginacaoUtil = unidadeAdministrativaRepository.buscaPaginada(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }


    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<UnidadeAdministrativa>> findAll() {
        List<UnidadeAdministrativa> list = unidadeAdministrativaRepository.findAll();
        return ResponseEntity.ok().body(list);
    }
}
