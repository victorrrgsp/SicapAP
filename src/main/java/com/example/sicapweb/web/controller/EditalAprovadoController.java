package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.concurso.Edital;
import br.gov.to.tce.model.ap.concurso.EditalAprovado;
import br.gov.to.tce.model.ap.concurso.EditalVaga;
import com.example.sicapweb.repository.EditalAprovadoRepository;
import com.example.sicapweb.repository.EditalRepository;
import com.example.sicapweb.repository.EditalVagaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/concursoAprovado")
public class EditalAprovadoController {

    @Autowired
    private EditalAprovadoRepository editalAprovadoRepository;

    @Autowired
    private EditalRepository editalRepository;

    @Autowired
    private EditalVagaRepository editalVagaRepository;

    @GetMapping("/")
    public String lista(ModelMap model, EditalAprovado editalAprovado) {
        model.addAttribute("aprovados", editalAprovadoRepository.findAll());
        return "concursoAprovado";
    }

    @Transactional
    @PostMapping("salvar")
    public String salvar(EditalAprovado editalAprovado) {
        editalAprovadoRepository.save(editalAprovado);
        return "redirect:/concursoAprovado/";
    }

    @ModelAttribute("editais")
    public List<Edital> editalList() {
        return editalRepository.findAll();
    }

    @ModelAttribute("vagas")
    public List<EditalVaga> editalVagaList() {
        return editalVagaRepository.findAll();
    }
}
