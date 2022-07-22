package com.example.sicapweb.web.controller.ap.concurso;


import br.gov.to.tce.model.ap.concurso.ConcursoEnvio;
import br.gov.to.tce.model.ap.concurso.documento.DocumentoEdital;
import br.gov.to.tce.model.ap.concurso.Edital;
import com.example.sicapweb.model.EditalConcurso;
import com.example.sicapweb.model.Inciso;
import com.example.sicapweb.repository.concurso.ConcursoEnvioRepository;
import com.example.sicapweb.repository.concurso.DocumentoEditalRepository;
import com.example.sicapweb.repository.concurso.EditalRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigInteger;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/documentoConcursoEdital")
public class DocumentoConcursoEditalController extends DefaultController<DocumentoEdital> {

    @Autowired
    private EditalRepository editalRepository;

    @Autowired
    private DocumentoEditalRepository documentoEditalRepository;

    @Autowired
    private ConcursoEnvioRepository concursoEnvioRepository;


    @CrossOrigin
    @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<EditalConcurso>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<EditalConcurso> paginacaoUtil = editalRepository.buscaPaginadaEditais(pageable,searchParams,tipoParams);
        List<EditalConcurso> listE = paginacaoUtil.getRegistros();
        for(Integer i= 0; i < listE.size(); i++){
            Integer quantidadeDocumentos = documentoEditalRepository.findSituacao("documentoEdital","idEdital", listE.get(i).getId(), "'I','II','III','IV','V','VI','VII','VIII','IX','IX.I','X'");
            List<ConcursoEnvio> Lenvio= concursoEnvioRepository.buscarEnvioFAse1PorEdital(listE.get(i).getId());
           if (listE.get(i).getVeiculoPublicacao()==null  || listE.get(i).getDataPublicacao()==null || listE.get(i).getDataInicioInscricoes()==null || listE.get(i).getDataFimInscricoes() == null  || listE.get(i).getPrazoValidade()==null || listE.get(i).getCnpjEmpresaOrganizadora()==null ) {
                listE.get(i).setSituacao("Dados Incompletos");
               listE.get(i).setTooltip("Complete o cadastro dos campos vazios antes de prosseguir");
            }
           else if (
                   ( Integer.valueOf(listE.get(i).getNumeroEdital().substring(listE.get(i).getNumeroEdital().length()-4)) <1990
                           ||  Integer.valueOf(listE.get(i).getNumeroEdital().substring(listE.get(i).getNumeroEdital().length()-4)) > (LocalDateTime.now().getYear() +5) )

           ){
               listE.get(i).setSituacao("Dados Incompletos");
               listE.get(i).setTooltip("numero do edital deve ter o formato numero e ano. Exemplo:00042022");
            }
           else if( Lenvio.size()>0 ){
               ConcursoEnvio envio  = Lenvio.get(0);
               if (envio.getStatus() == ConcursoEnvio.Status.Enviado.getValor() ){
                   listE.get(i).setSituacao("Aguardando Assinatura");
               }
               else if (envio.getStatus() == ConcursoEnvio.Status.Finalizado.getValor() ){
                   listE.get(i).setSituacao("Concluido");
                   listE.get(i).setProcesso(envio.getProcesso());
               }
           }
            else  if(quantidadeDocumentos <  11) {
                listE.get(i).setSituacao("Pendente");
            } else if(quantidadeDocumentos >= 11){
                listE.get(i).setSituacao("Pendente");
            }
        }

        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @GetMapping(path = {"getSituacao/{id}"})
    public ResponseEntity<?> findSituacao(@PathVariable BigInteger id) {
        Integer situacao = documentoEditalRepository.findSituacao("DocumentoEdital","idEdital",id, "'I','II','III','IV','V','VI','VII','VIII','IX','IX.I','X'");
        return ResponseEntity.ok().body(situacao);
    }

