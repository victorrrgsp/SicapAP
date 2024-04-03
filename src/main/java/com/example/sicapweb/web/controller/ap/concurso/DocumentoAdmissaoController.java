package com.example.sicapweb.web.controller.ap.concurso;


import br.gov.to.tce.model.ap.concurso.EditalAprovado;
import br.gov.to.tce.model.ap.concurso.documento.DocumentoAdmissao;
import com.example.sicapweb.exception.InvalitInsert;
import com.example.sicapweb.repository.concurso.DocumentoAdmissaoRepository;
import com.example.sicapweb.repository.concurso.EditalAprovadoRepository;
import com.example.sicapweb.repository.concurso.EditalVagaRepository;
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
import java.util.List;

@RestController
@RequestMapping({"/documentoConcursoAdmissao"})
public class DocumentoAdmissaoController extends DefaultController<DocumentoAdmissao> {

    @Autowired
    private DocumentoAdmissaoRepository documentoAdmissaoRepository;
    @Autowired
    private EditalAprovadoRepository editalAprovadoRepository;
    @Autowired
    private EditalVagaRepository editalVagaRepository;


    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        DocumentoAdmissao list = documentoAdmissaoRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping("/AddAprovadosSemNomeacao" )
    public ResponseEntity<List<DocumentoAdmissao>> AddDocumentoAprovado(@RequestBody List<DocumentoAdmissao> lstdocumentoAdmissao) {
        for(Integer i= 0; i < lstdocumentoAdmissao.size(); i++){
            DocumentoAdmissao a = (DocumentoAdmissao) lstdocumentoAdmissao.get(i);

            if(a.getOpcaoDesistencia().equals(DocumentoAdmissao.OpcaoDesistencia.FINAL_DE_FILA.getValor()) ){

                if (a.getFinalFila() == null || a.getFinalFila() == 0) {
                    throw new InvalitInsert("Informe uma posicao de final da fila valida");
                }else{
                    var aprovado =(EditalAprovado) a.getEditalAprovado();
                    var editalvaga = editalVagaRepository.findById(aprovado.getEditalVaga().getId());
                    EditalAprovado mesmaclassifmesmavaga = editalAprovadoRepository.buscarAprovadoPorClassificacaoConc(editalvaga.getId(),a.getFinalFila()+"");
                    if (mesmaclassifmesmavaga!=null){
                        throw new InvalitInsert("Outro aprovado ja se encontra na mesma classificação e tipo de concorrencia para mesma vaga!");
                    }
                    else{
                        documentoAdmissaoRepository.save(a);
                        aprovado.setChave(editalAprovadoRepository.buscarPrimeiraRemessa());
                        aprovado.setClassificacao(a.getFinalFila()+"");
                        aprovado.setId(null);
                        aprovado.setEditalVaga(editalvaga);
                        editalAprovadoRepository.save(aprovado);
                    }
                }
            }else{
                documentoAdmissaoRepository.save(a);
            }
            
            
        }
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(lstdocumentoAdmissao.getClass()).toUri();
        return ResponseEntity.created(uri).body(lstdocumentoAdmissao);
    }

    @CrossOrigin
    @Transactional
    @PostMapping("/AddNomeacoes" )
    public ResponseEntity<List<DocumentoAdmissao>> AddDocumentoAdmissao(@RequestBody List<DocumentoAdmissao> lstdocumentoAdmissao) {
        for(Integer i= 0; i < lstdocumentoAdmissao.size(); i++){
            DocumentoAdmissao a = (DocumentoAdmissao) lstdocumentoAdmissao.get(i);
            documentoAdmissaoRepository.save(a);
        }
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(lstdocumentoAdmissao.getClass()).toUri();
        return ResponseEntity.created(uri).body(lstdocumentoAdmissao);
    }


    @CrossOrigin
    @GetMapping(path = {"/getOocumentosAprovados/{idproc}/pagination"})
    public ResponseEntity<PaginacaoUtil<DocumentoAdmissao>>  getOocumentosAprovados(Pageable pageable, @PathVariable BigInteger idproc) {
        PaginacaoUtil<DocumentoAdmissao> paginacaoUtil = documentoAdmissaoRepository.buscaPaginadaAprovadosSemAdmissao(pageable,idproc) ;
        return ResponseEntity.ok().body(paginacaoUtil);
    }


    @CrossOrigin
    @GetMapping(path = {"/getOocumentosAdmissoes/{idproc}/pagination"})
    public ResponseEntity<PaginacaoUtil<DocumentoAdmissao>> getOocumentosAmissoes(Pageable pageable, @PathVariable BigInteger idproc) {
        PaginacaoUtil<DocumentoAdmissao> paginacaoUtil = documentoAdmissaoRepository.buscaPaginadaAprovadosComAdmissao(pageable,idproc) ;
        return ResponseEntity.ok().body(paginacaoUtil);
    }


