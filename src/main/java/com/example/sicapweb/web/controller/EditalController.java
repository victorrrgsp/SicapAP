package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.concurso.Edital;
import com.example.sicapweb.repository.EditalRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/concursoEdital")
public class EditalController {

    @Autowired
    private EditalRepository editalDao;

    @GetMapping("/")
    public String listaEdital(ModelMap model, @RequestParam("page")Optional<Integer> page, @RequestParam("dir")Optional<String> dir) {
        int paginaAtual = page.orElse(1);
        String ordem = dir.orElse("asc");

        PaginacaoUtil<Edital> pageEdital = editalDao.buscaPaginada(paginaAtual, ordem);

        model.addAttribute("pageEdital", pageEdital);
        return "concursoEdital";
    }
}
