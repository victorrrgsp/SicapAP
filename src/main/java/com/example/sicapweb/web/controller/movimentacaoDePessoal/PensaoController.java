package com.example.sicapweb.web.controller.movimentacaoDePessoal;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.pessoal.Aposentadoria;
import br.gov.to.tce.model.ap.pessoal.Pensao;

import java.math.BigInteger;

import com.example.sicapweb.repository.concessao.PensaoRepository;
import com.example.sicapweb.repository.geral.AtoRepository;
import com.example.sicapweb.repository.movimentacaoDePessoal.AdmissaoRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


    @RestController
    @RequestMapping({"/movimentacaoDePessoal/pensao"})
    public class PensaoController extends DefaultController<Pensao> {

        @Autowired
        private PensaoRepository pensaoRepository;
        @Autowired
        private AtoRepository atoRepository;    
        @Autowired
        private AdmissaoRepository admissaoRepository;

        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<Pensao>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<Pensao> paginacaoUtil = pensaoRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }

        @CrossOrigin
        @Transactional
        @DeleteMapping(value = {"/{id}"})
        public ResponseEntity<?> delete(@PathVariable BigInteger id) {
            pensaoRepository.deleteRestrito(id);
            return ResponseEntity.noContent().build();
        }

        @CrossOrigin
        @Transactional
        @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
        public ResponseEntity<Aposentadoria> update(@RequestBody Pensao pensao, @PathVariable BigInteger id) {

            InfoRemessa chave = pensaoRepository.findById(id).getChave();
            // InfoRemessa chave = aposentadoria.findById(id).getAdmissao().getChave();
            pensao.setChave(chave);
            // aposentadoria.setNumeroAto(aposentadoria.getNumeroAto().replace("/", ""));
            pensao.setId(id);
            pensao.setAto(atoRepository.findById(pensao.getAto().getId()));
            
            pensao.setAdmissao(admissaoRepository.findById(pensao.getAdmissao().getId()));
            pensaoRepository.update(pensao);
            return ResponseEntity.noContent().build();

        }


    }

