package com.example.sicapweb.web.controller.ap.concessao;

import br.gov.to.tce.model.adm.AdmEnvio;
import br.gov.to.tce.model.ap.concessoes.DocumentoAposentadoria;
import br.gov.to.tce.model.ap.pessoal.Aposentadoria;
import br.gov.to.tce.util.Date;
import com.example.sicapweb.model.Inciso;
import com.example.sicapweb.model.dto.AposentadoriaDTO;
import com.example.sicapweb.repository.concessao.AdmEnvioRepository;
import com.example.sicapweb.repository.concessao.AposentadoriaRepository;
import com.example.sicapweb.repository.concessao.DocumentoAposentadoriaRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/documentoConcessaoReforma")
public class ConcessaoReformaController extends DefaultController<DocumentoAposentadoria> {

    @Autowired
    private AposentadoriaRepository aposentadoriaRepository;

    @Autowired
    private DocumentoAposentadoriaRepository documentoAposentadoriaRepository;

    @Autowired
    private AdmEnvioRepository admEnvioRepository;

    HashMap<String, Object> aposentadoria = new HashMap<String, Object>();

    public class AposentadoriaDocumento {
        private Aposentadoria aposentadoria;

        private String situacao;

        public Aposentadoria getAposentadoria() {
            return aposentadoria;
        }

        public void setAposentadoria(Aposentadoria aposentadoria) {
            this.aposentadoria = aposentadoria;
        }

        public String getSituacao() {
            return situacao;
        }

        public void setSituacao(String situacao) {
            this.situacao = situacao;
        }
    }

    @CrossOrigin
    @GetMapping(path = "/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<AposentadoriaDTO>> listReservas(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<AposentadoriaDTO> paginacaoUtil = aposentadoriaRepository.buscaPaginadaPorTipo(pageable, searchParams, tipoParams, Aposentadoria.TipoAposentadoria.Reforma.getValor());
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        Aposentadoria list = aposentadoriaRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping("/upload/{inciso}/{id}")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file, @PathVariable String inciso, @PathVariable BigInteger id, @RequestParam(value = "descricao", required = false) String descricao) throws UnknownHostException {
        
        // Verificar o tipo de arquivo
        getFileType(file);

        DocumentoAposentadoria documentoAposentadoria = new DocumentoAposentadoria();
        documentoAposentadoria.setAposentadoria(aposentadoriaRepository.findById(id));
        documentoAposentadoria.setInciso(inciso);
        String idCastor = super.setCastorFile(file, "AposentadoriaReforma");
        documentoAposentadoria.setIdCastorFile(idCastor);
        documentoAposentadoria.setStatus(DocumentoAposentadoria.Status.Informado.getValor());
        documentoAposentadoria.setDescricao(descricao);
        documentoAposentadoria.setReforma("S");
        documentoAposentadoria.setIdCargo(User.getUser(aposentadoriaRepository.getRequest()).getCargo().getValor());
        documentoAposentadoria.setCpfUsuario(User.getUser(aposentadoriaRepository.getRequest()).getCpf());
        documentoAposentadoria.setIpUsuario(InetAddress.getLocalHost().getHostAddress());
        documentoAposentadoria.setDataUpload(new Date());
        documentoAposentadoriaRepository.save(documentoAposentadoria);
        return ResponseEntity.ok().body(idCastor);
    }

    @CrossOrigin
    @GetMapping(path = {"getDocumentos"})
    public ResponseEntity<?> findAllDocumentos() {
        List<Aposentadoria> list = aposentadoriaRepository.buscarAposentadoriaPorTipo(6);
        AposentadoriaDocumento situacao = new AposentadoriaDocumento();
        for (Integer i = 0; i < list.size(); i++) {
            Integer quantidadeDocumentos = documentoAposentadoriaRepository.findSituacao("documentoAposentadoria", "idAposentadoria", list.get(i).getId(), "'I', 'II', 'III', 'IV', 'V', 'VI', 'VII', 'VIII', 'IX', 'X'", "N", "S", "N", "N");
            if (quantidadeDocumentos == 0) {
                situacao.setAposentadoria(list.get(i));
                situacao.setSituacao("Pendente");
            } else if (quantidadeDocumentos == 10) {
                situacao.setAposentadoria(list.get(i));
                situacao.setSituacao("Concluído");
            } else {
                situacao.setAposentadoria(list.get(i));
                situacao.setSituacao("Aguardando verificação");
            }
            aposentadoria.put("Aposentadoria", situacao);
        }

        return ResponseEntity.ok().body(aposentadoria);
    }

    @CrossOrigin
    @GetMapping(path = {"getSituacao/{id}"})
    public ResponseEntity<?> findSituacao(@PathVariable BigInteger id) {
        Integer situacao = documentoAposentadoriaRepository.findSituacao("documentoAposentadoria", "idAposentadoria", id, "'I', 'II', 'III', 'IV', 'V', 'VI', 'VII', 'VIII', 'IX', 'X'", "N", "S", "N", "N");
        return ResponseEntity.ok().body(situacao);
    }

