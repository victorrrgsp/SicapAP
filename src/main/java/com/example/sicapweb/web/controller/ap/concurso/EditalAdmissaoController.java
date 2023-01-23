package com.example.sicapweb.web.controller.ap.concurso;

import br.gov.to.tce.model.ap.concurso.AdmissaoEnvio;
import br.gov.to.tce.model.ap.concurso.ConcursoEnvio;
import com.example.sicapweb.exception.InvalitInsert;
import com.example.sicapweb.model.EditalFinalizado;
import com.example.sicapweb.model.NomeacaoConcurso;
import com.example.sicapweb.model.AdmissaoEnvioAssRetorno;
import com.example.sicapweb.repository.concurso.*;
import com.example.sicapweb.model.EditalAprovadoConcurso;
import com.example.sicapweb.repository.movimentacaoDePessoal.AdmissaoRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.ValidationException;
import java.math.BigInteger;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@RestController
@RequestMapping({"/ConcessaoAdmissao"})
public class EditalAdmissaoController {

    @Autowired
    private EditalAprovadoRepository editalAprovadoRepository;

    @Autowired
    private AdmissaoEnvioRepository admissaoEnvioRepository;

    @Autowired
    private AdmissaoRepository admissaoRepository;

    @Autowired
    private EditalRepository editalRepository;

    @Autowired
    private ConcursoEnvioRepository concursoEnvioRepository;

