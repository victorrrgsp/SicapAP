package com.example.sicapweb.web.controller.ap.concurso;

import br.gov.to.tce.model.ap.concurso.EditalVaga;
import com.example.sicapweb.exception.InvalitInsert;
import com.example.sicapweb.repository.concurso.DocumentoAdmissaoRepository;
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
                 return ResponseEntity.ok().body(editalVagaRepository.buscaPaginada(pageable,searchParams,tipoParams));
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        return ResponseEntity.ok().body(editalVagaRepository.findById(id));
    }

    @CrossOrigin
    @GetMapping("/buscar/{id}")
    public ResponseEntity<List<EditalVaga>> findVagasByIdEdital(@PathVariable Integer id) {
        return ResponseEntity.ok().body(editalVagaRepository.buscarVagasPorEdital(id));
    }

    @CrossOrigin
    @GetMapping("/todos")
    public ResponseEntity<List<EditalVaga>> findVagas(){
        return ResponseEntity.ok().body(editalVagaRepository.findAll());
    }

    @CrossOrigin
    @Transactional
    @PostMapping
    public ResponseEntity<EditalVaga> create(@RequestBody EditalVaga editalVaga) {
            editalVaga.setChave(editalVagaRepository.buscarPrimeiraRemessa());
            editalVaga.setEdital(editalRepository.buscarEditalPorNumero(editalVaga.getNumeroEdital(),editalVaga.getComplementoEdital()));
            editalVaga.setUnidadeAdministrativa(unidadeAdministrativaRepository.buscarUnidadePorcodigo(editalVaga.codigoUnidadeAdministrativa));
            editalVaga.setCargo(cargoRepository.buscarCargoPorcodigo(editalVaga.codigoCargo));
            EditalVaga mesmocodigo = editalVagaRepository.buscarVagasPorCodigoTipo(editalVaga.getCodigoVaga(),editalVaga.getTipoConcorrencia());
            
            // if (mesmocodigo!=null )
            //     throw  new InvalitInsert("Ja existe vaga com esse codigo!!");

            editalVagaRepository.save(editalVaga);

            return ResponseEntity.created(ServletUriComponentsBuilder.
                    fromCurrentRequest().path("/{id}").
                    buildAndExpand(editalVaga.getId()).
                    toUri()).body(editalVaga);
    }

    @CrossOrigin
    @Transactional
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
    public void update(@RequestBody EditalVaga editalVaga, @PathVariable BigInteger id){
        editalVaga.setId(id);
        editalVaga.setChave(editalVagaRepository.buscarPrimeiraRemessa());
        editalVaga.setEdital(editalRepository.buscarEditalPorNumero(editalVaga.getNumeroEdital(),editalVaga.getComplementoEdital() ));
        editalVaga.setUnidadeAdministrativa(unidadeAdministrativaRepository.buscarUnidadePorcodigo(editalVaga.codigoUnidadeAdministrativa));
        editalVaga.setCargo(cargoRepository.buscarCargoPorcodigo(editalVaga.codigoCargo));
        EditalVaga mesmocodigo = editalVagaRepository.buscarVagasPorCodigo(editalVaga.getCodigoVaga());
        EditalVaga mesmoId = editalVagaRepository.findById(id);

        if (!mesmoId.getCodigoVaga().equals(editalVaga.getCodigoVaga()) && mesmocodigo != null )
            throw  new InvalitInsert("Ja existe vaga com esse codigo!!");
        try {
            editalVagaRepository.update(editalVaga);
        } catch (Exception e) {
            throw new InvalitInsert("Erro na ATALIZACAO de dados, por favor cheque os canpos enviados ");
            //TODO: handle exception
        }
    }

    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public void delete(@PathVariable BigInteger id) {

        editalVagaRepository.delete(id);

    }
}
