package com.example.sicapweb.web.controller.ap.concurso;

import br.gov.to.tce.model.ap.concurso.ConcursoEnvio;
import br.gov.to.tce.model.ap.concurso.Edital;
import com.example.sicapweb.repository.concurso.ConcursoEnvioRepository;
import com.example.sicapweb.repository.concurso.EditalRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigInteger;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import  com.example.sicapweb.exception.InvalitInsert;
@RestController
@RequestMapping({"/concursoEdital"})
public class EditalController extends DefaultController<Edital> {

    @Autowired
    private EditalRepository editalRepository;

    @Autowired
    private ConcursoEnvioRepository concursoEnvioRepository;

    @CrossOrigin
    @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<Edital>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<Edital> paginacaoUtil = editalRepository.buscaPaginada(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }
    @CrossOrigin
    @GetMapping(path = "/EnviosFase1PorEditais/{ids}")
    public ResponseEntity<Map<String,Object>> getEnviosFase1PorEditais( @PathVariable List<Integer> ids) {
        Map<String,Object> retorno = new HashMap<String,Object>();
        for (Integer id : ids) {
            //var aux = documentoEditalHomologacaoRepository.buscarDocumentoEditalHomologacao(null,BigInteger.valueOf(id));            
            var aux = concursoEnvioRepository.buscarEnvioFAse1PorEdital(BigInteger.valueOf(id));
            
            if(!aux.isEmpty()){
                retorno.put(id.toString(), aux);
            }
        }
        return ResponseEntity.ok().body(retorno);
    }


    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<Edital>> findAll() {
        List<Edital> list = editalRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/homologar"})
    public ResponseEntity<List<Edital>> findAllHomologar() {
        List<Edital> list = editalRepository.findAllAHomologar();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        Edital list = editalRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/editaisNaoHomologados"})
    public ResponseEntity<List<Edital>> findEditalNaoHomologado() {
        List<Edital> list = editalRepository.buscarEditaisNaoHomologados();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping
    public ResponseEntity<Edital> create(@RequestBody Edital edital) {
        edital.setInfoRemessa(editalRepository.buscarPrimeiraRemessa());
        Edital e =editalRepository.buscarEditalPorNumero(edital.getNumeroEdital(),edital.getComplementoNumero());

        if (e ==null) {
            if  ( Integer.valueOf(edital.getNumeroEdital().substring(edital.getNumeroEdital().length()-4)) <1990 ||  Integer.valueOf(edital.getNumeroEdital().substring(edital.getNumeroEdital().length()-4)) > (LocalDateTime.now().getYear() +5) ) {
                throw new InvalitInsert("não é um número de Edital valido. Os ultinmos 4 digitos correspondem ao ano do edital !!");
            }
            if (edital.getNumeroEdital() == null || edital.getCnpjEmpresaOrganizadora()==null || edital.getTipoEdital() ==null || edital.getDataPublicacao()==null ||edital.getDataInicioInscricoes()==null || edital.getDataFimInscricoes() == null || edital.getPrazoValidade() == null ||edital.getVeiculoPublicacao() == null ){
                throw new InvalitInsert("favor envie todos os campos obrigatorios preenchidos!!");

            } else if (edital.getNumeroEdital().isEmpty() || edital.getCnpjEmpresaOrganizadora().isEmpty() || edital.getPrazoValidade().isEmpty() || edital.getVeiculoPublicacao().isEmpty()) {
                throw new InvalitInsert("favor envie todos os campos obrigatorios preenchidos!!");
            }

            editalRepository.save(edital);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(edital.getId()).toUri();
            return ResponseEntity.created(uri).body(edital);
        } else {

        throw new InvalitInsert("ja existe o edital!!");

        }


    }

    @CrossOrigin
    @Transactional
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
    public void update(@RequestBody Edital edital, @PathVariable BigInteger id) {
        var aux = concursoEnvioRepository.buscarEnvioFAse1PorEdital(id);
            
        if(!aux.isEmpty()){
            throw new InvalitInsert("não é possível editar um edital com  processo associado !!");
        }

        edital.setInfoRemessa(editalRepository.buscarPrimeiraRemessa());
        edital.setId(id);
        Edital e =editalRepository.buscarEditalPorNumero(edital.getNumeroEdital(),edital.getComplementoNumero());

        if ((e ==null) || edital.getId() == id) {
            if  ( Integer.valueOf(edital.getNumeroEdital().substring(edital.getNumeroEdital().length()-4)) <1990 ||  Integer.valueOf(edital.getNumeroEdital().substring(edital.getNumeroEdital().length()-4)) > (LocalDateTime.now().getYear() +5) ) {
                throw new InvalitInsert("não é um numero de Edital valido. Os ultinmos 4 digitos correspondem ao ano do edital !!");
            }
            ConcursoEnvio envio = concursoEnvioRepository.buscarEnvioFAse1PorEditalassinado(id);
            if(envio != null){
                if (envio.getStatus()==ConcursoEnvio.Status.Aguardandoassinatura.getValor()) throw new InvalitInsert("Edital não pode ser alterado pois ja foi enviado processo no econtas !!");
            }
            editalRepository.update(edital);

        } else {
            throw new InvalitInsert("ja existe o edital!!");
        }


    }


    @CrossOrigin
    @GetMapping(path = {"/getInfoReciboEdital/{numproc}/{anoproc}"})
    public ResponseEntity<?> findInfoReciboEdital(@PathVariable Integer numproc, @PathVariable Integer anoproc) {
        List<Map<String, Object>> infoRecibo = editalRepository.buscarInfoReciboEdital(numproc,anoproc);
        return ResponseEntity.ok().body(Objects.requireNonNullElse(infoRecibo, "seminfo"));
    }


    @CrossOrigin
    @PostMapping(path = {"/VincularProcessoPreExistente/{id}"})
    public ResponseEntity<?> VincularProcessoPreExistente(@PathVariable BigInteger id,@RequestBody String processo) throws JsonProcessingException {
        Edital edital = editalRepository.findById(id);
        ConcursoEnvio novo= new ConcursoEnvio();
        novo.setEdital(edital);
        novo.setFase(ConcursoEnvio.Fase.Edital.getValor());

        if ( processo != null  ){
            try{
                JsonNode requestJson = new ObjectMapper().readTree(processo);
                String proc = requestJson.get("processo").asText();
                if (proc.length()>0){
                    novo.setProcesso(proc);
                    novo.setStatus(ConcursoEnvio.Status.Desambiguado.getValor());
                }else{
                    novo.setStatus(ConcursoEnvio.Status.Pendente.getValor());
                }
            }catch (JsonProcessingException e){
                throw new RuntimeException("Formato invalido de payload!");
            }
        }
        else {
            novo.setStatus(ConcursoEnvio.Status.Pendente.getValor());
        }

        novo.setDataEnvio(LocalDateTime.now());
        concursoEnvioRepository.save(novo);
        return ResponseEntity.ok().body(novo);
    }

    @CrossOrigin
    @PutMapping(path = {"/NaoVincularProcessoPreExistente/{id}"})
    public ResponseEntity<?>  NaoVincularProcessoPreExistente(@PathVariable BigInteger id) {
        Edital edital = editalRepository.findById(id);
        ConcursoEnvio novo= new ConcursoEnvio();
        novo.setEdital(edital);
        novo.setFase(ConcursoEnvio.Fase.Edital.getValor());
        novo.setStatus(ConcursoEnvio.Status.Pendente.getValor());
        concursoEnvioRepository.save(novo);
        return ResponseEntity.ok().body(novo);
    }



    @CrossOrigin
    @GetMapping(path = {"/getProcessosEcontas/{id}"})
    public ResponseEntity<?> getProcessosEcontaporEdital(@PathVariable BigInteger id) {
        Edital edital = editalRepository.findById(id);
        List<Map<String, Integer>> htp;
        if (edital!= null ){
            Integer numEdital = Integer.valueOf(edital.getNumeroEdital().substring(0, edital.getNumeroEdital().length() - 4));
            Integer anoEdital = Integer.valueOf(edital.getNumeroEdital().substring(edital.getNumeroEdital().length() - 4));
             htp =  concursoEnvioRepository.getProcessosEcontas(numEdital,anoEdital, redisConnect.getUser(concursoEnvioRepository.getRequest()).getUnidadeGestora().getId());
        }
        else { throw new RuntimeException("não encontrou edital!!");}


        return ResponseEntity.ok().body(htp);
    }



    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public void delete(@PathVariable BigInteger id) {
        editalRepository.delete(id); 
    }

    @CrossOrigin
    @Transactional
    @GetMapping(path = {"/getenvios"})
    public ResponseEntity<List<HashMap<String,Object>>> BuscaTotal(
            @RequestParam(required = false) List<String> Ug ,
            @RequestParam(required = false) List<Integer> Fase ,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataInico,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataFim,
            @RequestParam(required = false) Integer Status
    ){
        Fase = ifNull(Fase, Arrays.asList(new Integer[]{-1}));
        Ug           = ifNull(Ug, Arrays.asList(new String[]{"todos"}));
        List<HashMap<String,Object>> Listatotal = concursoEnvioRepository.buscaTotalNaoPaginada(Ug,Fase,dataInico,dataFim,Status);
        return ResponseEntity.ok().body(Listatotal);
    }

    <T> T ifNull(T obj, T seNull){
        return obj = obj == null?seNull:obj;
    }


}
