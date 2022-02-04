package com.example.sicapweb.web.controller.movimentacaoDePessoal;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.pessoal.Admissao;
import br.gov.to.tce.model.ap.pessoal.Cessao;
import br.gov.to.tce.model.ap.pessoal.Desligamento;
import br.gov.to.tce.model.ap.relacional.Ato;

import com.example.sicapweb.repository.geral.AtoRepository;
import com.example.sicapweb.repository.movimentacaoDePessoal.AdmissaoRepository;
import com.example.sicapweb.repository.movimentacaoDePessoal.DesligamentoRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@RestController
    @RequestMapping({"/movimentacaoDePessoal/desligamento"})
    public class DesligamentoController extends DefaultController<Desligamento> {

    @Autowired
    private DesligamentoRepository desligamentoRepository;
    @Autowired
    private AtoRepository atoRepository;
    @Autowired
    private AdmissaoRepository admissaoRepository;

    @CrossOrigin
    @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<Desligamento>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<Desligamento> paginacaoUtil = desligamentoRepository.buscaPaginada(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }
    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        Desligamento list = desligamentoRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }
    @CrossOrigin
    @Transactional
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
    public ResponseEntity<Desligamento> update(@RequestBody Desligamento desligamento, @PathVariable BigInteger id) {
        InfoRemessa chave = desligamentoRepository.findById(id).getChave();
       // InfoRemessa chave = desligamentoRepository.findById(id).getAdmissao().getChave();
        desligamento.setChave(chave);
        
        //desligamento.setNumeroAto(desligamento.getNumeroAto().replace("/", ""));
        desligamento.setId(id);
        desligamento.setAto(atoRepository.findById(desligamento.getAto().getId()));
        desligamento.setAdmissao(admissaoRepository.findById(desligamento.getAdmissao().getId()));
        desligamentoRepository.update(desligamento);
        return ResponseEntity.noContent().build();
    }

    }


