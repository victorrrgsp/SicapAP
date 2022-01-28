package com.example.sicapweb.web.controller.movimentacaoDePessoal;

import br.gov.to.tce.model.ap.pessoal.Cessao;
import br.gov.to.tce.model.ap.pessoal.Readaptacao;
import br.gov.to.tce.model.ap.pessoal.Reconducao;
import com.example.sicapweb.repository.concessao.ReadaptacaoRepository;
import com.example.sicapweb.repository.concessao.ReconducaoRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping({"/movimentacaoDePessoal/reconducao"})
    public class ReconducaoController extends DefaultController<Reconducao> {

        @Autowired
        private ReconducaoRepository reconducaoRepository;

        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<Reconducao>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<Reconducao> paginacaoUtil = reconducaoRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }

    }


