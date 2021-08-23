package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.pessoal.Aposentadoria;
import com.example.sicapweb.repository.AposentadoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;

@RestController
@RequestMapping("/documentoConcessaoRevisaoReserva")
public class ConcessaoRevisaoReservaController  extends DefaultController<Aposentadoria> {

    @Autowired
    private AposentadoriaRepository aposentadoriaRepository;
    @PostConstruct
    public void initialize() {
        this.clazz  = "com.example.sicapweb.repository.AposentadoriaRepository";
    }

    @CrossOrigin
    @GetMapping("/findAposentadoriaRevisoesReserva")
    public ResponseEntity<List<Aposentadoria>> findAposentadoriaRevisoesReserva() {
        List<Aposentadoria> list = aposentadoriaRepository.buscarAposentadoriaRevisaoReserva();
        return ResponseEntity.ok().body(list);
    }
}
