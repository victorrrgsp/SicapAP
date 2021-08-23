package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.pessoal.Aposentadoria;
import com.example.sicapweb.repository.AposentadoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/documentoConcessaoReversaoAposentadoriaReserva")
public class ConcessaoReversaoAposentadoriaReservaController  extends DefaultController<Aposentadoria>  {

    @Autowired
    private AposentadoriaRepository aposentadoriaRepository;

    @CrossOrigin
    @GetMapping("/findReversaoAposentadoriaReserva")
    public ResponseEntity<List<Aposentadoria>> findReversaoAposentadoriaReserva() {
        List<Aposentadoria> list = aposentadoriaRepository.buscarReversaoAposentadoriaReserva();
        return ResponseEntity.ok().body(list);
    }
}