    @CrossOrigin
    @Transactional
    @PostMapping("/upload/{inciso}/{id}")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file, @PathVariable String inciso, @PathVariable BigInteger id) {
        DocumentoEdital documentoEdital = new DocumentoEdital();
        documentoEdital.setEdital(editalRepository.findById(id));
        documentoEdital.setInciso(inciso);
        String idCastor = super.setCastorFile(file, "Edital");
        documentoEdital.setIdCastorFile(idCastor);
        documentoEdital.setStatus(DocumentoEdital.Status.Informado.getValor());
        documentoEditalRepository.save(documentoEdital);
        return ResponseEntity.ok().body(idCastor);
    }


    @CrossOrigin
    @GetMapping(path = {"getInciso/{id}"})
    public ResponseEntity<?> findInciso(@PathVariable BigInteger id) {
        List<Inciso> list = new ArrayList<>();
        list.add(new Inciso("I", "Ofício subscrito pela autoridade competente",
                "Ofício subscrito pela autoridade competente", "", "Sim"));
        list.add(new Inciso("II", "Justificativa para a abertura do concurso público",
                "Justificativa para a abertura do concurso público", "", "Sim"));
        list.add(new Inciso("III", "Demonstrativo de despesa do impacto orçamentário-financeiro",
                "Demonstrativo de despesa do impacto orçamentário-financeiro", "", "Sim"));
        list.add(new Inciso("IV", "Declaração de despesa da autorização para realização do consurso público",
                "Declaração de despesa da autorização para realização do consurso público", "", "Sim"));
        list.add(new Inciso("V", "Demonstrativo informando o percentual da despesa total com pessoal",
                "Demonstrativo informando o percentual da despesa total com pessoal", "", "Sim"));
        list.add(new Inciso("VI", "Ato expedido pela autoridade competente designando a comissão examinadora/julgadora",
                "Ato expedido pela autoridade competente designando a comissão examinadora/julgadora", "", "Sim"));
        list.add(new Inciso("VII", "Demonstrativo do quadro de pessoal efetivo e quantidade de vagas criadas por lei",
                "Demonstrativo do quadro de pessoal efetivo e quantidade de vagas criadas por lei", "", "Sim"));
        list.add(new Inciso("VIII", "Lei(s) de criação e/ou alteração dos cargos disponibilizados no Edital",
                "Lei(s) de criação e/ou alteração dos cargos disponibilizados no Edital", "", "Sim"));
        list.add(new Inciso("IX", "Documentos da contratação da entidade pomotora do certame",
                "Documentos da contratação da entidade pomotora do certame", "", "Sim"));
        list.add(new Inciso("IX.I", "Comprovante de cadastramento no SICAP-LCO",
                "Comprovante de cadastramento no SICAP-LCO", "", "Sim"));
        list.add(new Inciso("X", "Edital de abertura do consurso público e o respectivo comprovante de publicação",
                "Edital de abertura do consurso público e o respectivo comprovante de publicação", "", "Sim"));
        list.add(new Inciso("XI", "Demais editais do consurso público, quando houver",
                "Demais editais do consurso público, quando houver", "", "Não"));
        list.add(new Inciso("", "Outros",
                "Outros", "", "Não"));

        for (int i = 0; i < list.size(); i++){
            Integer existeArquivo = documentoEditalRepository.findAllInciso("documentoEdital","idEdital",id, list.get(i).getInciso());
            if (existeArquivo > 0){
                list.get(i).setStatus("Informado");
            }else{
                list.get(i).setStatus("Não informado");
            }
        }

        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/envio/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        ConcursoEnvio list = concursoEnvioRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping(path = {"/envio"})
    public ResponseEntity<ConcursoEnvio>Enviar(@RequestBody ConcursoEnvio concursoEnvio){
        concursoEnvio.setFase(ConcursoEnvio.Fase.Edital.getValor());
        concursoEnvio.setStatus(ConcursoEnvio.Status.Enviado.getValor());
        concursoEnvioRepository.save(concursoEnvio);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(concursoEnvio.getId()).toUri();
        return ResponseEntity.created(uri).body(concursoEnvio);
    }

    @CrossOrigin
    @GetMapping(path = {"anexos/{inciso}/{id}"})
    public ResponseEntity<?> findByDocumento(@PathVariable String inciso, @PathVariable BigInteger id) {
        DocumentoEdital list = documentoEditalRepository.buscarDocumentoEdital(inciso, id).get(0);
        return ResponseEntity.ok().body(list);
    }


    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public ResponseEntity<?> delete(@PathVariable BigInteger id) {
        documentoEditalRepository.delete(id);
        return ResponseEntity.noContent().build();
    }
}
