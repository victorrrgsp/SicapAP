package com.example.sicapweb.web.controller.movimentacaoDePessoal;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.pessoal.Cessao;
import br.gov.to.tce.model.ap.pessoal.Disponibilidade;

import java.math.BigInteger;

import com.example.sicapweb.repository.geral.AtoRepository;
import com.example.sicapweb.repository.movimentacaoDePessoal.AdmissaoRepository;
import com.example.sicapweb.repository.movimentacaoDePessoal.CessaoRepository;
import com.example.sicapweb.repository.movimentacaoDePessoal.DisponibilidadeRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


    @RestController
    @RequestMapping({"/movimentacaoDePessoal/disponibilidade"})
    public class DisponibilidadeController extends DefaultController<Disponibilidade> {

        @Autowired
        private DisponibilidadeRepository disponibilidadeRepository;
        @Autowired
        private AtoRepository atoRepository;    
        @Autowired
        private AdmissaoRepository admissaoRepository;

        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<Disponibilidade>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<Disponibilidade> paginacaoUtil = disponibilidadeRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }
        @CrossOrigin
        @Transactional
        @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
        public ResponseEntity<Disponibilidade> update(@RequestBody Disponibilidade disponibilidade, @PathVariable BigInteger id) {

            InfoRemessa chave = disponibilidadeRepository.findById(id).getChave();
            disponibilidade.setChave(chave);
            disponibilidade.setId(id);

            disponibilidade.setAto(atoRepository.findById(disponibilidade.getAto().getId()));
            disponibilidade.setAdmissao(admissaoRepository.findById(disponibilidade.getAdmissao().getId()));
            
            disponibilidadeRepository.update(disponibilidade);
            return ResponseEntity.noContent().build();
        }

    }