    @CrossOrigin
    @Transactional
    @PutMapping("/upload/{id}" )
    public ResponseEntity<?> uploadDocumentoAdmissao(@RequestParam("file") MultipartFile file, @PathVariable BigInteger id) {

        // Verificar o tipo de arquivo
        getFileType(file);
        DocumentoAdmissao documentoAdmissao = documentoAdmissaoRepository.findById(id);
        String idCastor=null;
        if (documentoAdmissao != null ){
             idCastor = super.setCastorFile(file, "documentoAdmissao");
             if (idCastor == null) throw  new InvalitInsert("nÃ£o conseguiu gravar o file castor. Entre em contato com o TCE!!");
            documentoAdmissao.setDocumentoCastorId(idCastor);
            documentoAdmissao.setStatus(DocumentoAdmissao.Status.INFORMADO.getValor());
            documentoAdmissao.setData_cr(LocalDateTime.now());
            ServletRequestAttributes getIp = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            documentoAdmissao.setIp_cr(getIp.getRequest().getRemoteAddr());
            documentoAdmissao.setUsuario_cr(user.getUser(documentoAdmissaoRepository.getRequest()).getUserName());
            documentoAdmissaoRepository.update(documentoAdmissao);
        }
        return ResponseEntity.ok().body(idCastor);
    }

    @CrossOrigin
    @Transactional
    @PutMapping("/upload/excluir/{id}" )
    public void ExcluirDocumentoAdmissao( @PathVariable BigInteger id) {
        DocumentoAdmissao documentoAdmissao = documentoAdmissaoRepository.findById(id);
        if (documentoAdmissao != null ){
            documentoAdmissao.setStatus(DocumentoAdmissao.Status.EXCLUIDO_DOCUMENTO.getValor());
            ServletRequestAttributes getIp = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            documentoAdmissao.setIp_altr(getIp.getRequest().getRemoteAddr());
            documentoAdmissao.setUsuario_altr(user.getUser(documentoAdmissaoRepository.getRequest()).getUserName());
            documentoAdmissao.setData_altr(LocalDateTime.now());
            documentoAdmissaoRepository.update(documentoAdmissao);
            DocumentoAdmissao novo = new DocumentoAdmissao();
            novo.setDocumentoCastorId(null);
            novo.setStatus(DocumentoAdmissao.Status.NAO_INFORMADO.getValor());
            novo.setAdmissao(documentoAdmissao.getAdmissao());
            novo.setEditalAprovado(documentoAdmissao.getEditalAprovado());
            novo.setOpcaoDesistencia(documentoAdmissao.getOpcaoDesistencia());
            novo.setAdmissaoEnvio(documentoAdmissao.getAdmissaoEnvio());
            documentoAdmissaoRepository.save(novo);
        }
    }


    @CrossOrigin
    @Transactional
    @PutMapping("/excluir/{id}" )
    public ResponseEntity<?> ExcluiAprovado( @PathVariable BigInteger id) {
        DocumentoAdmissao documentoAdmissao = documentoAdmissaoRepository.findById(id);
        if (documentoAdmissao != null ){
            documentoAdmissao.setDocumentoCastorId(null);
            documentoAdmissao.setStatus(DocumentoAdmissao.Status.EXCLUIDO_APROVADO.getValor());
            documentoAdmissao.setData_altr(LocalDateTime.now());
            ServletRequestAttributes getIp = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            documentoAdmissao.setIp_altr(getIp.getRequest().getRemoteAddr());
            documentoAdmissao.setUsuario_altr(user.getUser(documentoAdmissaoRepository.getRequest()).getUserName());
            documentoAdmissaoRepository.update(documentoAdmissao);
        }
        return ResponseEntity.ok().body(documentoAdmissao);
    }

    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public void delete(@PathVariable BigInteger id) {
        DocumentoAdmissao documentoAdmissao = documentoAdmissaoRepository.findById(id);
        if(documentoAdmissao.getOpcaoDesistencia() != null && documentoAdmissao.getOpcaoDesistencia() == 5 ){
            editalAprovadoRepository.delete(documentoAdmissao.getEditalAprovado().getId());
        }
        if (documentoAdmissao.getDocumentoCastorId()!=null ) throw new RuntimeException("remova  primeiro o documento antes de excluir a aprovado!!");

        documentoAdmissaoRepository.delete(id); 
    }

}
