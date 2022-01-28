package com.example.sicapweb.web.controller.ap.geral;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.relacional.Ato;
import br.gov.to.tce.model.ap.relacional.Cargo;
import com.example.sicapweb.repository.geral.AtoRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/ato")
public class AtoController extends DefaultController<Ato>  {

    @Autowired
    private AtoRepository atoRepository;
    @CrossOrigin
    @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<Ato>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<Ato> paginacaoUtil = atoRepository.buscaPaginada(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<Ato>> findAll() {
        List<Ato> list = atoRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        Ato list = atoRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }
    @CrossOrigin
    @GetMapping(path = {"/{numero}/{tipo}"})
    public ResponseEntity<?> findById(@PathVariable String numero, @PathVariable int tipo) {
        Ato list = atoRepository.buscarAtoPorNumero(numero, tipo);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
    public ResponseEntity<Ato> update(@RequestBody Ato ato, @PathVariable BigInteger id) {
        InfoRemessa chave = atoRepository.findById(id).getChave();
        ato.setNumeroAto(ato.getNumeroAto().replace("/", ""));
        ato.setCnpjUgPublicacao(ato.getCnpjUgPublicacao().replace(".", "").replace("-", "").replace("/", ""));
        ato.setId(id);
        ato.setChave(chave);
        atoRepository.update(ato);
        return ResponseEntity.noContent().build();
    }

}
