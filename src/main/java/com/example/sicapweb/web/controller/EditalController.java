package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.concurso.Edital;
import br.gov.to.tce.model.ap.concurso.EmpresaOrganizadora;
import com.example.sicapweb.repository.EditalRepository;
import com.example.sicapweb.repository.EmpresaOrganizadoraRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/concursoEdital")
public class EditalController {

    @Autowired
    private EditalRepository editalRepository;

    @Autowired
    private EmpresaOrganizadoraRepository empresaOrganizadoraRepository;

    @GetMapping("/")
    public String lista(ModelMap model, @RequestParam("page")Optional<Integer> page, @RequestParam("dir")Optional<String> dir, Edital edital) {
        int paginaAtual = page.orElse(1);
        String ordem = dir.orElse("asc");

        PaginacaoUtil<Edital> pageEdital = editalRepository.buscaPaginada(paginaAtual, ordem, "numeroEdital");

        model.addAttribute("pageEdital", pageEdital);
        return "concursoEdital";
    }

    @Transactional
    @PostMapping("salvar")
    public String salvar(Edital edital) {
        editalRepository.save(edital);
        return "redirect:/concursoEdital/";
    }

    @ModelAttribute("empresas")
    public List<EmpresaOrganizadora> empresaOrganizadoraList() {
        return empresaOrganizadoraRepository.findAll();
    }
}
