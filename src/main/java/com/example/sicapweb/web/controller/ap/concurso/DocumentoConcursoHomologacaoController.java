package com.example.sicapweb.web.controller.ap.concurso;


import br.gov.to.tce.model.ap.concurso.EditalHomologacao;
import br.gov.to.tce.model.ap.concurso.documento.DocumentoEditalHomologacao;
import com.example.sicapweb.model.EditalConcurso;
import com.example.sicapweb.model.EditalHomologaConcurso;
import com.example.sicapweb.model.Inciso;
import com.example.sicapweb.repository.concurso.DocumentoEditalHomologacaoRepository;
import com.example.sicapweb.repository.concurso.EditalHomologacaoRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/documentoConcursoHomologacao")
public class DocumentoConcursoHomologacaoController extends DefaultController<EditalHomologacao> {

    @Autowired
    private EditalHomologacaoRepository editalHomologacaoRepository;

    @Autowired
    private DocumentoEditalHomologacaoRepository documentoEditalHomologacaoRepository;

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
            Integer quantidadeDocumentos = editalHomologacaoRepository.findSituacao("DocumentoEditalHomologacao","idEditalHomologacao", listE.get(i).getId(), "'XII','XIII','XIV','XV','XVI'");
            if (listE.get(i).getVeiculoPublicacao()==null  || listE.get(i).getDataHomologacao()==null || listE.get(i).getAto()==null || listE.get(i).getEdital()==null  ) {
                listE.get(i).setSituacao("Dados Incompletos");
            }
            else
            if(quantidadeDocumentos <  5) {
                listE.get(i).setSituacao("Pendente");
            } else if(quantidadeDocumentos == 5){
                listE.get(i).setSituacao("Aguardando Assinatura");
            }
        }
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @GetMapping(path = {"getSituacao/{id}"})
    public ResponseEntity<?> findSituacao(@PathVariable BigInteger id) {
        Integer situacao = documentoEditalHomologacaoRepository.findSituacao("DocumentoEditalHomologacao","idEditalHomologacao",id, "'XII','XIII','XIV','XV','XVI'");
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
        documentoEditalHomologacaoRepository.save(documentoEditalHomologacao);
        return ResponseEntity.ok().body(idCastor);
    }

    @CrossOrigin
    @GetMapping(path = {"getInciso/{id}"})
    public ResponseEntity<?> findInciso(@PathVariable BigInteger id) {
        List<Inciso> list = new ArrayList<>();
        list.add(new Inciso("XI", "Demais editais do concurso público, quando houver",
                "Demais editais do concurso público, quando houver", "", "Não"));
        list.add(new Inciso("XII ", "Relação de candidatos inscritos para o concurso público",
                "Relação de candidatos inscritos para o concurso público", "", "Sim"));
        list.add(new Inciso("XIII", "Lista de presença dos candidatos",
                "Lista de presença dos candidatos", "", "Sim"));
        list.add(new Inciso("XIV", "Ata ou relatório final dos trabalhos realizados na promoção do concurso público",
                "Ata ou relatório final dos trabalhos realizados na promoção do concurso público", "", "Sim"));
        list.add(new Inciso("XV", "Ato de homologação do resultado do concurso público e lista de aprovados",
                "Ato de homologação do resultado do concurso público e lista de aprovados", "", "Sim"));
        list.add(new Inciso("XVI", "Demais documentos exigidos em legislação específica de concurso público",
                "Demais documentos exigidos em legislação específica de concurso público", "", "Sim"));
        list.add(new Inciso("", "Outros",
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
}
