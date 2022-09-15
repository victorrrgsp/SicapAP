package com.example.sicapweb.web.controller.folhaDePagamento;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.folha.FolhaPagamento;
import com.example.sicapweb.repository.folhaDePagamento.FolhaDePagamentoRepository;
import com.example.sicapweb.repository.folhaDePagamento.FolhaItemRepository;
import com.example.sicapweb.repository.movimentacaoDePessoal.AdmissaoRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@RestController
    @RequestMapping({"/folhaDePagamento/folhaDePagamento"})
    public class FolhaDePagamentoController extends DefaultController<FolhaPagamento> {

        @Autowired
        private FolhaDePagamentoRepository folhaDePagamentoRepository;
        @Autowired
        private AdmissaoRepository admissaoRepository;
        @Autowired
        private FolhaItemRepository folhaItemRepository;
    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public void delete(@PathVariable BigInteger id) {
        folhaDePagamentoRepository.deleteRestrito(id); 
    }

        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<FolhaPagamento>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<FolhaPagamento> paginacaoUtil = folhaDePagamentoRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }
        @CrossOrigin
        @Transactional
        @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
        public ResponseEntity<FolhaPagamento> update(@RequestBody FolhaPagamento folhaPagamento, @PathVariable BigInteger id) {

            InfoRemessa chave = folhaDePagamentoRepository.findById(id).getChave();
            folhaPagamento.setChave(chave);
            folhaPagamento.setId(id);
            folhaPagamento.setAdmissao(admissaoRepository.findById(folhaPagamento.getAdmissao().getId()));
            folhaPagamento.setFolhaItem(folhaItemRepository.findById(folhaPagamento.getFolhaItem().getId()));
            folhaDePagamentoRepository.update(folhaPagamento);
            return ResponseEntity.noContent().build();
        }

    }
