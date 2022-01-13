package com.example.sicapweb.web.controller.movimentacaoDePessoal;

import br.gov.to.tce.model.ap.pessoal.Admissao;
import br.gov.to.tce.model.ap.relacional.Lotacao;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.repository.movimentacaoDePessoal.AdmissaoRepository;
import com.example.sicapweb.repository.orgaosDeLotacoes.LotacaoRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import java.math.BigInteger;

    @RestController
    @RequestMapping({"/movimentacaoDePessoal/admissao"})
    public class AdmissaoController {

            @Autowired
            private AdmissaoRepository admissaoRepository;

            @CrossOrigin
            @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
            public ResponseEntity<PaginacaoUtil<Admissao>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
                PaginacaoUtil<Admissao> paginacaoUtil = admissaoRepository.buscaPaginada(pageable,searchParams,tipoParams);
                return ResponseEntity.ok().body(paginacaoUtil);
            }

        }


