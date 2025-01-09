package com.example.sicapweb.web.controller.remessa;

import com.example.sicapweb.repository.remessa.RemessaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/remessa"})
public class RemessaController {

    @Autowired
    private RemessaRepository remessaRepository;

    @CrossOrigin
    @GetMapping(path = {"/{exercicio}"})
    public ResponseEntity<List<Integer>> listaRemessasByExercicio(@PathVariable Integer exercicio){
        List<Integer> list = remessaRepository.findAllRemessasByExercicio(exercicio);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/remessaVigente/{exercicio}"})
    public ResponseEntity<?> findRemessaVigente(@PathVariable Integer exercicio) {
        List<Integer> infoRemessa = remessaRepository.buscarRemessaVigente(exercicio);
        return ResponseEntity.ok().body(infoRemessa);
    }

}
