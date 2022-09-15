package com.example.sicapweb.web.controller.movimentacaoDePessoal;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.pessoal.Cessao;

import java.math.BigInteger;

import com.example.sicapweb.repository.geral.AtoRepository;
import com.example.sicapweb.repository.movimentacaoDePessoal.AdmissaoRepository;
import com.example.sicapweb.repository.movimentacaoDePessoal.CessaoRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping({"/movimentacaoDePessoal/cessao"})
    public class CessaoController  extends DefaultController<Cessao> {

        @Autowired
        private CessaoRepository cessaoRepository;
        @Autowired
        private AtoRepository atoRepository;    
        @Autowired
        private AdmissaoRepository admissaoRepository;

        @CrossOrigin
        @Transactional
        @DeleteMapping(value = {"/{id}"})
        public void delete(@PathVariable BigInteger id) {
            cessaoRepository.deleteRestrito(id); 
        }

        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<Cessao>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<Cessao> paginacaoUtil = cessaoRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }
        @CrossOrigin
        @Transactional
        @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
        public ResponseEntity<Cessao> update(@RequestBody Cessao cessao, @PathVariable BigInteger id) {

            InfoRemessa chave = cessaoRepository.findById(id).getChave();
            cessao.setChave(chave);
            cessao.setId(id);

            cessao.setAto(atoRepository.findById(cessao.getAto().getId()));
            cessao.setAdmissao(admissaoRepository.findById(cessao.getAdmissao().getId()));
            
            cessaoRepository.update(cessao);
            return ResponseEntity.noContent().build();
        }

    }

