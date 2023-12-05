package com.example.sicapweb.web.controller.remessa;


import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.folha.documento.Gfip;
import com.example.sicapweb.repository.remessa.AssinarRemessaRepository;
import com.example.sicapweb.repository.remessa.GfipRepository;
import com.example.sicapweb.repository.remessa.InfoRemessaRepository;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
@CrossOrigin
@RestController
@RequestMapping("/cadastrarGfip")
public class GfipController extends DefaultController<InfoRemessa> {

    @Autowired
    InfoRemessaRepository infoRemessaRepository;

    @Autowired
    private GfipRepository gfipRepository;

    @Autowired
    private AssinarRemessaRepository assinarRemessaRepository;

    HashMap<String, Object> gfip = new HashMap<String, Object>();

    public class GfipDocumento {
        private InfoRemessa infoRemessa;

        private String situacaoGfip;

        private String situacaoBoleto;

        private String situacaoComprovante;

        public InfoRemessa getInfoRemessa() {
            return infoRemessa;
        }

        public void setInfoRemessa(InfoRemessa infoRemessa) {
            this.infoRemessa = infoRemessa;
        }

        public String getSituacaoGfip() {
            return situacaoGfip;
        }

        public void setSituacaoGfip(String situacaoGfip) {
            this.situacaoGfip = situacaoGfip;
        }

        public String getSituacaoBoleto() {
            return situacaoBoleto;
        }

        public void setSituacaoBoleto(String situacaoBoleto) {
            this.situacaoBoleto = situacaoBoleto;
        }

        public String getSituacaoComprovante() {
            return situacaoComprovante;
        }

        public void setSituacaoComprovante(String situacaoComprovante) {
            this.situacaoComprovante = situacaoComprovante;
        }
    }

    public ResponseEntity<?> findSituacao(String chave, String tipo) {
        String situacao = gfipRepository.findSituacao(chave, tipo);
        return ResponseEntity.ok().body(situacao);
    }


    @GetMapping
    public ResponseEntity<List<InfoRemessa>> findAll() {
        List<InfoRemessa> list = infoRemessaRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(path = {"/getDocumentos"})
    public ResponseEntity<?> findAllDocumentos() {
        InfoRemessa infoRemessa = assinarRemessaRepository.buscarRemessaAberta();
        if (infoRemessa == null) {
            gfip = null;
            return ResponseEntity.ok().body(Objects.requireNonNullElse(gfip, "semRemessa"));
        }
        if (infoRemessa != null) {
            gfip = new HashMap<String, Object>();
            GfipDocumento gfipDocumento = new GfipDocumento();
            gfipDocumento.setInfoRemessa(infoRemessa);
            gfipDocumento.setSituacaoGfip(gfipRepository.findSituacao(infoRemessa.getChave(), "GFIP"));
            gfipDocumento.setSituacaoBoleto(gfipRepository.findSituacao(infoRemessa.getChave(), "boletoGFIP"));
            gfipDocumento.setSituacaoComprovante(gfipRepository.findSituacao(infoRemessa.getChave(), "comprovanteGFIP"));
            gfip.put("Gfip", gfipDocumento);
        } else {
            gfip = null;
        }

        return ResponseEntity.ok().body(Objects.requireNonNullElse(gfip, "semRemessa"));
    }

    @GetMapping(path = {"/find/{chave}"})
    public ResponseEntity<InfoRemessa> findById(@PathVariable String chave) {
        InfoRemessa list = infoRemessaRepository.findById(chave);
        return ResponseEntity.ok().body(list);
    }

    @Transactional
    @PostMapping("/upload/{chave}/{tipo}")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file, @PathVariable String chave, @PathVariable String tipo) {
        
        // Verificar o tipo de arquivo
        getFileType(file);

        Gfip gfip = new Gfip();
        Date hoje = new Date();
        gfip.setInfoRemessa(infoRemessaRepository.findById(chave));
        String idCastor = super.setCastorFile(file, tipo);
        gfip.setIdCastorFile(idCastor);
        gfip.setTipo(tipo);
        gfip.setData(hoje);
        gfipRepository.save(gfip);
        return ResponseEntity.ok().body(idCastor);
    }

    @GetMapping(path = {"/anexos/{chave}/{tipo}"})
    public ResponseEntity<?> findByDocumento(@PathVariable String chave, @PathVariable String tipo) {
        Gfip list = gfipRepository.buscarDocumentoGfip(chave, tipo).get(0);
        return ResponseEntity.ok().body(list);
    }
    @GetMapping(path = {"/anexosanteriores/{chave}/{tipo}"})
    public ResponseEntity<?> findByDocumentoanteriores(@PathVariable String chave, @PathVariable String tipo) {
        var result = gfipRepository.buscarDocumentoAnteriorGfip(chave, tipo).get(0);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping(path = {"/GetAll/{UG}"})
    public ResponseEntity<?> findAll(@PathVariable String UG) {
        List<Gfip> list = gfipRepository.buscarDocumentoAllGfip(UG);
        return ResponseEntity.ok().body(list);
    }
    @GetMapping(path = {"/GetByUgUsuario"})
    public ResponseEntity<?> findByUgUsuario() {
        List<Gfip> list = gfipRepository.buscarDocumentoByUgUsuario();
        return ResponseEntity.ok().body(list);
    }
}
