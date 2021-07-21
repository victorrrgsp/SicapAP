package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.concurso.Edital;
import br.gov.to.tce.model.ap.concurso.EditalHomologacao;
import br.gov.to.tce.model.ap.relacional.Ato;
import com.example.sicapweb.repository.AtoRepository;
import com.example.sicapweb.repository.EditalHomologacaoRepository;
import com.example.sicapweb.repository.EditalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

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

    @Transactional
    @PostMapping("salvar")
    public String salvar(EditalHomologacao editalHomologacao) {
        editalHomologacaoRepository.save(editalHomologacao);
        return "redirect:/concursoHomologacao/";
    }

    @ModelAttribute("editais")
    public List<Edital> editalList() {
        return editalRepository.findAll();
    }

    @ModelAttribute("atos")
    public List<Ato> atoList() {
        return atoRepository.findAll();
    }
}
