package com.example.sicapweb.web.controller.servidores;

import br.gov.to.tce.model.ap.pessoal.Servidor;
import com.example.sicapweb.repository.servidores.ServidorRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


    @RestController
    @RequestMapping({"/servidores/servidor"})
    public class ServidorController {

        @Autowired
        private ServidorRepository servidorRepository;

        @CrossOrigin
        @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
        public ResponseEntity<PaginacaoUtil<Servidor>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
            PaginacaoUtil<Servidor> paginacaoUtil = servidorRepository.buscaPaginada(pageable,searchParams,tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }

    }

