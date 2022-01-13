package com.example.sicapweb.web.controller.movimentacaoDePessoal;

import br.gov.to.tce.model.ap.pessoal.Pensao;
import br.gov.to.tce.model.ap.pessoal.Pensionista;
import com.example.sicapweb.repository.concessao.PensaoRepository;
import com.example.sicapweb.repository.movimentacaoDePessoal.PensionistaRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


    @RestController
    @RequestMapping({"/movimentacaoDePessoal/pensionista"})
    public class PensionistaController {

        @Autowired
        private PensionistaRepository pensionistaRepository;

        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<Pensionista>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<Pensionista> paginacaoUtil = pensionistaRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }

    }
