package com.example.sicapweb.web.controller.movimentacaoDePessoal;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.pessoal.Reintegracao;

import java.math.BigInteger;

import javax.transaction.Transactional;

import com.example.sicapweb.repository.concessao.ReintegracaoRepository;
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
    @RequestMapping({"/movimentacaoDePessoal/reintegracao"})
    public class ReintegracaoController extends DefaultController<Reintegracao> {

        @Autowired
        private ReintegracaoRepository reintegracaoRepository;
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
            reintegracaoRepository.deleteRestrito(id); 
        }

        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<Reintegracao>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<Reintegracao> paginacaoUtil = reintegracaoRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }
        @CrossOrigin
        @Transactional
        @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
        public ResponseEntity<Reintegracao> update(@RequestBody Reintegracao reconducao, @PathVariable BigInteger id) {
            InfoRemessa chave = reintegracaoRepository.findById(id).getChave();
            
            reconducao.setChave(chave);
            
            reconducao.setId(id);
            reconducao.setAto(atoRepository.findById(reconducao.getAto().getId()));
            reconducao.setCargo(cargoRepository.findById(reconducao.getCargo().getId()));
            reconducao.setAdmissao(admissaoRepository.findById(reconducao.getAdmissao().getId()));
            reintegracaoRepository.update(reconducao);
            return ResponseEntity.noContent().build();
        }

    }


