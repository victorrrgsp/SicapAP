package com.example.sicapweb.web.controller.folhaDePagamento;

import br.gov.to.tce.model.ap.folha.DemonstrativoPrevidenciario;
import com.example.sicapweb.repository.folhaDePagamento.DemonstrativoPrevidenciarioRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping({"/folhaDePagamento/demonstrativoPrevidenciario"})
    public class DemonstrativoPrevidenciarioController {

        @Autowired
        private DemonstrativoPrevidenciarioRepository demonstrativoPrevidenciarioRepository;

        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<DemonstrativoPrevidenciario>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<DemonstrativoPrevidenciario> paginacaoUtil = demonstrativoPrevidenciarioRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }

    }