    @CrossOrigin
    @GetMapping(path = {"getInciso/{id}"})
    public ResponseEntity<?> findInciso(@PathVariable BigInteger id) {
        List<Inciso> list = new ArrayList<>();
        list.add(new Inciso("I", "Ofício subscrito pela autoridade competente",
                "Ofício subscrito pela autoridade competente dirigido ao Presidente do TCE/TO dando ciência do fato", "", "Sim"));
        list.add(new Inciso("II", "Requerimento de reforma",
                "Requerimento de reforma devidamente preenchido e assinado pelo interessado", "", "Sim"));
        list.add(new Inciso("III", "Documento de identidade e de inscrição no Cadastro de Pessoas Físicas ? CPF/MF, certidão de nascimento ou de casamento",
                "Documento de identidade e de inscrição no Cadastro de Pessoas Físicas ? CPF/MF, certidão de nascimento ou de casamento", "", "Sim"));
        list.add(new Inciso("IV", "Último contracheque",
                "Último contracheque", "", "Sim"));
        list.add(new Inciso("V", "Certidão de tempo de contribuição",
                "Certidão de tempo de contribuição expedida por órgão gestor de regime previdenciário, caso tenha exercido emprego anterior ao ingresso na Polícia Militar", "", "Sim"));
        list.add(new Inciso("VI", "Ato de concessão do benefício",
                "Ato de concessão do benefício, firmado na forma da lei de regência e acompanhado da respectiva publicação, constando o nome, a graduação até então ocupada, o valor dos proventos, a fundamentação legal para a concessão, bem como a data a partir da qual o militar será considerado inativo", "", "Sim"));
        list.add(new Inciso("VII", "Histórico funcional do militar",
                "Histórico funcional do militar", "", "Sim"));
        list.add(new Inciso("VIII", "Informação emitida pelo instituto de previdência",
                "Informação emitida pelo instituto de previdência ao qual o interessado esteja vinculado, constando o demonstrativo de apuração do tempo de contribuição e de cálculo do benefício", "", "Sim"));
        list.add(new Inciso("IX", "Declaração de não acumulação de proventos",
                "Declaração de não acumulação de proventos, nos termos da legislação correspondente", "", "Sim"));
        list.add(new Inciso("X", "Parecer jurídico",
                "Parecer jurídico atestando a legalidade da concessão do benefício", "", "Sim"));
        list.add(new Inciso("XI", "Laudo pericial",
                "Laudo pericial, com a indicação da moléstia que ensejou a incapacidade definitiva do militar, nos casos de reforma por invalidez", "", "Não"));
        list.add(new Inciso("Outros", "Outros",
                "Outros", "", "Não"));

        for (int i = 0; i < list.size(); i++) {
            Integer existeArquivo = documentoAposentadoriaRepository.findAllInciso("documentoAposentadoria", "idAposentadoria", id, list.get(i).getInciso());
            if (existeArquivo > 0) {
                list.get(i).setStatus("Informado");
            } else {
                list.get(i).setStatus("Não informado");
            }
        }

        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"anexos/{inciso}/{id}"})
    public ResponseEntity<?> findByDocumento(@PathVariable String inciso, @PathVariable BigInteger id) {
        return ResponseEntity.ok().body(documentoAposentadoriaRepository.buscarDocumentoAposentadoriaReforma(inciso, id));
    }

    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public void delete(@PathVariable BigInteger id) {
        documentoAposentadoriaRepository.delete(id); 
    }

    @CrossOrigin
    @PostMapping("/enviarGestor/{id}")
    public ResponseEntity<?> enviarGestorAssinar(@PathVariable BigInteger id,@RequestParam(value = "Ug", required = false) String ug) {
        AdmEnvio admEnvio = preencherEnvio(id,ug);
        admEnvioRepository.save(admEnvio);
        return ResponseEntity.ok().body("Ok");
    }

    private AdmEnvio preencherEnvio(BigInteger id,String ug) {
        Aposentadoria aposentadoria = aposentadoriaRepository.findById(id);
        AdmEnvio admEnvio = new AdmEnvio();
        admEnvio.setTipoRegistro(AdmEnvio.TipoRegistro.REFORMA.getValor());
        admEnvio.setUnidadeGestora(aposentadoria.getChave().getIdUnidadeGestora());
        admEnvio.setStatus(AdmEnvio.Status.AGUARDANDOASSINATURA.getValor());

        if (ug != null && !ug.equals(""))
            admEnvio.setOrgaoOrigem(ug);

        admEnvio.setIdMovimentacao(id);
        admEnvio.setAdmissao(aposentadoria.getAdmissao());
        admEnvio.setNumeroAto(aposentadoria.getAto().getNumeroAto());

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String dataFormatada = df.format(aposentadoria.getAto().getDataPublicacao());
        admEnvio.setComplemento("Conforme PORTARIA De: " + dataFormatada);
        return admEnvio;
    }
}
