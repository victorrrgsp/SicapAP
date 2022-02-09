package com.example.sicapweb.web.controller.servidores;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.pessoal.Servidor;
import com.example.sicapweb.repository.servidores.ServidorRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;


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
        @CrossOrigin
        @GetMapping(path = {"/{id}"})
        public ResponseEntity<?> findById(@PathVariable BigInteger id) {
            Servidor list = servidorRepository.findById(id);
            return ResponseEntity.ok().body(list);
        }
        @CrossOrigin
        @Transactional
        @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
        public ResponseEntity<Servidor> update(@RequestBody Servidor servidor, @PathVariable BigInteger id) {
            servidor.setId(id);
            InfoRemessa chave = servidorRepository.findById(id).getChave();
            servidor.setCpfServidor(servidor.getCpfServidor().replace(".", "").replace("-", "").replace("/", ""));
            if(servidor.getCpfConjuge() != null) {
                servidor.setCpfConjuge(servidor.getCpfConjuge().replace(".", "").replace("-", "").replace("/", ""));
            }
            if(servidor.getCpfMae() != null) {
                servidor.setCpfMae(servidor.getCpfMae().replace(".", "").replace("-", "").replace("/", ""));
            }
            servidor.setChave(chave);
            servidorRepository.update(servidor);
            return ResponseEntity.noContent().build();
        }
        @CrossOrigin
        @GetMapping(path = {"/findByCpf/{cpf}"})
        public ResponseEntity<?> findByCpf(@PathVariable String cpf) {
            Servidor list = servidorRepository.buscaServidorPorCpf(cpf);
            return ResponseEntity.ok().body(list);
        }
        @CrossOrigin
        @Transactional
        @DeleteMapping(value = {"/{id}"})
        public ResponseEntity<?> delete(@PathVariable BigInteger id) {
            servidorRepository.deleteRestrito(id);
            return ResponseEntity.noContent().build();
        }

    }

