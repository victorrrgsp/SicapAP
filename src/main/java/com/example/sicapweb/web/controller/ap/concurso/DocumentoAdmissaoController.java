package com.example.sicapweb.web.controller.ap.concurso;


import com.example.sicapweb.repository.concurso.EditalAprovadoRepository;
import com.example.sicapweb.repository.concurso.EditalVagaRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/documentoConcursoAdmissão"})
public class DocumentoAdmissaoController {

    @Autowired
    private EditalAprovadoRepository editalAprovadoRepository;

    @Autowired
    private EditalVagaRepository editalVagaRepository;


}
