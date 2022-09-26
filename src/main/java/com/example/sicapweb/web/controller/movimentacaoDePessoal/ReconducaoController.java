package com.example.sicapweb.web.controller.movimentacaoDePessoal;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.pessoal.Reconducao;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

import com.example.sicapweb.repository.concessao.ReconducaoRepository;
import com.example.sicapweb.repository.geral.AtoRepository;
import com.example.sicapweb.repository.geral.CargoRepository;
import com.example.sicapweb.repository.movimentacaoDePessoal.AdmissaoRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping({"/movimentacaoDePessoal/reconducao"})
    public class ReconducaoController extends DefaultController<Reconducao> {

        @Autowired
        private ReconducaoRepository reconducaoRepository;
        @Autowired
        private AtoRepository atoRepository;
        @Autowired
        private CargoRepository cargoRepository;
        @Autowired
        private AdmissaoRepository admissaoRepository;


        @CrossOrigin
        @Transactional
        @DeleteMapping(value = {"/{id}"})
        public void delete(@PathVariable BigInteger id) {
            reconducaoRepository.deleteRestrito(id); 
        }
        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<Reconducao>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<Reconducao> paginacaoUtil = reconducaoRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }
        
        @CrossOrigin
        @GetMapping(path = {"/{id}"})
        public ResponseEntity<?> findById(@PathVariable BigInteger id) {
            Reconducao reconducao = reconducaoRepository.findById(id);
            return ResponseEntity.ok().body(reconducao);
        }

        @CrossOrigin
        @Transactional
        @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
        public void update(@RequestBody Reconducao reconducao, @PathVariable BigInteger id) {
            InfoRemessa chave = reconducaoRepository.findById(id).getChave();
            
            reconducao.setChave(chave);
            
            reconducao.setId(id);
            reconducao.setAto(atoRepository.findById(reconducao.getAto().getId()));
            reconducao.setCargo(cargoRepository.findById(reconducao.getCargo().getId()));
            reconducao.setAdmissao(admissaoRepository.findById(reconducao.getAdmissao().getId()));
            reconducaoRepository.update(reconducao);
        }

    }


