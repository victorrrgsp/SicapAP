package com.example.sicapweb.web.controller.ap.concurso;

import br.gov.to.tce.model.ap.concurso.Edital;
import br.gov.to.tce.model.ap.concurso.ProcessoAdmissao;
import br.gov.to.tce.model.ap.concurso.documento.DocumentoAdmissao;
import com.example.sicapweb.exception.InvalitInsert;
import com.example.sicapweb.model.EditalFinalizado;
import com.example.sicapweb.model.NomeacaoConcurso;
import com.example.sicapweb.model.ProcessoAdmissaoConcurso;
import com.example.sicapweb.repository.concurso.*;
import com.example.sicapweb.repository.geral.admissaoRepository;
import com.example.sicapweb.model.EditalAprovadoConcurso;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigInteger;
import java.net.URI;
import java.util.List;


@RestController
@RequestMapping({"/ConcessaoAdmissao"})
public class EditalAdmissaoController {

    @Autowired
    private EditalAprovadoRepository editalAprovadoRepository;

    @Autowired
    private ProcessoAdmissaoRepository processoAdmissaoRepository;

    @Autowired
    private admissaoRepository admissaoRepository;

    @Autowired
    private EditalRepository editalRepository;


    @CrossOrigin
    @GetMapping(path="/aprovados/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<EditalAprovadoConcurso>> listaprov(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<EditalAprovadoConcurso> paginacaoUtil = editalAprovadoRepository.buscaPaginadaAprovados(pageable);
        return ResponseEntity.ok().body(paginacaoUtil);
    }


    @CrossOrigin
    @GetMapping(path="/editaisfinalizados/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<EditalFinalizado>> listaEditaisFinalizados(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<EditalFinalizado> paginacaoUtil = editalRepository.buscarEditaiFinalizados(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @GetMapping(path="/admissoes/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<NomeacaoConcurso>> listaAdmissoes(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<NomeacaoConcurso> paginacaoUtil = admissaoRepository.buscarAdmissoes(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @GetMapping(path="/processos/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<ProcessoAdmissaoConcurso>> listaAProcessos(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<ProcessoAdmissaoConcurso> paginacaoUtil = processoAdmissaoRepository.buscarProcessos(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @GetMapping(path="/processosAguardandoAss/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<ProcessoAdmissaoConcurso>> listaAProcessosAguardandoAss(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<ProcessoAdmissaoConcurso> paginacaoUtil = processoAdmissaoRepository.buscarProcessosAguardandoAss(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }


    @CrossOrigin
    @GetMapping(path = {"/processos/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        ProcessoAdmissao list = processoAdmissaoRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping(path = "/processos")
    public ResponseEntity<ProcessoAdmissao> create(@RequestBody ProcessoAdmissao processoAdmissao) {

        processoAdmissao.setCnpjEmpresaOrganizadora(User.getUser(processoAdmissaoRepository.getRequest()).getUnidadeGestora().getId());
        processoAdmissaoRepository.save(processoAdmissao);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(processoAdmissao.getClass()).toUri();
        return ResponseEntity.created(uri).body(processoAdmissao);
    }

    @CrossOrigin
    @Transactional
    @PutMapping(path = {"/processos/enviar/{id}"})
    public ResponseEntity<ProcessoAdmissao> Enviar(@PathVariable BigInteger id) {
        ProcessoAdmissao processoAdmissao = processoAdmissaoRepository.findById(id);
        if (processoAdmissao!= null) {
            processoAdmissao.setStatus(2);
            processoAdmissaoRepository.update(processoAdmissao);
        }
        return ResponseEntity.ok().body(processoAdmissao);
    }


    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/processos/{id}"})
    public ResponseEntity<?> delete(@PathVariable BigInteger id) {
        ProcessoAdmissao processoAdmissao = processoAdmissaoRepository.findById(id);
        if (processoAdmissao!= null) {
            if(processoAdmissao.getStatus()==1){
                processoAdmissaoRepository.delete(id);
            }
        }
        return ResponseEntity.noContent().build();
    }
}
