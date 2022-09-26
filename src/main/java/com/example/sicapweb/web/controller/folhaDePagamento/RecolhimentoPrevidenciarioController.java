package com.example.sicapweb.web.controller.folhaDePagamento;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.folha.RecolhimentoPrevidenciario;
import com.example.sicapweb.repository.folhaDePagamento.RecolhimentoPrevidenciarioRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;


@RestController
    @RequestMapping({"/folhaDePagamento/recolhimentoPrevidenciario"})
    public class RecolhimentoPrevidenciarioController extends DefaultController<RecolhimentoPrevidenciario> {
        @Autowired
        private RecolhimentoPrevidenciarioRepository recolhimentoPrevidenciarioRepository;

        @CrossOrigin
        @Transactional
        @DeleteMapping(value = {"/{id}"})
        public void delete(@PathVariable BigInteger id) {
            recolhimentoPrevidenciarioRepository.deleteRestrito(id); 
        }
        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<RecolhimentoPrevidenciario>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<RecolhimentoPrevidenciario> paginacaoUtil = recolhimentoPrevidenciarioRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }
        @CrossOrigin
        @Transactional
        @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
        public void update(@RequestBody RecolhimentoPrevidenciario recolhimentoPrevidenciario, @PathVariable BigInteger id) {

            InfoRemessa chave = recolhimentoPrevidenciarioRepository.findById(id).getChave();
            recolhimentoPrevidenciario.setChave(chave);
            recolhimentoPrevidenciario.setId(id);
            recolhimentoPrevidenciarioRepository.update(recolhimentoPrevidenciario);
        }

    }

