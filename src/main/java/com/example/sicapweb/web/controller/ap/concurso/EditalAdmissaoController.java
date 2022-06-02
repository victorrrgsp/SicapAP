package com.example.sicapweb.web.controller.ap.concurso;

import com.example.sicapweb.model.EditalAprovadoConcurso;
import com.example.sicapweb.repository.concurso.EditalAprovadoRepository;
import com.example.sicapweb.repository.concurso.EditalVagaRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/ConcessaoAdmissao"})
public class EditalAdmissaoController {

    @Autowired
    private EditalAprovadoRepository editalAprovadoRepository;

    @Autowired
    private EditalVagaRepository editalVagaRepository;

    @CrossOrigin
    @GetMapping(path="/aprovados//{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<EditalAprovadoConcurso>> listaprov(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<EditalAprovadoConcurso> paginacaoUtil = editalAprovadoRepository.buscaPaginadaAprovados(pageable);
        return ResponseEntity.ok().body(paginacaoUtil);
    }
}
