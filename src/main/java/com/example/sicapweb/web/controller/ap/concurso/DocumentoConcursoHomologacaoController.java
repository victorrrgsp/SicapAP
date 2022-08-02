package com.example.sicapweb.web.controller.ap.concurso;


import br.gov.to.tce.model.ap.concurso.ConcursoEnvio;
import br.gov.to.tce.model.ap.concurso.EditalHomologacao;
import br.gov.to.tce.model.ap.concurso.documento.DocumentoEdital;
import br.gov.to.tce.model.ap.concurso.documento.DocumentoEditalHomologacao;
import com.example.sicapweb.exception.InvalitInsert;
import com.example.sicapweb.model.EditalConcurso;
import com.example.sicapweb.model.EditalHomologaConcurso;
import com.example.sicapweb.model.Inciso;
import com.example.sicapweb.repository.concurso.ConcursoEnvioRepository;
import com.example.sicapweb.repository.concurso.DocumentoEditalHomologacaoRepository;
import com.example.sicapweb.repository.concurso.EditalHomologacaoRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigInteger;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/documentoConcursoHomologacao")
public class DocumentoConcursoHomologacaoController extends DefaultController<EditalHomologacao> {

    @Autowired
    private EditalHomologacaoRepository editalHomologacaoRepository;

    @Autowired
    private DocumentoEditalHomologacaoRepository documentoEditalHomologacaoRepository;

