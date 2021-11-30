package com.example.sicapweb.web.controller.externo;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.relacional.Cargo;
import com.example.sicapweb.repository.externo.AcompanhamentoRemessaRepository;
import com.example.sicapweb.repository.remessa.GfipRepository;
import com.example.sicapweb.repository.remessa.InfoRemessaRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(value = "/externo/acompanhamentoRemessa")
public class AcompanhamentoRemessaController {


    @Autowired
    private AcompanhamentoRemessaRepository acompanhamentoRemessaRepository;

    @Autowired
    private GfipRepository gfipRepository;

    @Autowired
    private InfoRemessaRepository infoRemessaRepository;


    @CrossOrigin
    @GetMapping(path = {"/all"})
    public ResponseEntity<?> findTodos() {
        List<Map<String, Object>> infoRemessa = acompanhamentoRemessaRepository.buscarTodosAcompanhamentoRemessa();
        return ResponseEntity.ok().body(Objects.requireNonNullElse(infoRemessa, "semRemessa"));
    }


    @CrossOrigin
    @GetMapping(path = {"/getRemessa/{exercicio}/{remessa}"})
    public ResponseEntity<?> findByRemessa(@PathVariable Integer exercicio, @PathVariable Integer remessa) {
        List<Map<String, Object>> infoRemessa = acompanhamentoRemessaRepository.buscarAcompanhamentoRemessa(exercicio, remessa);
        return ResponseEntity.ok().body(Objects.requireNonNullElse(infoRemessa, "semRemessa"));
    }

    @CrossOrigin
    @GetMapping(path = {"/getExercicio/{exercicio}/{remessa}"})
    public ResponseEntity<?> findByExercicio(@PathVariable Integer exercicio, @PathVariable Integer remessa) {
        List<Map<String, Object>> infoRemessa = acompanhamentoRemessaRepository.buscarExercicioAcompanhamentoRemessa(exercicio, remessa);
        return ResponseEntity.ok().body(Objects.requireNonNullElse(infoRemessa, "semRemessa"));
    }




    @CrossOrigin
    @GetMapping(path = {"/{cargo}/{chave}"})
    public ResponseEntity<?> findResponsavel(@PathVariable String cargo, @PathVariable String chave ) {
        Integer tipoCargo;
        switch (cargo) {
            case "Gestor":
                tipoCargo = User.Cargo.Gestor.getValor();
                break;
            case "Responsável R.H.":
                tipoCargo = User.Cargo.ResponsavelRH.getValor();
                break;
            case "Controle Interno":
                tipoCargo = User.Cargo.ControleInterno.getValor();
                break;
            default:
                tipoCargo = 0;
                break;

        }
        InfoRemessa info = infoRemessaRepository.findById(chave);
        Object resp = acompanhamentoRemessaRepository.buscarResponsavelAssinatura(tipoCargo, info);
        return ResponseEntity.ok().body(Objects.requireNonNullElse(resp, "semPermissao"));
    }

}
