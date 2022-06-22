package com.example.sicapweb.web.controller.ap.concessao;

import br.gov.to.tce.model.adm.AdmEnvio;
import br.gov.to.tce.model.ap.concessoes.DocumentoAposentadoria;
import br.gov.to.tce.model.ap.pessoal.Aposentadoria;
import com.example.sicapweb.model.Inciso;
import com.example.sicapweb.model.dto.AposentadoriaDTO;
import com.example.sicapweb.repository.AdmEnvioRepository;
import com.example.sicapweb.repository.concessao.AposentadoriaRepository;
import com.example.sicapweb.repository.concessao.DocumentoAposentadoriaRepository;
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
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/documentoConcessaoAposentadoria")
public class ConcessaoAposentadoriaController extends DefaultController<DocumentoAposentadoria> {

    @Autowired
    private AposentadoriaRepository aposentadoriaRepository;

    @Autowired
    private DocumentoAposentadoriaRepository documentoAposentadoriaRepository;

    @Autowired
    private AdmEnvioRepository admEnvioRepository;

    HashMap<String, Object> aposentadoria = new HashMap<String, Object>();

    public class AposentadoriaDocumento{
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
    @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<AposentadoriaDTO>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<AposentadoriaDTO> paginacaoUtil = aposentadoriaRepository.buscaPaginadaAposentadorias(pageable,searchParams,tipoParams);
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
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file, @PathVariable String inciso, @PathVariable BigInteger id) {
        DocumentoAposentadoria documentoAposentadoria = new DocumentoAposentadoria();
        documentoAposentadoria.setAposentadoria(aposentadoriaRepository.findById(id));
        documentoAposentadoria.setInciso(inciso);
        String idCastor = super.setCastorFile(file, "Aposentadoria");
        documentoAposentadoria.setIdCastorFile(idCastor);
        documentoAposentadoria.setStatus(DocumentoAposentadoria.Status.Informado.getValor());
        documentoAposentadoriaRepository.save(documentoAposentadoria);
        return ResponseEntity.ok().body(idCastor);
    }

