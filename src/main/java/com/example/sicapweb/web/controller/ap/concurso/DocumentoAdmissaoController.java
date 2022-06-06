package com.example.sicapweb.web.controller.ap.concurso;


import br.gov.to.tce.model.ap.concurso.EditalHomologacao;
import br.gov.to.tce.model.ap.concurso.documento.DocumentoAdmissao;
import br.gov.to.tce.model.ap.concurso.documento.DocumentoEditalHomologacao;
import com.example.sicapweb.repository.concurso.DocumentoAdmissaoRepository;
import com.example.sicapweb.repository.concurso.EditalAprovadoRepository;
import com.example.sicapweb.repository.concurso.EditalVagaRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigInteger;
import java.net.URI;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping({"/documentoConcursoAdmissão"})
public class DocumentoAdmissaoController extends DefaultController<DocumentoAdmissao> {

    @Autowired
    private DocumentoAdmissaoRepository documentoAdmissaoRepository;


    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        DocumentoAdmissao list = documentoAdmissaoRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping("/AddAprovadosSemNomeacao" )
    public ResponseEntity<List<DocumentoAdmissao>> AddDocumentoAdmissao(@RequestBody List<DocumentoAdmissao> lstdocumentoAdmissao) {

        for(Integer i= 0; i < lstdocumentoAdmissao.size(); i++){
            DocumentoAdmissao a = (DocumentoAdmissao) lstdocumentoAdmissao.get(i);
            documentoAdmissaoRepository.save(a);
        }

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(lstdocumentoAdmissao.getClass()).toUri();
        return ResponseEntity.created(uri).body(lstdocumentoAdmissao);
    }


    @CrossOrigin
    @Transactional
    @PostMapping("/upload/{id}" )
    public ResponseEntity<?> uploadDocumentoAdmissao(@RequestParam("file") MultipartFile file, @PathVariable BigInteger id) {
        DocumentoAdmissao documentoAdmissao = documentoAdmissaoRepository.findById(id);
        String idCastor = super.setCastorFile(file, "documentoAdmissao");
        documentoAdmissao.setDocumentoCastorId(idCastor);
        documentoAdmissao.setStatus(DocumentoAdmissao.Status.Informado.getValor());
        documentoAdmissao.setDataAnexouDocumento(new Date());
        documentoAdmissao.setUsuarioAnexouDocumento(User.getUser(documentoAdmissaoRepository.getRequest()).getUnidadeGestora().getId());
        documentoAdmissaoRepository.update(documentoAdmissao);
        return ResponseEntity.ok().body(idCastor);
    }
}
