package com.example.sicapweb.web.controller.ap.concurso;

import br.gov.to.tce.model.ap.concurso.ConcursoEnvio;
import br.gov.to.tce.model.ap.concurso.ProcessoAdmissao;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
    private ProcessoAdmissaoRepository processoAdmissaoRepository;

    @Autowired
    private AdmissaoRepository admissaoRepository;

    @Autowired
    private EditalRepository editalRepository;

    @Autowired
    private ConcursoEnvioRepository concursoEnvioRepository;

    @CrossOrigin
    @GetMapping(path="/aprovados/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<EditalAprovadoConcurso>> listaprov(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<EditalAprovadoConcurso> paginacaoUtil = editalAprovadoRepository.buscaPaginadaAprovados(pageable);
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
        PaginacaoUtil<AdmissaoEnvioAssRetorno> paginacaoUtil = processoAdmissaoRepository.buscarProcessos(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @GetMapping(path="/processosAguardandoAss/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<AdmissaoEnvioAssRetorno>> listaAProcessosAguardandoAss(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<AdmissaoEnvioAssRetorno> paginacaoUtil = processoAdmissaoRepository.buscarProcessosAguardandoAss(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }


    @CrossOrigin
    @GetMapping(path = {"/processos/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        ProcessoAdmissao list = processoAdmissaoRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping(path = "/processos")
    public ResponseEntity<ProcessoAdmissao> create(@RequestBody ProcessoAdmissao processoAdmissao) {

        processoAdmissao.setCnpjEmpresaOrganizadora(User.getUser(processoAdmissaoRepository.getRequest()).getUnidadeGestora().getId());
        List<ProcessoAdmissao> pa = processoAdmissaoRepository.GetEmAbertoByEdital(processoAdmissao.getEdital().getId());
        if (pa.size()>0) throw  new InvalitInsert("Existe um processo em aberto para esse edital!");
        List<ConcursoEnvio> enviofase2 = concursoEnvioRepository.buscarEnvioFAse2PorEdital(processoAdmissao.getEdital().getId());
        if (enviofase2.size()==0) throw  new InvalitInsert("Fase de homnogação não foi concluida!!");
        processoAdmissaoRepository.save(processoAdmissao);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(processoAdmissao.getClass()).toUri();
        return ResponseEntity.created(uri).body(processoAdmissao);
    }

    @CrossOrigin
    @Transactional
    @PutMapping(path = {"/processos/enviar/{id}"})
    public ResponseEntity<ProcessoAdmissao> Enviar(@PathVariable BigInteger id) {
        ProcessoAdmissao processoAdmissao = processoAdmissaoRepository.findById(id);
        if (processoAdmissao!= null) {
            List<Map<String,Object>> la = processoAdmissaoRepository.getValidInfoEnvio(processoAdmissao.getEdital().getId());
            for (Map<String,Object> hashmap : la){
                if (  !((Boolean)hashmap.get("valido"))  ) {
                    throw new InvalitInsert( ((String)hashmap.get("ocorrencia")) );
                }
            }
            processoAdmissao.setStatus(2);
            processoAdmissaoRepository.update(processoAdmissao);
        }

        return ResponseEntity.ok().body(processoAdmissao);
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
    public ResponseEntity<?> delete(@PathVariable BigInteger id) {
        ProcessoAdmissao processoAdmissao = processoAdmissaoRepository.findById(id);
        if (processoAdmissao!= null) {
            if(processoAdmissao.getStatus()==1){
                processoAdmissaoRepository.delete(id);
            }
        }
        return ResponseEntity.noContent().build();
    }
}
