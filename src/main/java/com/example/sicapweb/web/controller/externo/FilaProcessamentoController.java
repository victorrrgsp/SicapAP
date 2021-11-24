
package com.example.sicapweb.web.controller.externo;

import br.gov.to.tce.model.InfoRemessa;
import com.example.sicapweb.model.ProcessoVO;
import com.example.sicapweb.repository.externo.FilaProcessamentosRepository;
import com.example.sicapweb.repository.remessa.InfoRemessaRepository;
import com.example.sicapweb.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/filaProcessamento")
public class FilaProcessamentoController {


    @Autowired
    private FilaProcessamentosRepository filaProcessamentoRepository;


    @Autowired
    private InfoRemessaRepository infoRemessaRepository;


    @CrossOrigin
    @GetMapping(path = {"/processos"})
    public ResponseEntity<?> findProcessos() {
        var infoRemessa = filaProcessamentoRepository.processo();

        return ResponseEntity.ok().body(Objects.requireNonNullElse(infoRemessa, "semRemessa"));
    }


    @CrossOrigin
    @GetMapping(path = {"/fila"})
    public ResponseEntity<?> filaProcessos() {
        var infoRemessa = filaProcessamentoRepository.filaProcessamentos();
        return ResponseEntity.ok().body(Objects.requireNonNullElse(infoRemessa, "semRemessa"));
    }




}
