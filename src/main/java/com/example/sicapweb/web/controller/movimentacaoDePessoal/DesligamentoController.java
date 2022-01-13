package com.example.sicapweb.web.controller.movimentacaoDePessoal;

import br.gov.to.tce.model.ap.pessoal.Admissao;
import br.gov.to.tce.model.ap.pessoal.Desligamento;
import com.example.sicapweb.repository.movimentacaoDePessoal.AdmissaoRepository;
import com.example.sicapweb.repository.movimentacaoDePessoal.DesligamentoRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping({"/movimentacaoDePessoal/desligamento"})
    public class DesligamentoController {

        @Autowired
        private DesligamentoRepository desligamentoRepository;

        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<Desligamento>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<Desligamento> paginacaoUtil = desligamentoRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }

    }


