package com.example.sicapweb.web.controller.movimentacaoDePessoal;

import br.gov.to.tce.model.ap.pessoal.Cessao;
import br.gov.to.tce.model.ap.pessoal.DesignacaoFuncao;
import com.example.sicapweb.repository.movimentacaoDePessoal.DesignacaoFuncaoRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


    @RestController
    @RequestMapping({"/movimentacaoDePessoal/designacaoFuncao"})
    public class DesignacaoFuncaoController extends DefaultController<DesignacaoFuncao> {

        @Autowired
        private DesignacaoFuncaoRepository designacaoFuncaoRepository;

        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<DesignacaoFuncao>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<DesignacaoFuncao> paginacaoUtil = designacaoFuncaoRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }

    }


