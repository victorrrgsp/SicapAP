package com.example.sicapweb.web.controller.ap.geral;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.sicapweb.repository.geral.RelatorioRepository;
import com.example.sicapweb.web.controller.DefaultController;

import br.gov.to.tce.model.ap.relacional.Lei;

@RestController
@RequestMapping("/Relatorios")
public class RelatoriosController extends DefaultController<Lei> {
    @Autowired
    private RelatorioRepository relatorioRepository; 

    @CrossOrigin
    @GetMapping(path = "/folhaAnalitica")
    public ResponseEntity<List<HashMap<String, Object>>> listChaves(@RequestParam(required = false) String cpf,
                                                                    @RequestParam(required = false) String nome,
                                                                    @RequestParam String Natureza,
                                                                    @RequestParam(required = false) List<String> Vinculo,
                                                                    @RequestParam int ano,
                                                                    @RequestParam int mes,
                                                                    @RequestParam(required = false) List<String> lotacao,
                                                                    @RequestParam(required = false) List<String> UnidadeAdministrativa,
                                                                    @RequestParam(required = false) String folhaItem,
                                                                    @RequestParam String UnidadeGestora){
        List<HashMap<String, Object>> result = relatorioRepository.buscarfolhaAnalitica(cpf, 
                                                                                        nome, 
                                                                                        Natureza, 
                                                                                        Vinculo,
                                                                                        ano, 
                                                                                        mes, 
                                                                                        lotacao, 
                                                                                        UnidadeAdministrativa, 
                                                                                        UnidadeGestora,
                                                                                        folhaItem);
        return ResponseEntity.ok().body(result);
    }

}