    @Autowired
    private ConcursoEnvioRepository concursoEnvioRepository;

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<EditalHomologacao>> findAll() {
        List<EditalHomologacao> list = editalHomologacaoRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<EditalHomologaConcurso>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<EditalHomologaConcurso> paginacaoUtil = editalHomologacaoRepository.buscaPaginadaEditaisHomologa(pageable,searchParams,tipoParams);
        List<EditalHomologaConcurso> listE = paginacaoUtil.getRegistros();
        for(Integer i= 0; i < listE.size(); i++){
            Integer quantidadeDocumentos = editalHomologacaoRepository.findSituacao("DocumentoEditalHomologacao","idEditalHomologacao", listE.get(i).getId(), "'XII','XIII','XIV','XV'");
            List<ConcursoEnvio> Lenvio= concursoEnvioRepository.buscarEnvioFAse2PorEdital(listE.get(i).getEdital().getId());
            if (listE.get(i).getVeiculoPublicacao()==null  || listE.get(i).getDataHomologacao()==null || listE.get(i).getAto()==null || listE.get(i).getEdital()==null  ) {
                listE.get(i).setSituacao("Dados Incompletos");
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
            else {
                listE.get(i).setSituacao("Pendente");
            }
        }
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @GetMapping(path = {"getSituacao/{id}"})
    public ResponseEntity<?> findSituacao(@PathVariable BigInteger id) {
        Integer situacao = documentoEditalHomologacaoRepository.findSituacao("DocumentoEditalHomologacao","idEditalHomologacao",id, "'XII','XIII','XIV','XV'");
        return ResponseEntity.ok().body(situacao);
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        EditalHomologacao list = editalHomologacaoRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping("/upload/{inciso}/{id}")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file, @PathVariable String inciso, @PathVariable BigInteger id) {
        DocumentoEditalHomologacao documentoEditalHomologacao = new DocumentoEditalHomologacao();
        documentoEditalHomologacao.setEditalHomologacao(editalHomologacaoRepository.findById(id));
        documentoEditalHomologacao.setInciso(inciso);
        String idCastor = super.setCastorFile(file, "EditalHomologacao");
        documentoEditalHomologacao.setIdCastorFile(idCastor);
        documentoEditalHomologacao.setStatus(DocumentoEditalHomologacao.Status.Informado.getValor());
        documentoEditalHomologacao.setData_cr(LocalDateTime.now());
        ServletRequestAttributes getIp = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        documentoEditalHomologacao.setIp_cr(getIp.getRequest().getRemoteAddr());
        documentoEditalHomologacao.setUsuario_cr(User.getUser(editalHomologacaoRepository.getRequest()).getUserName());
        documentoEditalHomologacaoRepository.save(documentoEditalHomologacao);
        return ResponseEntity.ok().body(idCastor);
    }

    @CrossOrigin
    @GetMapping(path = {"getInciso/{id}"})
    public ResponseEntity<?> findInciso(@PathVariable BigInteger id) {
        List<Inciso> list = new ArrayList<>();
        list.add(new Inciso("XI", "Demais editais do concurso público, quando houver",
                "Demais editais do concurso público, quando houver", "", "Não"));
        list.add(new Inciso("XII", "Relação de candidatos inscritos para o concurso público",
                "Relação de candidatos inscritos para o concurso público", "", "Sim"));
        list.add(new Inciso("XIII", "Lista de presença dos candidatos",
                "Lista de presença dos candidatos", "", "Sim"));
        list.add(new Inciso("XIV", "Ata ou relatório final dos trabalhos realizados na promoção do concurso público",
                "Ata ou relatório final dos trabalhos realizados na promoção do concurso público", "", "Sim"));
        list.add(new Inciso("XV", "Ato de homologação do resultado do concurso público e lista de aprovados",
                "Ato de homologação do resultado do concurso público e lista de aprovados", "", "Sim"));
        list.add(new Inciso("XVI", "Demais documentos exigidos em legislação específica de concurso público",
                "Demais documentos exigidos em legislação específica de concurso público", "", "Não"));
        list.add(new Inciso("sem", "Outros",
                "Outros", "", "Não"));

        for (int i = 0; i < list.size(); i++){
            Integer existeArquivo = documentoEditalHomologacaoRepository.findAllInciso("documentoEditalHomologacao","idEditalHomologacao",id, list.get(i).getInciso());
            if (existeArquivo > 0){
                list.get(i).setStatus("Informado");
            }else{
                list.get(i).setStatus("Não informado");
            }
        }

        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"anexos/{inciso}/{id}"})
    public ResponseEntity<?> findByDocumento(@PathVariable String inciso, @PathVariable BigInteger id) {
        DocumentoEditalHomologacao list = documentoEditalHomologacaoRepository.buscarDocumentoEditalHomologacao(inciso, id).get(0);
        return ResponseEntity.ok().body(list);
    }


    @CrossOrigin
    @Transactional
    @PutMapping("/anexos/excluir/{id}" )
    public ResponseEntity<?> ExcluirDocumento( @PathVariable BigInteger id) {
        DocumentoEditalHomologacao documentoEditalHomologacao = documentoEditalHomologacaoRepository.findById(id);
        if (documentoEditalHomologacao != null ){
            documentoEditalHomologacao.setStatus(DocumentoEditalHomologacao.Status.NaoInformado.getValor());
            ServletRequestAttributes getIp = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            documentoEditalHomologacao.setIp_altr(getIp.getRequest().getRemoteAddr());
            documentoEditalHomologacao.setUsuario_altr(User.getUser(documentoEditalHomologacaoRepository.getRequest()).getUserName());
            documentoEditalHomologacao.setData_altr(LocalDateTime.now());
            documentoEditalHomologacaoRepository.update(documentoEditalHomologacao);
        }
        return ResponseEntity.noContent().build();
    }

    @CrossOrigin
    @Transactional
    @PostMapping(path = {"/envio"})
    public ResponseEntity<ConcursoEnvio>Enviar(@RequestBody ConcursoEnvio concursoEnvio){
        Integer situacao=0;
         situacao = documentoEditalHomologacaoRepository.findSituacaobyIdEdital(concursoEnvio.getEdital().getId(), "'XII','XIII','XIV','XV'");
         if (situacao < 4) throw  new InvalitInsert("Anexe todos os documentos obrigatorios!!");
        concursoEnvio.setFase(ConcursoEnvio.Fase.Homologacao.getValor());
        concursoEnvio.setStatus(ConcursoEnvio.Status.Enviado.getValor());
        ConcursoEnvio envioPai =  concursoEnvioRepository.buscarEnvioFAse1PorEditalassinado(concursoEnvio.getEdital().getId());
        if (envioPai !=null) concursoEnvio.setProcessoPai(envioPai.getProcesso());
        concursoEnvioRepository.save(concursoEnvio);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(concursoEnvio.getId()).toUri();
        return ResponseEntity.created(uri).body(concursoEnvio);
    }



    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public ResponseEntity<?> delete(@PathVariable BigInteger id) {
        documentoEditalHomologacaoRepository.delete(id);
        return ResponseEntity.noContent().build();
    }
}
