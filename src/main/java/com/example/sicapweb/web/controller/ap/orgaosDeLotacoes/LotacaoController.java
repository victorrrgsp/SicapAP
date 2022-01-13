package com.example.sicapweb.web.controller.ap.orgaosDeLotacoes;

import br.gov.to.tce.model.ap.relacional.Lotacao;
import com.example.sicapweb.repository.orgaosDeLotacoes.LotacaoRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping({"/orgaosDeLotacoes/lotacao"})
    public class LotacaoController {

        @Autowired
        private LotacaoRepository lotacaoRepository;

        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<Lotacao>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<Lotacao> paginacaoUtil = lotacaoRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }

    }


