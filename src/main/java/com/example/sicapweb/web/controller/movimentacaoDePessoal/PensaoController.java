package com.example.sicapweb.web.controller.movimentacaoDePessoal;

import br.gov.to.tce.model.ap.pessoal.Aposentadoria;
import br.gov.to.tce.model.ap.pessoal.Cessao;
import br.gov.to.tce.model.ap.pessoal.Pensao;
import com.example.sicapweb.repository.concessao.AposentadoriaRepository;
import com.example.sicapweb.repository.concessao.PensaoRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


    @RestController
    @RequestMapping({"/movimentacaoDePessoal/pensao"})
    public class PensaoController extends DefaultController<Pensao> {

        @Autowired
        private PensaoRepository pensaoRepository;

        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<Pensao>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<Pensao> paginacaoUtil = pensaoRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }

    }

