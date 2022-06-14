package com.example.sicapweb.web.controller.ap.concurso;

import com.example.sicapweb.model.ProcessoAdmissaoConcurso;
import com.example.sicapweb.repository.concurso.ProcessoAdmissaoRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assinarAdmissao")
public class AssinarAdmissaoController {
    @Autowired
    private ProcessoAdmissaoRepository processoAdmissaoRepository;

    @CrossOrigin
    @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<ProcessoAdmissaoConcurso>> listaAProcessosAguardandoAss(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<ProcessoAdmissaoConcurso> paginacaoUtil = processoAdmissaoRepository.buscarProcessosAguardandoAss(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }
}
