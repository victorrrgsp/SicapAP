package com.example.sicapweb.web.controller.movimentacaoDePessoal;

import br.gov.to.tce.model.ap.pessoal.Cessao;
import br.gov.to.tce.model.ap.pessoal.Disponibilidade;
import com.example.sicapweb.repository.movimentacaoDePessoal.CessaoRepository;
import com.example.sicapweb.repository.movimentacaoDePessoal.DisponibilidadeRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


    @RestController
    @RequestMapping({"/movimentacaoDePessoal/disponibilidade"})
    public class DisponibilidadeController extends DefaultController<Disponibilidade> {

        @Autowired
        private DisponibilidadeRepository disponibilidadeRepository;

        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<Disponibilidade>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<Disponibilidade> paginacaoUtil = disponibilidadeRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }

    }



