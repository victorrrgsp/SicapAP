package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.concurso.Edital;
import br.gov.to.tce.model.ap.concurso.EditalVaga;
import br.gov.to.tce.model.ap.relacional.Cargo;
import br.gov.to.tce.model.ap.relacional.UnidadeAdministrativa;
import com.example.sicapweb.repository.CargoRepository;
import com.example.sicapweb.repository.EditalRepository;
import com.example.sicapweb.repository.EditalVagaRepository;
import com.example.sicapweb.repository.UnidadeAdministrativaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/concursoVaga")
public class EditalVagaController {

    @Autowired
    private EditalVagaRepository editalVagaRepository;

    @Autowired
    private EditalRepository editalRepository;

    @Autowired
    private UnidadeAdministrativaRepository unidadeAdministrativaRepository;

    @Autowired
    private CargoRepository cargoRepository;

    @GetMapping("/")
    public String lista(ModelMap model, EditalVaga editalVaga) {
        model.addAttribute("vagas", editalVagaRepository.findAll());
        return "concursoVaga";
    }

    @Transactional
    @PostMapping("salvar")
    public String salvar(EditalVaga editalVaga) {
        editalVagaRepository.save(editalVaga);
        return "redirect:/concursoVaga/";
    }

    @ModelAttribute("editais")
    public List<Edital> editalList() {
        return editalRepository.findAll();
    }

    @ModelAttribute("unidades")
    public List<UnidadeAdministrativa> unidadeAdministrativaList() {
        return unidadeAdministrativaRepository.findAll();
    }

    @ModelAttribute("cargos")
    public List<Cargo> cargoList() {
        return cargoRepository.findAll();
    }
}
