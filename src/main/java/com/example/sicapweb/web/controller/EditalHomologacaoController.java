package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.concurso.Edital;
import br.gov.to.tce.model.ap.concurso.EditalHomologacao;
import br.gov.to.tce.model.ap.relacional.Ato;
import com.example.sicapweb.repository.AtoRepository;
import com.example.sicapweb.repository.EditalHomologacaoRepository;
import com.example.sicapweb.repository.EditalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigInteger;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/concursoHomologacao")
public class EditalHomologacaoController {

    @Autowired
    private EditalHomologacaoRepository editalHomologacaoRepository;

    @Autowired
    private EditalRepository editalRepository;

    @Autowired
    private AtoRepository atoRepository;

    @GetMapping("/")
    public String lista(ModelMap model, EditalHomologacao editalHomologacao) {
        model.addAttribute("homologacoes", editalHomologacaoRepository.findAll());
        return "concursoHomologacao";
    }

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<EditalHomologacao>> findAll() {
        List<EditalHomologacao> list = editalHomologacaoRepository.findAll();
        return ResponseEntity.ok().body(list);
    }


    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        EditalHomologacao list = editalHomologacaoRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping
    public ResponseEntity<EditalHomologacao> create(@RequestBody EditalHomologacao editalHomologacao) {
        editalHomologacao.setChave(editalHomologacaoRepository.buscarPrimeiraRemessa());
        //editalHomologacao.setEmpresaOrganizadora(empresaOrganizadoraRepository.buscaEmpresaPorCnpj(edital.getCnpjEmpresaOrganizadora()));
        editalHomologacaoRepository.save(editalHomologacao);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(editalHomologacao.getId()).toUri();
        return ResponseEntity.created(uri).body(editalHomologacao);
    }

    @CrossOrigin
    @Transactional
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
    public ResponseEntity<EditalHomologacao> update(@RequestBody EditalHomologacao editalHomologacao, @PathVariable BigInteger id) {
        editalHomologacao.setChave(editalRepository.buscarPrimeiraRemessa());
        editalHomologacao.setId(id);
        //edital.setEmpresaOrganizadora(empresaOrganizadoraRepository.buscaEmpresaPorCnpj(edital.getCnpjEmpresaOrganizadora()));
        editalHomologacaoRepository.update(editalHomologacao);
        return ResponseEntity.noContent().build();
    }

    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public ResponseEntity<?> delete(@PathVariable BigInteger id) {
        editalHomologacaoRepository.delete(id);
        return ResponseEntity.noContent().build();
    }
}
