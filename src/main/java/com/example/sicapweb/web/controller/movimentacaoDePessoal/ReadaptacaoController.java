package com.example.sicapweb.web.controller.movimentacaoDePessoal;

import br.gov.to.tce.model.ap.pessoal.Readaptacao;
import com.example.sicapweb.repository.concessao.ReadaptacaoRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping({"/movimentacaoDePessoal/readaptacao"})
    public class ReadaptacaoController {

        @Autowired
        private ReadaptacaoRepository readaptacaoRepository;

        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<Readaptacao>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<Readaptacao> paginacaoUtil = readaptacaoRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }

    }




