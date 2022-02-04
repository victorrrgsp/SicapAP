package com.example.sicapweb.web.controller.movimentacaoDePessoal;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.pessoal.Aproveitamento;
import br.gov.to.tce.model.ap.pessoal.Disponibilidade;

import java.math.BigInteger;

import com.example.sicapweb.repository.concessao.AproveitamentoRepository;
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
    @RequestMapping({"/movimentacaoDePessoal/aproveitamento"})
    public class AproveitamentoController extends DefaultController<Aproveitamento> {

        @Autowired
        private AproveitamentoRepository aproveitamentoRepository;
        @Autowired
        private AtoRepository atoRepository;   
        @Autowired
        private CargoRepository cargoRepository; 
        @Autowired
        private AdmissaoRepository admissaoRepository;

        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<Aproveitamento>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<Aproveitamento> paginacaoUtil = aproveitamentoRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }
        @CrossOrigin
        @Transactional
        @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
        public ResponseEntity<Aproveitamento> update(@RequestBody Aproveitamento aproveitamento, @PathVariable BigInteger id) {

            InfoRemessa chave = aproveitamentoRepository.findById(id).getChave();
            aproveitamento.setChave(chave);
            aproveitamento.setId(id);

            aproveitamento.setAto(atoRepository.findById(aproveitamento.getAto().getId()));
            aproveitamento.setAdmissao(admissaoRepository.findById(aproveitamento.getAdmissao().getId()));
            aproveitamento.setCargo(cargoRepository.findById(aproveitamento.getCargo().getId()));

            aproveitamentoRepository.update(aproveitamento);
            return ResponseEntity.noContent().build();
        }

    }




