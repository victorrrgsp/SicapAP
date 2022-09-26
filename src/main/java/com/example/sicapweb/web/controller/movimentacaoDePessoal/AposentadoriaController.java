package com.example.sicapweb.web.controller.movimentacaoDePessoal;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.pessoal.Aposentadoria;

import java.math.BigInteger;

import com.example.sicapweb.repository.concessao.AposentadoriaRepository;
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
    @RequestMapping({"/movimentacaoDePessoal/aposentadoria"})
    public class AposentadoriaController extends DefaultController<Aposentadoria> {

        @Autowired
        private AposentadoriaRepository aposentadoriaRepository;
        @Autowired
        private AtoRepository atoRepository;    
        @Autowired
        private AdmissaoRepository admissaoRepository;
        @CrossOrigin
        @Transactional
        @DeleteMapping(value = {"/{id}"})
        public void delete(@PathVariable BigInteger id) {
            aposentadoriaRepository.deleteRestrito(id); 
        }
        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<Aposentadoria>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<Aposentadoria> paginacaoUtil = aposentadoriaRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }
    
        @CrossOrigin
        @Transactional
        @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
        public void update(@RequestBody Aposentadoria aposentadoria, @PathVariable BigInteger id) {
            InfoRemessa chave = aposentadoriaRepository.findById(id).getChave();
            // InfoRemessa chave = aposentadoria.findById(id).getAdmissao().getChave();
            aposentadoria.setChave(chave);
            //aposentadoria.setNumeroAto(aposentadoria.getNumeroAto().replace("/", ""));
            aposentadoria.setId(id);
            aposentadoria.setAto(atoRepository.findById(aposentadoria.getAto().getId()));
            
            if (aposentadoria.getAtoReversao() !=null)
                aposentadoria.setAtoReversao(atoRepository.findById(aposentadoria.getAtoReversao().getId()));
            
                aposentadoria.setAdmissao(admissaoRepository.findById(aposentadoria.getAdmissao().getId()));
            aposentadoriaRepository.update(aposentadoria);

        }

    }

