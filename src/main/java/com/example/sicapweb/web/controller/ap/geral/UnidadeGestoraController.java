package com.example.sicapweb.web.controller.ap.geral;
import br.gov.to.tce.model.UnidadeGestora;
import com.example.sicapweb.repository.geral.UnidadeGestoraRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping({"/unidadeGestora"})
public class UnidadeGestoraController {

    @Autowired
    private UnidadeGestoraRepository unidadeGestoraRepository;

    @CrossOrigin
    @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<UnidadeGestora>> listUnidadeGestora(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<UnidadeGestora> paginacaoUtil = unidadeGestoraRepository.buscaPaginadaUnidadeGestora(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<UnidadeGestora>> findAll() {
        List<UnidadeGestora> list = unidadeGestoraRepository.findAll();
        return ResponseEntity.ok().body(list);
    }
    @CrossOrigin
    @GetMapping(path = "/findAllWithConsesorios")
    public ResponseEntity<List<UnidadeGestora>> findAllWithConsesorios() {
        List<UnidadeGestora> list = unidadeGestoraRepository.findAllWithConsesorios();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = "/findAllWithConcursos")
    public ResponseEntity<List<UnidadeGestora>> findAllWithConcursos() {
        List<UnidadeGestora> list = unidadeGestoraRepository.findAllWithEnvioConcursos();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = "/findAllWithRegistros")
    public ResponseEntity<List<UnidadeGestora>> findAllWithRegistros() {
        List<UnidadeGestora> list = unidadeGestoraRepository.findAllWithRegistros();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/todos"})
    public ResponseEntity<List<UnidadeGestora>> findTodos() {
        List<UnidadeGestora> list = unidadeGestoraRepository.buscaTodasUnidadeGestora();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/todosOutros"})
    public ResponseEntity<List<UnidadeGestora>> findTodosOutros() {
        List<UnidadeGestora> list = unidadeGestoraRepository.buscaTodasOutraUnidadeGestora();
        return ResponseEntity.ok().body(list);
    }
    @CrossOrigin
    @GetMapping(path = {"/ugServidoresComSociedadePrivada"})
    public ResponseEntity<List<Object[]>> findUGServidoresComSociedadePrivada() {
        List<Object[]> list = unidadeGestoraRepository.buscaugServidoresComsociedade();
        return ResponseEntity.ok().body(list);
    }
    @CrossOrigin
    @GetMapping(path = {"/todosNome"})
    public ResponseEntity<List<Object>> findTodosNome() {
        List<Object> list = unidadeGestoraRepository.buscaNomeCnpjUnidadeGestora();
        return ResponseEntity.ok().body(list);
    }
    @CrossOrigin
    @GetMapping(path = {"/{Cnpj}"})
    public ResponseEntity<?> findById(@PathVariable String Cnpj) {
        UnidadeGestora list = unidadeGestoraRepository.buscaUnidadeGestoraPorCnpj(Cnpj);
        return ResponseEntity.ok().body(list);
    }
    @CrossOrigin
    @GetMapping(path = {"/vigencia/{Cnpj}/{Exercicio}/{Remessa}"})
    public ResponseEntity<List<Integer>>  buscaVigenciaUnidadeGestoraPorCnpj(@PathVariable String Cnpj, @PathVariable Integer Exercicio, @PathVariable Integer Remessa) {
        List<Integer> resposta = unidadeGestoraRepository.buscaVigenciaUnidadeGestoraPorCnpj(Cnpj, Exercicio, Remessa);
        return ResponseEntity.ok().body(resposta);
    }
}
