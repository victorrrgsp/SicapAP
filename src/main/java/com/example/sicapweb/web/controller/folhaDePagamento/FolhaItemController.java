package com.example.sicapweb.web.controller.folhaDePagamento;


import br.gov.to.tce.model.ap.relacional.FolhaItem;
import br.gov.to.tce.model.ap.estatico.FolhaItemESocial;

import java.util.List;

import com.example.sicapweb.repository.folhaDePagamento.FolhaItemRepository;
import com.example.sicapweb.repository.folhaDePagamento.FolhaItemEsocialRepository ;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping({"/folhaDePagamento/folhaItem"})
    public class FolhaItemController extends DefaultController<FolhaItem> {

        @Autowired
        private FolhaItemRepository folhaItemRepository;
        @Autowired
        private FolhaItemEsocialRepository folhaItemESocialRepository;

        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<FolhaItem>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<FolhaItem> paginacaoUtil = folhaItemRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }

        @CrossOrigin
        @GetMapping(path="/FolhaItemESocial")
        public ResponseEntity<List<FolhaItemESocial>> folhaItemESocial() {
            List<FolhaItemESocial> list = folhaItemESocialRepository.findAll();
            return ResponseEntity.ok().body(list);
        }
        @CrossOrigin
        @GetMapping(path="/all")
        public ResponseEntity<List<FolhaItem>> folhaItem() {
            List<FolhaItem> list = folhaItemRepository.findAll();
            return ResponseEntity.ok().body(list);
        }

    }
