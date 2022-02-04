package com.example.sicapweb.web.controller.movimentacaoDePessoal;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.pessoal.Cessao;
import br.gov.to.tce.model.ap.pessoal.Desligamento;
import br.gov.to.tce.model.ap.pessoal.Licenca;


import java.math.BigInteger;

import com.example.sicapweb.repository.geral.AtoRepository;
import com.example.sicapweb.repository.movimentacaoDePessoal.AdmissaoRepository;
import com.example.sicapweb.repository.movimentacaoDePessoal.DesligamentoRepository;
import com.example.sicapweb.repository.movimentacaoDePessoal.LicencaRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping({"/movimentacaoDePessoal/licenca"})
    public class LicencaController extends DefaultController<Licenca> {

        @Autowired
        private LicencaRepository licencaRepository;
        @Autowired
        private AtoRepository atoRepository;    

        @Autowired
        private AdmissaoRepository admissaoRepository;
        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<Licenca>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<Licenca> paginacaoUtil = licencaRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }

        @CrossOrigin
        @Transactional
        @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
        public ResponseEntity<Licenca> update(@RequestBody Licenca licenca, @PathVariable BigInteger id) {

            InfoRemessa chave = licencaRepository.findById(id).getChave();
            licenca.setChave(chave);
            licenca.setId(id);
            licenca.setAto(atoRepository.findById(licenca.getAto().getId()));
            
            licenca.setAdmissao(admissaoRepository.findById(licenca.getAdmissao().getId()));
            licencaRepository.update(licenca);
            return ResponseEntity.noContent().build();
        }
    }

