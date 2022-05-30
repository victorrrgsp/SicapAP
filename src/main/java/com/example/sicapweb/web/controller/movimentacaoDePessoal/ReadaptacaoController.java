package com.example.sicapweb.web.controller.movimentacaoDePessoal;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.pessoal.Readaptacao;

import java.math.BigInteger;

import com.example.sicapweb.repository.concessao.ReadaptacaoRepository;
import com.example.sicapweb.repository.geral.AtoRepository;
import com.example.sicapweb.repository.geral.CargoRepository;
import com.example.sicapweb.repository.movimentacaoDePessoal.AdmissaoRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping({"/movimentacaoDePessoal/readaptacao"})
    public class ReadaptacaoController extends DefaultController<Readaptacao> {

        @Autowired
        private ReadaptacaoRepository readaptacaoRepository;
        @Autowired
        private AtoRepository atoRepository;
        @Autowired
        private AdmissaoRepository admissaoRepository;
        @Autowired
        private CargoRepository cargoRepository;

        @CrossOrigin
        @Transactional
        @DeleteMapping(value = {"/{id}"})
        public ResponseEntity<?> delete(@PathVariable BigInteger id) {
            readaptacaoRepository.deleteRestrito(id);
            return ResponseEntity.noContent().build();
        }

        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<Readaptacao>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<Readaptacao> paginacaoUtil = readaptacaoRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }
        @CrossOrigin
        @Transactional
        @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
        public ResponseEntity<Readaptacao> update(@RequestBody Readaptacao readaptacao, @PathVariable BigInteger id) {
            InfoRemessa chave = readaptacaoRepository.findById(id).getChave();
            readaptacao.setChave(chave);
            readaptacao.setId(id);
            
            readaptacao.setAto(atoRepository.findById(readaptacao.getAto().getId()));
            readaptacao.setAdmissao(admissaoRepository.findById(readaptacao.getAdmissao().getId()));
            readaptacao.setCargo(cargoRepository.findById(readaptacao.getCargo().getId()));
            readaptacaoRepository.update(readaptacao);
            return ResponseEntity.noContent().build();
        }

    }




