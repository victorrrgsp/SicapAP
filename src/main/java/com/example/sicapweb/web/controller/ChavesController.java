package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.adm.AdmAutenticacao;
import com.example.sicapweb.repository.AdmAutenticacaoRepository;
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

@RestController
@RequestMapping(value="/chaves")
public class ChavesController {

    @Autowired
    private AdmAutenticacaoRepository admAutenticacaoRepository;

    @CrossOrigin
     @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<AdmAutenticacao>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {

        PaginacaoUtil<AdmAutenticacao> paginacaoUtil = admAutenticacaoRepository.buscaPaginadaChaves(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }

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
//        Integer remessa;
//        Integer exercicio;
//        if(!(autenticacao.getRemessa() == 1 && autenticacao.getExercicio()==2021)){
//            if (autenticacao.getRemessa()==1 ) {
//                remessa=12;
//                exercicio= autenticacao.getExercicio()-1;
//            }
//            else{
//                remessa= autenticacao.getRemessa();
//                exercicio= autenticacao.getExercicio();
//            }
//            Integer chavesRemessaAtual =admAutenticacaoRepository.
//                    getQtdAssinaturas(
//                            autenticacao.getUnidadeGestora().getId(),
//                            autenticacao.getExercicio(),
//                            autenticacao.getRemessa()
//                    );
//            Integer chavesRemessaAnterior =admAutenticacaoRepository.getQtdAssinaturas(autenticacao.getUnidadeGestora().getId(), exercicio, remessa );
//            if (chavesRemessaAtual > 0   ){
//                throw new InvalitInsert("Já existe uma chave com assinatura !!");
//            }
//            if (chavesRemessaAnterior < 3 ||chavesRemessaAnterior  == null) {
//                throw new InvalitInsert("Remessa anterior pendente de assinatura!!");
//            }
//        }
        try {
            //cria a chave hash SHA-1
            MessageDigest digest = MessageDigest.getInstance("SHA-1");

            digest.reset();
            digest.update(value.getBytes("utf8"));
            sha1 = String.format("%040x", new BigInteger(1, digest.digest()));
            autenticacao.setIdSistema(29);
            autenticacao.setData(strToDate);
            autenticacao.setChave(sha1);
            autenticacao.setStatus(true);
            admAutenticacaoRepository.save(autenticacao);

        } catch (Exception e) {
            e.printStackTrace();
        }

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(autenticacao.getId()).toUri();
        return ResponseEntity.created(uri).body(autenticacao);

    }

    @CrossOrigin
    @GetMapping(path = {"/status/{Cnpj}/{Exercicio}/{Remessa}"})
    public Boolean findStatusChave(@PathVariable String Cnpj, @PathVariable Integer Exercicio, @PathVariable Integer Remessa) {
        Boolean status = admAutenticacaoRepository.getStatusChave(Cnpj, Exercicio, Remessa);
        return status;
    }



    @CrossOrigin
    @GetMapping(path = {"/qtdassinaturas/{Cnpj}/{Exercicio}/{Remessa}"})
    public Integer findQtdAssinaturas(@PathVariable String Cnpj, @PathVariable Integer Exercicio, @PathVariable Integer Remessa) {
        Integer status = admAutenticacaoRepository.getQtdAssinaturas(Cnpj, Exercicio, Remessa);
        return status;
    }

}