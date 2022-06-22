package com.example.sicapweb.web.controller.ap.concurso;

import br.gov.to.tce.model.ap.concurso.ConcursoEnvio;
import br.gov.to.tce.model.ap.concurso.ConcursoEnvioAssinatura;
import br.gov.to.tce.model.ap.concurso.Edital;
import br.gov.to.tce.model.ap.concurso.documento.DocumentoAdmissao;
import br.gov.to.tce.util.Date;
import com.example.sicapweb.model.HashMessenger;
import com.example.sicapweb.model.ProcessoAdmissaoConcurso;
import com.example.sicapweb.repository.concurso.ConcursoEnvioAssinaturaRepository;
import com.example.sicapweb.repository.concurso.ConcursoEnvioRepository;
import com.example.sicapweb.repository.concurso.EditalRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.net.URI;
import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("/assinarConcurso")
public class AssinarConcursoController {

    @Autowired
    private ConcursoEnvioRepository concursoEnvioRepository;

    @Autowired
    private ConcursoEnvioAssinaturaRepository concursoEnvioAssinaturaRepository;

    @CrossOrigin
    @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<ConcursoEnvio>> listaAEnviosAguardandoAss(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<ConcursoEnvio> paginacaoUtil = concursoEnvioRepository.buscarEnviosAguardandoAss(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @PostMapping
    public ResponseEntity<List<ConcursoEnvio>> AddAssinaturas(@RequestBody List<ConcursoEnvio> listaconcursoenvios) {
        for(Integer i= 0; i < listaconcursoenvios.size(); i++){
            ConcursoEnvio envio = (ConcursoEnvio) listaconcursoenvios.get(i);
            ConcursoEnvioAssinatura assinatura =  new ConcursoEnvioAssinatura();
            assinatura.setData_Assinatura(new Date());
            assinatura.setConcursoEnvio(envio);
            assinatura.setIdCargo(User.getUser(concursoEnvioAssinaturaRepository.getRequest()).getCargo().getValor());
            assinatura.setCpf(User.getUser(concursoEnvioAssinaturaRepository.getRequest()).getCpf());
            HashMap<String, String> camposEnvioHasheaveis = new HashMap<>();
            camposEnvioHasheaveis.put("id",envio.getId().toString());
            camposEnvioHasheaveis.put("fase",envio.getFase().toString());
            camposEnvioHasheaveis.put("numero_edital",envio.getEdital().getNumeroEdital());
            camposEnvioHasheaveis.put("organizacao",envio.getEdital().getCnpjEmpresaOrganizadora());
            camposEnvioHasheaveis.put("meuCargo",User.getUser(concursoEnvioAssinaturaRepository.getRequest()).getCargo().getValor().toString());
            HashMessenger messenger = new HashMessenger(camposEnvioHasheaveis.toString());
            assinatura.setHashAssinado(messenger.getTexthashed());
            concursoEnvioAssinaturaRepository.save(assinatura);
        }
       //URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(lstdocumentoAdmissao.getClass()).toUri();
        return ResponseEntity.noContent().build();
    }

}
