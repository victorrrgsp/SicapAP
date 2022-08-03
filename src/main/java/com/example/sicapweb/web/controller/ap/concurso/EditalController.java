package com.example.sicapweb.web.controller.ap.concurso;

import br.gov.to.tce.model.ap.concurso.ConcursoEnvio;
import br.gov.to.tce.model.ap.concurso.Edital;
import com.example.sicapweb.repository.concurso.ConcursoEnvioRepository;
import com.example.sicapweb.repository.concurso.EditalRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigInteger;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import  com.example.sicapweb.exception.InvalitInsert;
@RestController
@RequestMapping({"/concursoEdital"})
public class EditalController extends DefaultController<Edital> {

    @Autowired
    private EditalRepository editalRepository;

    @Autowired
    private ConcursoEnvioRepository concursoEnvioRepository;

    @CrossOrigin
    @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<Edital>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<Edital> paginacaoUtil = editalRepository.buscaPaginada(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<Edital>> findAll() {
        List<Edital> list = editalRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        Edital list = editalRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/editaisNaoHomologados"})
    public ResponseEntity<List<Edital>> findEditalNaoHomologado() {
        List<Edital> list = editalRepository.buscarEditaisNaoHomologados();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping
    public ResponseEntity<Edital> create(@RequestBody Edital edital) {
        edital.setInfoRemessa(editalRepository.buscarPrimeiraRemessa());
        Edital e =editalRepository.buscarEditalPorNumero(edital.getNumeroEdital());

        if (e == null) {
            if  ( Integer.valueOf(edital.getNumeroEdital().substring(edital.getNumeroEdital().length()-4)) <1990 ||  Integer.valueOf(edital.getNumeroEdital().substring(edital.getNumeroEdital().length()-4)) > (LocalDateTime.now().getYear() +5) ) {
                throw new InvalitInsert("não é um número de Edital valido. Os ultinmos 4 digitos correspondem ao ano do edital !!");
            }

            editalRepository.save(edital);

            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(edital.getId()).toUri();
            return ResponseEntity.created(uri).body(edital);

        } else {

        throw new InvalitInsert("ja existe o edital!!");

        }

    }

    @CrossOrigin
    @Transactional
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
    public ResponseEntity<Edital> update(@RequestBody Edital edital, @PathVariable BigInteger id) {
        edital.setInfoRemessa(editalRepository.buscarPrimeiraRemessa());
        edital.setId(id);
        Edital e =editalRepository.buscarEditalPorNumero(edital.getNumeroEdital());

        if (e == null || edital.getId() == id) {
            if  ( Integer.valueOf(edital.getNumeroEdital().substring(edital.getNumeroEdital().length()-4)) <1990 ||  Integer.valueOf(edital.getNumeroEdital().substring(edital.getNumeroEdital().length()-4)) > (LocalDateTime.now().getYear() +5) ) {
                throw new InvalitInsert("não é um numero de Edital valido. Os ultinmos 4 digitos correspondem ao ano do edital !!");
            }

            ConcursoEnvio envio = concursoEnvioRepository.buscarEnvioFAse1PorEditalassinado(e.getId());
            if(envio != null){
                if (envio.getStatus()==3) throw new InvalitInsert("Edital não pode ser alterado pois ja foi enviado processo no econtas !!");
            }

            editalRepository.update(edital);
            return ResponseEntity.noContent().build();

        } else {
            throw new InvalitInsert("ja existe o edital!!");
        }


    }


    @CrossOrigin
    @GetMapping(path = {"/getInfoReciboEdital/{numproc}/{anoproc}"})
    public ResponseEntity<?> findInfoReciboEdital(@PathVariable Integer numproc, @PathVariable Integer anoproc) {
        List<Map<String, Object>> infoRecibo = editalRepository.buscarInfoReciboEdital(numproc,anoproc);
        return ResponseEntity.ok().body(Objects.requireNonNullElse(infoRecibo, "seminfo"));
    }

    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public ResponseEntity<?> delete(@PathVariable BigInteger id) {
        editalRepository.delete(id);
        return ResponseEntity.noContent().build();
    }


}
