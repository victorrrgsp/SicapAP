package com.example.sicapweb.web.controller.remessa;

import com.example.sicapweb.repository.RemessaRepository;
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
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<List<Integer>> listaRemessasByExercicio(@PathVariable Integer id){
        List<Integer> list = remessaRepository.findAllRemessasByExercicio(id);
        return ResponseEntity.ok().body(list);
    }



}
