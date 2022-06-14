package com.example.sicapweb.web.controller.ap.concurso;

import br.gov.to.tce.model.ap.concurso.ConcursoEnvio;
import br.gov.to.tce.model.ap.concurso.Edital;
import com.example.sicapweb.model.ProcessoAdmissaoConcurso;
import com.example.sicapweb.repository.concurso.ConcursoEnvioRepository;
import com.example.sicapweb.repository.concurso.EditalRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assinarConcurso")
public class AssinarConcursoController {

    @Autowired
    private ConcursoEnvioRepository concursoEnvioRepository;

    @CrossOrigin
    @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<ConcursoEnvio>> listaAEnviosAguardandoAss(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<ConcursoEnvio> paginacaoUtil = concursoEnvioRepository.buscarEnviosAguardandoAss(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }

}
