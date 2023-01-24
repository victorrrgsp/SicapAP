package com.example.sicapweb.web.controller.ap.geral;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.concurso.EmpresaOrganizadora;
import br.gov.to.tce.model.ap.relacional.Lotacao;
import br.gov.to.tce.model.ap.relacional.UnidadeAdministrativa;
import com.example.sicapweb.exception.InvalitInsert;
import com.example.sicapweb.repository.geral.UnidadeAdministrativaRepository;
import com.example.sicapweb.repository.orgaosDeLotacoes.LotacaoRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigInteger;
import java.net.URI;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping({"/unidadeAdministrativa"})
public class UnidadeAdministrativaController  extends DefaultController<UnidadeAdministrativa> {

    @Autowired
    private UnidadeAdministrativaRepository unidadeAdministrativaRepository;

    @Autowired
    private LotacaoRepository  lotacaoRepository;

    @CrossOrigin
    @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<UnidadeAdministrativa>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<UnidadeAdministrativa> paginacaoUtil = unidadeAdministrativaRepository.findbyUgPaginada(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }
    @CrossOrigin
    @GetMapping(path="/pesquisaPorUg/{Ug}")
    public ResponseEntity<List<HashMap<String, Object>>> pesquisaPorUg(@PathVariable String Ug) {
         
        return ResponseEntity.ok().body(unidadeAdministrativaRepository.pesquisaPorUg(Ug));
    }

    @CrossOrigin
    @GetMapping(path="/pesquisaLotacaoPorUg/{Ug}")
    public ResponseEntity<List<Object>> pesquisaLotacaoPorUg(@PathVariable String Ug) {
        return ResponseEntity.ok().body(unidadeAdministrativaRepository.pesquisaLotacoesPorUg(Ug));
    }


    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<UnidadeAdministrativa>> findAll() {
        List<UnidadeAdministrativa> list = unidadeAdministrativaRepository.findbyUg();
        return ResponseEntity.ok().body(list);
    }
    @CrossOrigin
    @GetMapping("/pesquisaPorRemessa/{mes}/{ano}")
    public ResponseEntity<List<Object[]>> findByRemessa(@PathVariable("mes") int mes,@PathVariable("ano") int ano) {
        List<Object[]> list = unidadeAdministrativaRepository.buscarremessa(ano,mes);
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
    @PostMapping
    public ResponseEntity<UnidadeAdministrativa> create(@RequestBody UnidadeAdministrativa unidadeAdministrativa) {
        unidadeAdministrativa.setChave(unidadeAdministrativaRepository.buscarPrimeiraRemessa());
        unidadeAdministrativa.setCnpj(unidadeAdministrativa.getCnpj().replace(".", "").replace("-", "").replace("/", ""));
        unidadeAdministrativaRepository.save(unidadeAdministrativa);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(unidadeAdministrativa.getId()).toUri();
        return ResponseEntity.created(uri).body(unidadeAdministrativa);
    }

    @CrossOrigin
    @Transactional
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
    public void update(@RequestBody UnidadeAdministrativa unidadeAdministrativa, @PathVariable BigInteger id) {
        InfoRemessa chave = unidadeAdministrativaRepository.findById(id).getChave();
        unidadeAdministrativa.setCodigoUnidadeAdministrativa(unidadeAdministrativa.getCodigoUnidadeAdministrativa().replace("/", ""));
        unidadeAdministrativa.setCnpj(unidadeAdministrativa.getCnpj().replace(".", "").replace("-", "").replace("/", ""));
        unidadeAdministrativa.setId(id);
        unidadeAdministrativa.setChave(chave);
        unidadeAdministrativaRepository.update(unidadeAdministrativa);
    }
    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public void delete(@PathVariable BigInteger id) {
        List<Lotacao> list=lotacaoRepository.buscarLotacaoPorUA(id);
        if (list!=null) 
            throw new InvalitInsert("Existem lotaçoes pertencentes a essa unidade administrativa!");
        unidadeAdministrativaRepository.deleteRestrito(id); 
    }
}