    @CrossOrigin
    @GetMapping(path = {"getDocumentos"})
    public ResponseEntity<?> findAllDocumentos() {
        List<Aposentadoria> list = aposentadoriaRepository.buscarAposentadorias();
        AposentadoriaDocumento situacao = new AposentadoriaDocumento();
        for(Integer i= 0; i < list.size(); i++){
            Integer quantidadeDocumentos = documentoAposentadoriaRepository.findSituacao("documentoAposentadoria","idAposentadoria", list.get(i).getId(), "'I', 'II', 'III', 'IV', 'V', 'VI', 'VII', 'VIII', 'XII', 'XIII'", "N", "N", "N", "N");
            if(quantidadeDocumentos == 0) {
                situacao.setAposentadoria(list.get(i));
                situacao.setSituacao("Pendente");
            } else if(quantidadeDocumentos == 10){
                situacao.setAposentadoria(list.get(i));
                situacao.setSituacao("Concluído");
            } else{
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
        Integer situacao = documentoAposentadoriaRepository.findSituacao("documentoAposentadoria","idAposentadoria",id, "'I', 'II', 'III', 'IV', 'V', 'VI', 'VII', 'VIII', 'XII', 'XIII'", "N", "N", "N", "N");
        return ResponseEntity.ok().body(situacao);
    }

    @CrossOrigin
    @GetMapping(path = {"getInciso/{id}"})
    public ResponseEntity<?> findInciso(@PathVariable BigInteger id) {
        List<Inciso> list = new ArrayList<>();
        list.add(new Inciso("I", "Ofício subscrito pela autoridade competente",
                "Ofício subscrito pela autoridade competente dirigido ao Presidente do TCE/TO dando ciência do fato", "", "Sim"));
        list.add(new Inciso("II", "Requerimento de aposentadoria",
                "Requerimento de aposentadoria indicando a permanência do servidor na atividade até a publicação do ato ou da data do afastamento preliminar", "", "Sim"));
        list.add(new Inciso("III", "Documento de identidade e de inscrição no Cadastro de Pessoas Físicas - CPF/MF",
                "Documento de identidade e de inscrição no Cadastro de Pessoas Físicas - CPF/MF", "", "Sim"));
        list.add(new Inciso("IV", "Ato de concessão do benefício",
                "Ato de concessão do benefício firmado na forma da lei de refência, acompanhado da respectiva publicação, constando o nome do servidor, o cargo até então ocupado, o valor dos proventos e da fundamentação legal para a concessão, bem como a data a partir da qual o servidor será considerado aposentado", "", "Sim"));
        list.add(new Inciso("V", "Certidão de tempo de contribuição",
                "Certidão de tempo de contribuição expedida pelo Regime Geral de Previdência Social (RGPS) ou pelo Regime Próprio de Previdência Social (RPPS), conforme o caso, detalhando o vínculo previdenciário do beneficiário antes do ingresso no cargo em que se der a aposentadoria.", "", "Sim"));
        list.add(new Inciso("VI", "Último contracheque do servidor",
                "Último contracheque do servidor", "", "Sim"));
        list.add(new Inciso("VII", "Demonstrativo dos cálculos de proventos",
                "Demonstrativo dos cálculos de proventos com base na remuneração do cargo efetivo, discriminando as verbas percebidas, inclusive as vantagens de caráter pessoal com fundamento legal para a incorporação, quando for o caso, informando o total mensal e especificando se os proventos são integrais ou proporcionais, devendo, neste último caso, informar a proporcionalidade adotada", "", "Sim"));
        list.add(new Inciso("VIII", "Declaração ou histórico funcional",
                "Declaração ou histórico funcional discriminando o tempo de efetivo exercício no serviço público, o tempo de exercício na carreira e no cargo efetivo em que se deu a aposentadoria, nos casos daquelas concedidas com base no disposto nos arts. 2º e 3º da EC nº 41/2003, arts. 2º e 3º da EC nº 47/2005, ou nas hipóteses de aposentadorias embasadas na EC nº 20/1998", "", "Sim"));
        list.add(new Inciso("IX", "Laudo pericial atestando a incapacidade definitiva do servidor",
                "Laudo pericial atestando a incapacidade definitiva do servidor, com a indicação da moléstia que o tornou inabilitado para a vida laboral, nos casos de aposentadoria por invalidez", "", "Não"));
        list.add(new Inciso("X", "Declaração firmada pelo servidor de não acúmulo de proventos de aposentadoria",
                "Declaração firmada pelo servidor de não acúmulo de proventos de aposentadoria por parte de qualquer ente público da Federação, ressalvados os cargos, empregos e funções públicas acumuláveis por permissivos constitucionais", "", "Não"));
        list.add(new Inciso("XI", "Termo de opção",
                "Termo de opção em sendo o caso de acúmulo de cargo, na conformidade das exigências legais", "", "Não"));
        list.add(new Inciso("XII", "Informação emitida pelo instituto previdenciário",
                "Informação emitida pelo instituto previdenciário ao qual o beneficiário esteja vinculado constando o demonstrativo de apuração do tempo de contribuição e de cálculo do benefício", "", "Sim"));
        list.add(new Inciso("XIII", "Parecer jurídico atestando a legalidade da concessão do benefício",
                "Parecer jurídico atestando a legalidade da concessão do benefício", "", "Sim"));
        list.add(new Inciso("Outros", "Outros",
                "Outros", "", "Não"));

        for (int i = 0; i < list.size(); i++){
            Integer existeArquivo = documentoAposentadoriaRepository.findAllInciso("documentoAposentadoria","idAposentadoria",id, list.get(i).getInciso());
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
        DocumentoAposentadoria list = documentoAposentadoriaRepository.buscarDocumentoAposentadoria(inciso, id).get(0);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public ResponseEntity<?> delete(@PathVariable BigInteger id) {
        documentoAposentadoriaRepository.delete(id);
        return ResponseEntity.noContent().build();
    }

    @CrossOrigin
    @PostMapping("/enviarGestor/{id}")
    public ResponseEntity<?> enviarGestorAssinar(@PathVariable BigInteger id) {
        AdmEnvio admEnvio = preencherEnvio(id);
        admEnvioRepository.save(admEnvio);
        return ResponseEntity.ok().body("Ok");
    }

    private AdmEnvio preencherEnvio(BigInteger id) {
        Aposentadoria aposentadoria = aposentadoriaRepository.findById(id);
        AdmEnvio admEnvio = new AdmEnvio();
        admEnvio.setTipoRegistro(AdmEnvio.TipoRegistro.APOSENTADORIA.getValor());
        admEnvio.setUnidadeGestora(aposentadoria.getChave().getIdUnidadeGestora());
        admEnvio.setStatus(AdmEnvio.Status.AGUARDANDOASSINATURA.getValor());
        admEnvio.setOrgaoOrigem(aposentadoria.getCnpjUnidadeGestoraOrigem());
        admEnvio.setIdMovimentacao(id);
        admEnvio.setComplemento("Conforme PORTARIA: " + aposentadoria.getAto().getNumeroAto() + " De: " + aposentadoria.getAto().getDataPublicacao());
        admEnvio.setAdmissao(aposentadoria.getAdmissao());
        return admEnvio;
    }
}
