package com.example.sicapweb.web.controller.folhaDePagamento;

import br.gov.to.tce.model.ap.folha.FolhaPagamento;
import com.example.sicapweb.repository.folhaDePagamento.FolhaDePagamentoRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping({"/folhaDePagamento/folhaDePagamento"})
    public class FolhaDePagamentoController extends DefaultController<FolhaPagamento> {

        @Autowired
        private FolhaDePagamentoRepository folhaDePagamentoRepository;

        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<FolhaPagamento>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<FolhaPagamento> paginacaoUtil = folhaDePagamentoRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }

    }
