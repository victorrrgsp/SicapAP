package com.example.sicapweb.web.controller.ap.geral;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.relacional.Ato;
import br.gov.to.tce.model.ap.relacional.Lei;
import br.gov.to.tce.model.ap.relacional.UnidadeAdministrativa;
import com.example.sicapweb.repository.geral.UnidadeAdministrativaRepository;
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
@RequestMapping({"/unidadeAdministrativa"})
public class UnidadeAdministrativaController  extends DefaultController<UnidadeAdministrativa> {

    @Autowired
    private UnidadeAdministrativaRepository unidadeAdministrativaRepository;

    @CrossOrigin
    @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<UnidadeAdministrativa>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<UnidadeAdministrativa> paginacaoUtil = unidadeAdministrativaRepository.buscaPaginada(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }


    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<UnidadeAdministrativa>> findAll() {
        List<UnidadeAdministrativa> list = unidadeAdministrativaRepository.findAll();
        return ResponseEntity.ok().body(list);
    }
    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        UnidadeAdministrativa list = unidadeAdministrativaRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
    public ResponseEntity<UnidadeAdministrativa> update(@RequestBody UnidadeAdministrativa unidadeAdministrativa, @PathVariable BigInteger id) {
        InfoRemessa chave = unidadeAdministrativaRepository.findById(id).getChave();
        unidadeAdministrativa.setCodigoUnidadeAdministrativa(unidadeAdministrativa.getCodigoUnidadeAdministrativa().replace("/", ""));
        unidadeAdministrativa.setCnpj(unidadeAdministrativa.getCnpj().replace(".", "").replace("-", "").replace("/", ""));
        unidadeAdministrativa.setId(id);
        unidadeAdministrativa.setChave(chave);
        unidadeAdministrativaRepository.update(unidadeAdministrativa);
        return ResponseEntity.noContent().build();
    }
}
