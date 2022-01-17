package com.example.sicapweb.web.controller.folhaDePagamento;

import br.gov.to.tce.model.ap.folha.FolhaPagamento;
import br.gov.to.tce.model.ap.folha.RecolhimentoPrevidenciario;
import com.example.sicapweb.repository.folhaDePagamento.FolhaDePagamentoRepository;
import com.example.sicapweb.repository.folhaDePagamento.RecolhimentoPrevidenciarioRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


    @RestController
    @RequestMapping({"/folhaDePagamento/recolhimentoPrevidenciario"})
    public class RecolhimentoPrevidenciarioController {

        @Autowired
        private RecolhimentoPrevidenciarioRepository recolhimentoPrevidenciarioRepository;

        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<RecolhimentoPrevidenciario>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<RecolhimentoPrevidenciario> paginacaoUtil = recolhimentoPrevidenciarioRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }

    }

