package com.example.sicapweb.web.controller.ap.concessao;

import br.gov.to.tce.model.adm.AdmEnvio;
import com.example.sicapweb.repository.concessao.AdmEnvioRepository;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<AdmEnvio>> BuscaTotal(@PathVariable String searchParams, @PathVariable Integer tipoParams){
        List<AdmEnvio> Listatotal = admEnvioRepository.buscaTotalNaoPaginada(searchParams,tipoParams);
        return ResponseEntity.ok().body(Listatotal);
    }
}
