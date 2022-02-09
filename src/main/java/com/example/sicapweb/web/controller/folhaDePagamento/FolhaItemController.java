package com.example.sicapweb.web.controller.folhaDePagamento;


import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.relacional.FolhaItem;

import java.math.BigInteger;
import java.util.List;

import com.example.sicapweb.repository.folhaDePagamento.FolhaItemRepository;
import com.example.sicapweb.repository.folhaDePagamento.FolhaItemEsocialRepository ;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
        @Transactional
        @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
        public ResponseEntity<FolhaItem> update(@RequestBody FolhaItem folhaItem, @PathVariable BigInteger id) {

            InfoRemessa chave = folhaItemRepository.findById(id).getChave();
            folhaItem.setChave(chave);
            folhaItem.setId(id);
            folhaItem.setFolhaItemESocial(folhaItemESocialRepository.findByCodigo(folhaItem.getCodigoFolhaItemESocial()));
            folhaItemRepository.update(folhaItem);
            return ResponseEntity.noContent().build();
        }
        @CrossOrigin
        @GetMapping(path="/all")
        public ResponseEntity<List<FolhaItem>> folhaItem() {
            List<FolhaItem> list = folhaItemRepository.findAll();
            return ResponseEntity.ok().body(list);
        }
        @CrossOrigin
        @Transactional
        @DeleteMapping(value = {"/{id}"})
        public ResponseEntity<?> delete(@PathVariable BigInteger id) {
            folhaItemRepository.deleteRestrito(id);
            return ResponseEntity.noContent().build();
        }

    }
