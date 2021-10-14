package com.example.sicapweb.web.controller.remessa;

import com.example.sicapweb.repository.geral.ExercicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value="/exercicio")
public class ExercicioController {

    @Autowired
    private ExercicioRepository exercicioRepository;
    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<Integer>> findAll(){
        List<Integer> list = exercicioRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = "/all")
    public ResponseEntity<List<Integer>> findExercicio(){
        List<Integer> list = exercicioRepository.findExercicio();
        return ResponseEntity.ok().body(list);
    }


}