    @CrossOrigin
    @GetMapping(path="/aprovados/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<EditalAprovadoConcurso>> listaprov(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<EditalAprovadoConcurso> paginacaoUtil = editalAprovadoRepository.buscaPaginadaAprovadosDto(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }


    @CrossOrigin
    @GetMapping(path="/editaisfinalizados/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<EditalFinalizado>> listaEditaisFinalizados(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<EditalFinalizado> paginacaoUtil = editalRepository.buscarEditaiFinalizados(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @GetMapping(path="/admissoes/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<NomeacaoConcurso>> listaAdmissoes(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<NomeacaoConcurso> paginacaoUtil = admissaoRepository.buscarAdmissoes(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @GetMapping(path="/processos/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<AdmissaoEnvioAssRetorno>> listaAProcessos(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<AdmissaoEnvioAssRetorno> paginacaoUtil = admissaoEnvioRepository.buscarProcessos(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @GetMapping(path="/processosAguardandoAss/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<AdmissaoEnvioAssRetorno>> listaAProcessosAguardandoAss(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<AdmissaoEnvioAssRetorno> paginacaoUtil = admissaoEnvioRepository.buscarProcessosAguardandoAss(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }


    @CrossOrigin
    @GetMapping(path = {"/processos/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        AdmissaoEnvio list = admissaoEnvioRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping(path = "/processos")
    public ResponseEntity<AdmissaoEnvio> create(@RequestBody AdmissaoEnvio admissaoEnvio) {
        admissaoEnvio.setCnpjUnidadeGestora(User.getUser(admissaoEnvioRepository.getRequest()).getUnidadeGestora().getId());
        Integer numeroEnvio = admissaoEnvioRepository.getLastNumeroEnvioByEdital(admissaoEnvio.getEdital().getId());
        admissaoEnvio.setNumeroEnvio(numeroEnvio==null?1:numeroEnvio );
        List<AdmissaoEnvio> pa = admissaoEnvioRepository.GetEmAbertoByEdital(admissaoEnvio.getEdital().getId());
        if (pa.size()>0) throw  new InvalitInsert("Existe um processo em aberto para esse edital!");
        List<ConcursoEnvio> enviofase2 = concursoEnvioRepository.buscarEnvioFAse2PorEdital(admissaoEnvio.getEdital().getId());
        if (enviofase2.size()==0) throw  new InvalitInsert("Fase de homnogação não foi concluida!!");
        admissaoEnvioRepository.save(admissaoEnvio);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(admissaoEnvio.getClass()).toUri();
        return ResponseEntity.created(uri).body(admissaoEnvio);
    }
    @CrossOrigin
    @GetMapping("/teste")
    public void teste() {
        
        List<Map<String,Object>> la ;
        // String ids[] = {
        //     "37463"
        //     ,"37502"
        //     ,"37597"
        //     ,"37608"
        //     ,"37618"
        //     ,"37626"
        //     ,"37631"
        //     ,"37643"
        //     ,"37646"
        //     ,"37652"
        //     ,"37898"
        //     ,"38050"
        //     ,"38053"
        //     ,"38057"
        //     ,"38100"
        //     ,"38108"
        //     ,"38111"
        //     ,"38193"
        //     ,"47010"
        //     ,"48313"
        //     ,"48945"
        //     ,"49896"
        //     ,"50241"
        // };
        // for (int i = 0; i < ids.length; i++) {
            
        // }
        la = admissaoEnvioRepository.getValidInfoEnvio(new BigInteger("37463"));
        la = admissaoEnvioRepository.getValidInfoEnvio(new BigInteger("37502"));
        la = admissaoEnvioRepository.getValidInfoEnvio(new BigInteger("37597"));
        la = admissaoEnvioRepository.getValidInfoEnvio(new BigInteger("37608"));
        la = admissaoEnvioRepository.getValidInfoEnvio(new BigInteger("37618"));
        la = admissaoEnvioRepository.getValidInfoEnvio(new BigInteger("37626"));
        la = admissaoEnvioRepository.getValidInfoEnvio(new BigInteger("37631"));
        la = admissaoEnvioRepository.getValidInfoEnvio(new BigInteger("37643"));
        la = admissaoEnvioRepository.getValidInfoEnvio(new BigInteger("37646"));
        la = admissaoEnvioRepository.getValidInfoEnvio(new BigInteger("37652"));
        la = admissaoEnvioRepository.getValidInfoEnvio(new BigInteger("37898"));
        la = admissaoEnvioRepository.getValidInfoEnvio(new BigInteger("38050"));
        la = admissaoEnvioRepository.getValidInfoEnvio(new BigInteger("38053"));
        la = admissaoEnvioRepository.getValidInfoEnvio(new BigInteger("38057"));
        la = admissaoEnvioRepository.getValidInfoEnvio(new BigInteger("38100"));
        la = admissaoEnvioRepository.getValidInfoEnvio(new BigInteger("38108"));
        la = admissaoEnvioRepository.getValidInfoEnvio(new BigInteger("38111"));
        la = admissaoEnvioRepository.getValidInfoEnvio(new BigInteger("38193"));
        la = admissaoEnvioRepository.getValidInfoEnvio(new BigInteger("47010"));
        la = admissaoEnvioRepository.getValidInfoEnvio(new BigInteger("48313"));
        la = admissaoEnvioRepository.getValidInfoEnvio(new BigInteger("48945"));
        la = admissaoEnvioRepository.getValidInfoEnvio(new BigInteger("49896"));
        la = admissaoEnvioRepository.getValidInfoEnvio(new BigInteger("50241"));

    }

    @CrossOrigin
    @Transactional
    @PutMapping(path = {"/processos/enviar/{id}"})
    public ResponseEntity<AdmissaoEnvio> Enviar(@PathVariable BigInteger id) {
        AdmissaoEnvio admissaoEnvio = admissaoEnvioRepository.findById(id);
        if (admissaoEnvio != null) {
            List<Map<String,Object>> la = admissaoEnvioRepository.getValidInfoEnvio(admissaoEnvio.getEdital().getId());
            if (la.size()>0) {
                for (Map<String, Object> hashmap : la) {
                    if (!((Boolean) hashmap.get("valido"))) {
                        throw new InvalitInsert(((String) hashmap.get("ocorrencia")));
                    }
                }
            }
            else {
                throw new InvalitInsert("não achou nenhum aprovado cadastrado!!");
            }
            admissaoEnvio.setStatus(AdmissaoEnvio.Status.aguardandoAssinatura.getValor());
            admissaoEnvioRepository.update(admissaoEnvio);
            return ResponseEntity.ok().body(admissaoEnvio);
        }
        else{
            throw new InvalitInsert("não achou envio!!");
        }


    }

    @CrossOrigin
    @GetMapping(path = {"/getInfoReciboAdmissao/{numproc}/{anoproc}"})
    public ResponseEntity<?> findInfoReciboAdmissao(@PathVariable Integer numproc, @PathVariable Integer anoproc) {
        List<Map<String, Object>> infoRecibo = editalAprovadoRepository.buscarInfoReciboAdmissao(numproc,anoproc);
        return ResponseEntity.ok().body(Objects.requireNonNullElse(infoRecibo, "seminfo"));
    }


    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/processos/{id}"})
    public void delete(@PathVariable BigInteger id) {
        AdmissaoEnvio admissaoEnvio = admissaoEnvioRepository.findById(id);
        if (admissaoEnvio==null)
            throw new ValidationException("Não achou envio paara excluir!!");
        else if  ( admissaoEnvio.getStatus()!=1)
            throw new ValidationException("Envio ja foi enviado!!");
        else
            admissaoEnvioRepository.delete(id);
             
    }
}
