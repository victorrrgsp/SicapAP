package com.example.sicapweb.web.controller.ap.orgaosDeLotacoes;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.relacional.Lotacao;
import com.example.sicapweb.repository.geral.UnidadeAdministrativaRepository;
import com.example.sicapweb.repository.orgaosDeLotacoes.LotacaoRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@RestController
    @RequestMapping({"/orgaosDeLotacoes/lotacao"})
    public class LotacaoController extends DefaultController<Lotacao> {

        @Autowired
        private LotacaoRepository lotacaoRepository;
        @Autowired
        private UnidadeAdministrativaRepository unidadeAdministrativaRepository;

        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<Lotacao>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            return ResponseEntity.ok().body(lotacaoRepository.buscaPaginada(pageable,searchParams,tipoParams));
        }

        @CrossOrigin
        @GetMapping(path = {"/{id}"})
        public ResponseEntity<?> findById(@PathVariable BigInteger id) {
            return ResponseEntity.ok().body(lotacaoRepository.findById(id));
        }

        @CrossOrigin
        @Transactional
        @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
        public void update(@RequestBody Lotacao lotacao, @PathVariable BigInteger id) {
            InfoRemessa chave = lotacaoRepository.findById(id).getChave();
            lotacao.setUnidadeAdministrativa(unidadeAdministrativaRepository.buscarUnidadePorcodigo(lotacao.codigoUnidadeAdministrativa));
            lotacao.setId(id);
            lotacao.setChave(chave);
            lotacaoRepository.update(lotacao);
        }
        @CrossOrigin
        @Transactional
        @DeleteMapping(value = {"/{id}"})
        public void delete(@PathVariable BigInteger id) {
            lotacaoRepository.deleteRestrito(id); 
        }

    }


