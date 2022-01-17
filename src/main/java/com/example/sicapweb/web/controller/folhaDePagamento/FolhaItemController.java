package com.example.sicapweb.web.controller.folhaDePagamento;


import br.gov.to.tce.model.ap.relacional.FolhaItem;
import com.example.sicapweb.repository.folhaDePagamento.FolhaItemRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping({"/folhaDePagamento/folhaItem"})
    public class FolhaItemController {

        @Autowired
        private FolhaItemRepository folhaItemRepository;

        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<FolhaItem>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<FolhaItem> paginacaoUtil = folhaItemRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }

    }
