package com.example.sicapweb.web.controller.ap.concurso;

import br.gov.to.tce.model.ap.concurso.ConcursoEnvio;
import br.gov.to.tce.model.ap.concurso.EditalHomologacao;

import com.example.sicapweb.exception.InvalitInsert;
import com.example.sicapweb.repository.concurso.ConcursoEnvioRepository;
import com.example.sicapweb.repository.concurso.DocumentoEditalHomologacaoRepository;
import com.example.sicapweb.repository.concurso.EditalHomologacaoRepository;
import com.example.sicapweb.repository.concurso.EditalRepository;
import com.example.sicapweb.repository.geral.AtoRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigInteger;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/concursoHomologacao"})
public class EditalHomologacaoController extends DefaultController<EditalHomologacao> {

    @Autowired
    private EditalHomologacaoRepository editalHomologacaoRepository;

    @Autowired
    private AtoRepository atoRepository;

    @Autowired
    private EditalRepository editalRepository;

    @Autowired
    private ConcursoEnvioRepository concursoEnvioRepository;

    @CrossOrigin
    @GetMapping(path = "/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<EditalHomologacao>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<EditalHomologacao> paginacaoUtil = editalHomologacaoRepository.buscaPaginada(pageable, searchParams, tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }


    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        EditalHomologacao list = editalHomologacaoRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping
    public ResponseEntity<EditalHomologacao> create(@RequestBody EditalHomologacao editalHomologacao) {
        editalHomologacao.setChave(editalHomologacaoRepository.buscarPrimeiraRemessa());
        //editalHomologacao.setEmpresaOrganizadora(empresaOrganizadoraRepository.buscaEmpresaPorCnpj(edital.getCnpjEmpresaOrganizadora()));
        editalHomologacaoRepository.save(editalHomologacao);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(editalHomologacao.getId()).toUri();
        return ResponseEntity.created(uri).body(editalHomologacao);
    }

    @CrossOrigin
    @Transactional
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
    public void update(@RequestBody EditalHomologacao editalHomologacao, @PathVariable BigInteger id) {
        var aux = concursoEnvioRepository.buscarEnvioFAse2PorEdital(id);
            
        if(!aux.isEmpty()){
            throw new InvalitInsert("não é possível editar um edital com  processo associado !!");
        }
        
        editalHomologacao.setChave(editalRepository.buscarPrimeiraRemessa());
        editalHomologacao.setAto(atoRepository.findById(editalHomologacao.getAto().getId()));
        editalHomologacao.setId(id);
        editalHomologacao.setEdital(editalRepository.findById(editalHomologacao.getEdital().getId()));
        editalHomologacao.setTipoAto(editalHomologacao.getAto().getTipoAto());
        editalHomologacaoRepository.update(editalHomologacao);
    

    }

    @CrossOrigin
    @GetMapping(path = "/EnviosFase2PorEditais/{ids}")
    public ResponseEntity<Map<String,Object>> getEnviosFase2PorEditais( @PathVariable List<Integer> ids) {
        Map<String,Object> retorno = new HashMap<String,Object>();
        for (Integer id : ids) {
            //var aux = documentoEditalHomologacaoRepository.buscarDocumentoEditalHomologacao(null,BigInteger.valueOf(id));            
            var aux = concursoEnvioRepository.buscarEnvioFAse2PorEdital(BigInteger.valueOf(id));

            if(!aux.isEmpty()){
                retorno.put(id.toString(), aux.get(0).getProcesso());
            }
        }
        return ResponseEntity.ok().body(retorno);
    }

    @CrossOrigin
    @GetMapping(path = {"/envio/{id}"})
    public ResponseEntity<?> findEnvioById(@PathVariable BigInteger id) {
        ConcursoEnvio list = concursoEnvioRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping(path = {"/envio"})
    public ResponseEntity<ConcursoEnvio>Enviar(@RequestBody ConcursoEnvio concursoEnvio){
        concursoEnvio.setFase(ConcursoEnvio.Fase.Homologacao.getValor());
        concursoEnvio.setStatus(ConcursoEnvio.Status.Aguardandoassinatura.getValor());
        List<ConcursoEnvio> le = concursoEnvioRepository.buscarEnvioFAse1PorEdital(concursoEnvio.getEdital().getId());
        if (le.size()>0 ){
            ConcursoEnvio c = le.get(0);
            concursoEnvio.setProcessoPai(c.getProcessoPai());
        }
        concursoEnvioRepository.save(concursoEnvio);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(concursoEnvio.getId()).toUri();
        return ResponseEntity.created(uri).body(concursoEnvio);
    }

    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public void delete(@PathVariable BigInteger id) {
        editalHomologacaoRepository.delete(id); 
    }
}
