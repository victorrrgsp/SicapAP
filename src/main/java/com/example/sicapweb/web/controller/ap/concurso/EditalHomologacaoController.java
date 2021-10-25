package com.example.sicapweb.web.controller.ap.concurso;

import br.gov.to.tce.model.ap.concurso.EditalHomologacao;
import com.example.sicapweb.repository.concurso.EditalHomologacaoRepository;
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

@RestController
@RequestMapping({"/concursoHomologacao"})
public class EditalHomologacaoController extends DefaultController<EditalHomologacao> {

    @Autowired
    private EditalHomologacaoRepository editalHomologacaoRepository;

    @Autowired
    private EditalRepository editalRepository;

    @CrossOrigin
    @GetMapping(path = "/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<EditalHomologacao>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<EditalHomologacao> paginacaoUtil = editalHomologacaoRepository.buscaPaginada(pageable, searchParams, tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
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
