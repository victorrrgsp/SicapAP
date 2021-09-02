package com.example.sicapweb.web.controller;
import br.gov.to.tce.model.adm.AdmAutenticacao;
import com.example.sicapweb.repository.AdmAutenticacaoRepository;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.math.BigInteger;
import java.net.URI;
import java.security.MessageDigest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value="/chaves")
public class ChavesController {

    @Autowired
    private AdmAutenticacaoRepository admAutenticacaoRepository;

    @CrossOrigin
    //@GetMapping(path="/pagination/{Pagina}/{Tamanho}/{Direcao}/{Campo}")
    //public ResponseEntity<PaginacaoUtil<AdmAutenticacao>> listChaves(@PathVariable Integer Pagina, @PathVariable Integer Tamanho,  @PathVariable String Direcao ,@PathVariable String Campo) {
    @GetMapping(path="/pagination")
    public ResponseEntity<PaginacaoUtil<AdmAutenticacao>> listChaves(Pageable pageable) {
        PaginacaoUtil<AdmAutenticacao> paginacaoUtil = admAutenticacaoRepository.buscaPaginada(pageable);
        return ResponseEntity.ok().body(paginacaoUtil);
    }

//    @CrossOrigin
//    @GetMapping
//    public ResponseEntity<List<AdmAutenticacao>> findAll() {
//        List<AdmAutenticacao> list = admAutenticacaoRepository.findAll();
//        return ResponseEntity.ok().body(list);
//    }

    // @ApiOperation(value="Salva uma Chave de Autorizacao para envio do Sicap AP")
    @CrossOrigin
    @Transactional
    @PostMapping("/salvar")
    public ResponseEntity<AdmAutenticacao> create(@RequestBody  AdmAutenticacao autenticacao) throws ParseException {

        Date data = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dataString = df.format(data);
        Date strToDate = df.parse(dataString);
        String value = ""+autenticacao.getExercicio()+"-"+autenticacao.getRemessa()+"-"+autenticacao.getUnidadeGestora().getId()+"-"+dataString+"WRF";
        String sha1 = "";

        try {
            //cria a chave hash SHA-1
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            digest.update(value.getBytes("utf8"));
            sha1 = String.format("%040x", new BigInteger(1, digest.digest()));
            autenticacao.setIdSistema(29);
            autenticacao.setData(strToDate);
            autenticacao.setChave(sha1);

           // chavesRepository.save(autenticacao);
        } catch (Exception e) {
            e.printStackTrace();
        }

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(autenticacao.getId()).toUri();
        return ResponseEntity.created(uri).body(autenticacao);

    }

    @CrossOrigin
    @GetMapping(path = {"/status/{Cnpj}/{Exercicio}/{Remessa}"})
    public Boolean findStatusChave(@PathVariable String Cnpj, @PathVariable Integer Exercicio, @PathVariable Integer Remessa) {
       // UnidadeGestora list = unidadeGestoraRepository.buscaUnidadeGestoraPorCnpj(Cnpj);
        Boolean status = admAutenticacaoRepository.getStatusChave(Cnpj, Exercicio, Remessa);
        System.out.println("aAAKUUUUUUUUUUUU"+status);

        return status;
    }

}