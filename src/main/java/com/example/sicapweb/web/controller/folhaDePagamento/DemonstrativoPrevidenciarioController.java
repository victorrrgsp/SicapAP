package com.example.sicapweb.web.controller.folhaDePagamento;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.folha.DemonstrativoPrevidenciario;
import com.example.sicapweb.repository.folhaDePagamento.DemonstrativoPrevidenciarioRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@RestController
    @RequestMapping({"/folhaDePagamento/demonstrativoPrevidenciario"})
    public class DemonstrativoPrevidenciarioController extends DefaultController<DemonstrativoPrevidenciario> {

        @Autowired
        private DemonstrativoPrevidenciarioRepository demonstrativoPrevidenciarioRepository;

        @CrossOrigin
        @Transactional
        @DeleteMapping(value = {"/{id}"})
        public ResponseEntity<?> delete(@PathVariable BigInteger id) {
            demonstrativoPrevidenciarioRepository.deleteRestrito(id);
            return ResponseEntity.noContent().build();
        }
        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<DemonstrativoPrevidenciario>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<DemonstrativoPrevidenciario> paginacaoUtil = demonstrativoPrevidenciarioRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }
        @CrossOrigin
        @Transactional
        @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
        public ResponseEntity<DemonstrativoPrevidenciario> update(@RequestBody DemonstrativoPrevidenciario demonstrativoPrevidenciario, @PathVariable BigInteger id) {

            InfoRemessa chave = demonstrativoPrevidenciarioRepository.findById(id).getChave();
            demonstrativoPrevidenciario.setChave(chave);
            demonstrativoPrevidenciario.setId(id);
            demonstrativoPrevidenciarioRepository.update(demonstrativoPrevidenciario);
            return ResponseEntity.noContent().build();
        }

    }
