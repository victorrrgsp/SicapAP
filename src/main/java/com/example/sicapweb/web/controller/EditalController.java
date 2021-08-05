package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.concurso.Edital;
import br.gov.to.tce.model.ap.concurso.EmpresaOrganizadora;
import com.example.sicapweb.repository.EditalRepository;
import com.example.sicapweb.repository.EmpresaOrganizadoraRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigInteger;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/concursoEdital")
public class EditalController {

    @Autowired
    private EditalRepository editalRepository;

    @Autowired
    private EmpresaOrganizadoraRepository empresaOrganizadoraRepository;

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
    @Transactional
    @PostMapping
    public ResponseEntity<Edital> create(@RequestBody Edital edital) {
        edital.setInfoRemessa(editalRepository.buscarPrimeiraRemessa());
        editalRepository.save(edital);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(edital.getId()).toUri();
        return ResponseEntity.created(uri).body(edital);
    }

    @CrossOrigin
    @Transactional
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
    public ResponseEntity<Edital> update(@RequestBody Edital edital, @PathVariable BigInteger id){
        edital.setId(id);
        editalRepository.update(edital);
        return ResponseEntity.noContent().build();
    }

    @CrossOrigin
    @Transactional
    @DeleteMapping
    public ResponseEntity<?> delete(@PathVariable BigInteger id) {
        editalRepository.delete(id);
        return ResponseEntity.noContent().build();
    }

//    @GetMapping("/")
//    public String lista(ModelMap model, @RequestParam("page")Optional<Integer> page, @RequestParam("dir")Optional<String> dir, Edital edital) {
//        int paginaAtual = page.orElse(1);
//        String ordem = dir.orElse("asc");
//
//        PaginacaoUtil<Edital> pageEdital = editalRepository.buscaPaginada(paginaAtual, ordem, "numeroEdital");
//
//        model.addAttribute("pageEdital", pageEdital);
//        return "concursoEdital";
//    }
//
//    @Transactional
//    @PostMapping("salvar")
//    public String salvar(Edital edital) {
//        editalRepository.save(edital);
//        return "redirect:/concursoEdital/";
//    }
//
//    @ModelAttribute("empresas")
//    public List<EmpresaOrganizadora> empresaOrganizadoraList() {
//        return empresaOrganizadoraRepository.findAll();
//    }
}
