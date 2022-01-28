package com.example.sicapweb.web.controller.movimentacaoDePessoal;

import br.gov.to.tce.model.ap.pessoal.Aproveitamento;
import br.gov.to.tce.model.ap.pessoal.Disponibilidade;
import com.example.sicapweb.repository.concessao.AproveitamentoRepository;
import com.example.sicapweb.repository.movimentacaoDePessoal.DisponibilidadeRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping({"/movimentacaoDePessoal/aproveitamento"})
    public class AproveitamentoController extends DefaultController<Aproveitamento> {

        @Autowired
        private AproveitamentoRepository aproveitamentoRepository;

        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<Aproveitamento>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<Aproveitamento> paginacaoUtil = aproveitamentoRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }

    }




