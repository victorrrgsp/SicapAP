package com.example.sicapweb.web.controller.movimentacaoDePessoal;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.pessoal.Pensionista;

import java.math.BigInteger;

import com.example.sicapweb.repository.geral.AtoRepository;
import com.example.sicapweb.repository.movimentacaoDePessoal.AdmissaoRepository;
import com.example.sicapweb.repository.movimentacaoDePessoal.PensionistaRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


    @RestController
    @RequestMapping({"/movimentacaoDePessoal/pensionista"})
    public class PensionistaController extends DefaultController<Pensionista> {

        @Autowired
        private PensionistaRepository pensionistaRepository;
        @Autowired
        private AtoRepository atoRepository;    
        @Autowired
        private AdmissaoRepository admissaoRepository;

        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<Pensionista>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<Pensionista> paginacaoUtil = pensionistaRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }

        @CrossOrigin
        @Transactional
        @DeleteMapping(value = {"/{id}"})
        public void delete(@PathVariable BigInteger id) {
            pensionistaRepository.deleteRestrito(id); 
        }

        @CrossOrigin
        @Transactional
        @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
        public ResponseEntity<Pensionista> update(@RequestBody Pensionista pensionista, @PathVariable BigInteger id) {
            InfoRemessa chave = pensionistaRepository.findById(id).getChave();
            pensionista.setChave(chave);
            pensionista.setId(id);
            pensionista.setAto(atoRepository.findById(pensionista.getAto().getId()));
            
            pensionista.setAdmissao(admissaoRepository.findById(pensionista.getAdmissao().getId()));
            pensionistaRepository.update(pensionista);
            return ResponseEntity.noContent().build();
        }
    }
