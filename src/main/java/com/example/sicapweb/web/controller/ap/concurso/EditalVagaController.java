package com.example.sicapweb.web.controller.ap.concurso;

import br.gov.to.tce.model.ap.concurso.EditalVaga;
import com.example.sicapweb.exception.InvalitInsert;
import com.example.sicapweb.repository.concurso.EditalRepository;
import com.example.sicapweb.repository.concurso.EditalVagaRepository;
import com.example.sicapweb.repository.geral.CargoRepository;
import com.example.sicapweb.repository.geral.UnidadeAdministrativaRepository;
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
import java.util.List;

@RestController
@RequestMapping({"/concursoVaga"})
public class EditalVagaController extends DefaultController<EditalVaga> {

    @Autowired
    private EditalVagaRepository editalVagaRepository;

    @Autowired
    private EditalRepository editalRepository;

    @Autowired
    private UnidadeAdministrativaRepository unidadeAdministrativaRepository;

    @Autowired
    private CargoRepository cargoRepository;

    @CrossOrigin
    @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<EditalVaga>> listVagas(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        
        PaginacaoUtil<EditalVaga> paginacaoUtil = editalVagaRepository.buscaPaginada(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        EditalVaga list = editalVagaRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping("/buscar/{id}")
    public ResponseEntity<List<EditalVaga>> findVagasByIdEdital(@PathVariable Integer id) {
        List<EditalVaga> list = editalVagaRepository.buscarVagasPorEdital(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping("/todos")
    public ResponseEntity<List<EditalVaga>> findVagas(){
        List<EditalVaga> list = editalVagaRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping
    public ResponseEntity<EditalVaga> create(@RequestBody EditalVaga editalVaga) {
            editalVaga.setChave(editalVagaRepository.buscarPrimeiraRemessa());
            editalVaga.setEdital(editalRepository.buscarEditalPorNumero(editalVaga.getNumeroEdital()));
            editalVaga.setUnidadeAdministrativa(unidadeAdministrativaRepository.buscarUnidadePorcodigo(editalVaga.codigoUnidadeAdministrativa));
            editalVaga.setCargo(cargoRepository.buscarCargoPorcodigo(editalVaga.codigoCargo));
            EditalVaga mesmocodigo = editalVagaRepository.buscarVagasPorCodigo(editalVaga.getCodigoVaga());
            if (mesmocodigo!=null) throw  new InvalitInsert("Ja existe vaga com esse codigo!!");
            editalVagaRepository.save(editalVaga);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(editalVaga.getId()).toUri();
            return ResponseEntity.created(uri).body(editalVaga);
    }

    @CrossOrigin
    @Transactional
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
    public ResponseEntity<EditalVaga> update(@RequestBody EditalVaga editalVaga, @PathVariable BigInteger id){
        try {
            
            editalVaga.setId(id);
            editalVaga.setChave(editalVagaRepository.buscarPrimeiraRemessa());
            editalVaga.setEdital(editalRepository.buscarEditalPorNumero(editalVaga.getNumeroEdital()));
            editalVaga.setUnidadeAdministrativa(unidadeAdministrativaRepository.buscarUnidadePorcodigo(editalVaga.codigoUnidadeAdministrativa));
            editalVaga.setCargo(cargoRepository.buscarCargoPorcodigo(editalVaga.codigoCargo));
            EditalVaga mesmocodigo = editalVagaRepository.buscarVagasPorCodigo(editalVaga.getCodigoVaga());
            if (mesmocodigo!=null) throw  new InvalitInsert("Ja existe vaga com esse codigo!!");
            editalVagaRepository.update(editalVaga);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new InvalitInsert("Erro na ATALIZACAO de dados, por favor cheque os canpos enviados ");
            //TODO: handle exception
        }
    }

    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public ResponseEntity<?> delete(@PathVariable BigInteger id) {
        editalVagaRepository.delete(id);
        return ResponseEntity.noContent().build();
    }
}
