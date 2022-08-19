package com.example.sicapweb.web.controller.ap.concessao;

import br.gov.to.tce.model.adm.AdmEnvio;
import com.example.sicapweb.repository.concessao.AdmEnvioRepository;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/admEnvio")
public class AdmEnvioController extends DefaultController<AdmEnvio> {

    @Autowired
    private AdmEnvioRepository admEnvioRepository;

    @GetMapping(path="/getByAdmissao/{idAdmissao}")
    public ResponseEntity<HashMap<String,Object>> listChaves(@PathVariable int idAdmissao) {
        return ResponseEntity.ok(admEnvioRepository.infoByRecibo(idAdmissao).get(0));
    }

    @GetMapping(path="/{searchParams}/{tipoParams}")
    public ResponseEntity<List<HashMap<String,Object>>> BuscaTotal(
            @PathVariable String searchParams,
            @RequestParam(required = false) List<String> Ug ,
            @RequestParam(required = false) Integer TipoRegistro ,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataInico,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataFim
    ){
        List<HashMap<String,Object>> Listatotal = admEnvioRepository.buscaTotalNaoPaginada(searchParams,Ug,TipoRegistro,dataInico,dataFim);
        return ResponseEntity.ok().body(Listatotal);
    }
}
