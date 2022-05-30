package com.example.sicapweb.web.controller.movimentacaoDePessoal;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.pessoal.DesignacaoFuncao;

import java.math.BigInteger;

import com.example.sicapweb.repository.geral.AtoRepository;
import com.example.sicapweb.repository.geral.CargoRepository;
import com.example.sicapweb.repository.movimentacaoDePessoal.AdmissaoRepository;
import com.example.sicapweb.repository.movimentacaoDePessoal.DesignacaoFuncaoRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


    @RestController
    @RequestMapping({"/movimentacaoDePessoal/designacaoFuncao"})
    public class DesignacaoFuncaoController extends DefaultController<DesignacaoFuncao> {

        @Autowired
        private DesignacaoFuncaoRepository designacaoFuncaoRepository;
        @Autowired
        private CargoRepository cargoRepository;
        @Autowired
        private AtoRepository atoRepository;    
        @Autowired
        private AdmissaoRepository admissaoRepository;

        @CrossOrigin
        @Transactional
        @DeleteMapping(value = {"/{id}"})
        public ResponseEntity<?> delete(@PathVariable BigInteger id) {
            designacaoFuncaoRepository.deleteRestrito(id);
            return ResponseEntity.noContent().build();
        }

        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<DesignacaoFuncao>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<DesignacaoFuncao> paginacaoUtil = designacaoFuncaoRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }
        @CrossOrigin
        @Transactional
        @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
        public ResponseEntity<DesignacaoFuncao> update(@RequestBody DesignacaoFuncao designacaoFuncao, @PathVariable BigInteger id) {

            InfoRemessa chave = designacaoFuncaoRepository.findById(id).getChave();
            designacaoFuncao.setChave(chave);
            designacaoFuncao.setId(id);

            designacaoFuncao.setAto(atoRepository.findById(designacaoFuncao.getAto().getId()));
            designacaoFuncao.setCargo(cargoRepository.findById(designacaoFuncao.getCargo().getId()));
            designacaoFuncao.setAdmissao(admissaoRepository.findById(designacaoFuncao.getAdmissao().getId()));
            
            designacaoFuncaoRepository.update(designacaoFuncao);
            return ResponseEntity.noContent().build();
        }

    }


