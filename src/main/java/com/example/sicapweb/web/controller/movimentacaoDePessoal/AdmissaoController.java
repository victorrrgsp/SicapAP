package com.example.sicapweb.web.controller.movimentacaoDePessoal;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.pessoal.Admissao;

import com.example.sicapweb.repository.geral.AtoRepository;
import com.example.sicapweb.repository.geral.CargoRepository;
import com.example.sicapweb.repository.movimentacaoDePessoal.AdmissaoRepository;

import com.example.sicapweb.repository.orgaosDeLotacoes.LotacaoRepository;
import com.example.sicapweb.repository.servidores.ServidorRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

    @RestController
    @RequestMapping({"/movimentacaoDePessoal/admissao"})
    public class AdmissaoController extends DefaultController<Admissao> {

        @Autowired
        private AdmissaoRepository admissaoRepository;
        @Autowired
        private ServidorRepository servidorRepository;
        @Autowired
        private LotacaoRepository lotacaoRepository;
        @Autowired
        private CargoRepository cargoRepository;

        @Autowired
        private AtoRepository atoRepository;

        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<Admissao>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<Admissao> paginacaoUtil = admissaoRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }
        @CrossOrigin
        @GetMapping(path = {"/{id}"})
        public ResponseEntity<?> findById(@PathVariable BigInteger id) {
            Admissao list = admissaoRepository.findById(id);
            return ResponseEntity.ok().body(list);
        }
        @CrossOrigin
        @GetMapping(path = {"/all"})
        public ResponseEntity<?> find() {
            List<Admissao> list = admissaoRepository.findAll();
            return ResponseEntity.ok().body(list);
        }
        @CrossOrigin
        @Transactional
        @DeleteMapping(value = {"/{id}"})
        public void delete(@PathVariable BigInteger id) {
            admissaoRepository.deleteRestrito(id); 
        }
        @CrossOrigin
        @Transactional
        @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
        public void update(@RequestBody Admissao admissao, @PathVariable BigInteger id) {
            admissao.setId(id);
            InfoRemessa chave = admissaoRepository.findById(id).getChave();
            if(admissao.getCpfServidor() != null) {
                admissao.setCpfServidor(admissao.getCpfServidor().replace(".", "").replace("-", "").replace("/", ""));
                admissao.setServidor(servidorRepository.buscaServidorPorCpf(admissao.cpfServidor));
            }
            if(admissao.getCnpjOrgaoOrigemCedido() != null) {
                admissao.setCnpjOrgaoOrigemCedido(admissao.getCnpjOrgaoOrigemCedido().replace(".", "").replace("-", "").replace("/", ""));
            }
            if(admissao.getCnpjRppsRequisitado() != null) {
                admissao.setCnpjRppsRequisitado(admissao.getCnpjRppsRequisitado().replace(".", "").replace("-", "").replace("/", ""));
            }
            if(admissao.getCessaoCnpjOrgaoRemuneracao() != null) {
                admissao.setCessaoCnpjOrgaoRemuneracao(admissao.getCessaoCnpjOrgaoRemuneracao().replace(".", "").replace("-", "").replace("/", ""));
            }
            admissao.setAto(atoRepository.findById(admissao.getAto().getId()));
            admissao.setCargo(cargoRepository.findById(admissao.getCargo().getId()));
            admissao.setLotacao(lotacaoRepository.buscarLotacaoPorcodigo(admissao.codigoLotacao));

            if(admissao.getContratoValor().equals( BigDecimal.valueOf(0)) ){
                admissao.setContratoValor(null);
            }

            admissao.setChave(chave);
            admissaoRepository.update(admissao);
        }
        }


