package com.example.sicapweb.web.controller.ap.geral;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.relacional.Ato;
import com.example.sicapweb.repository.geral.AtoRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
@CrossOrigin
@RestController
@RequestMapping("/ato")
public class AtoController extends DefaultController<Ato>  {

    @Autowired
    private AtoRepository atoRepository;

    @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<Ato>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<Ato> paginacaoUtil = atoRepository.buscaPaginadaAtos(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @GetMapping(path = "/vinculos/{id}")
    public ResponseEntity<List<HashMap<String,Object>>> listVinculos(@PathVariable BigInteger id ){
        List<HashMap<String,Object>> LISTVINCULOS =atoRepository.buscaVinculos(id,1);
        return ResponseEntity.ok().body(LISTVINCULOS);
    }

    @GetMapping
    public ResponseEntity<List<Ato>> findAll() {
        List<Ato> list = atoRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        Ato list = atoRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }
    @GetMapping(path = {"/{numero}/{tipo}"})
    public ResponseEntity<?> findById(@PathVariable String numero, @PathVariable int tipo) {
        Ato list = atoRepository.buscarAtoPorNumero(numero, tipo);
        return ResponseEntity.ok().body(list);
    }



    @Transactional
    @PostMapping("/create")
    public ResponseEntity<Ato> create(@RequestBody Ato ato) {
        InfoRemessa chave = atoRepository.findAll().get(0).getChave();
        ato.setNumeroAto(ato.getNumeroAto().replace("/", ""));
        ato.setCnpjUgPublicacao(ato.getCnpjUgPublicacao().replace(".", "").replace("-", "").replace("/", ""));
        ato.setChave(chave);
        ato.setId(null);
        atoRepository.save(ato);
        return ResponseEntity.noContent().build();
    }
    @Transactional
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
    public void update(@RequestBody Ato ato, @PathVariable BigInteger id) {
        InfoRemessa chave = atoRepository.findById(id).getChave();
        ato.setNumeroAto(ato.getNumeroAto().replace("/", ""));
        ato.setCnpjUgPublicacao(ato.getCnpjUgPublicacao().replace(".", "").replace("-", "").replace("/", ""));
        ato.setId(id);
        ato.setChave(chave);
        atoRepository.update(ato);
    }
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public void delete(@PathVariable BigInteger id) {
        atoRepository.deleteRestrito(id); 
    }

}
