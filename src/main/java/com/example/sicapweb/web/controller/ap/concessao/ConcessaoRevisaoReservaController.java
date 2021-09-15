package com.example.sicapweb.web.controller.ap.concessao;

import br.gov.to.tce.model.ap.concessoes.DocumentoAposentadoria;
import br.gov.to.tce.model.ap.pessoal.Aposentadoria;
import com.example.sicapweb.model.Inciso;
import com.example.sicapweb.repository.concessao.AposentadoriaRepository;
import com.example.sicapweb.repository.concessao.DocumentoAposentadoriaRepository;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/documentoConcessaoRevisaoReserva")
public class ConcessaoRevisaoReservaController  extends DefaultController<Aposentadoria> {

    @Autowired
    private AposentadoriaRepository aposentadoriaRepository;

    @Autowired
    private DocumentoAposentadoriaRepository documentoAposentadoriaRepository;

    @CrossOrigin
    @GetMapping
    @Override
    public ResponseEntity<List<Aposentadoria>> findAll() {
        List<Aposentadoria> list = aposentadoriaRepository.buscarAposentadoriaRevisaoReserva();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping("/upload/{inciso}/{id}")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file, @PathVariable String inciso, @PathVariable BigInteger id) {
        DocumentoAposentadoria documentoAposentadoria = new DocumentoAposentadoria();
        documentoAposentadoria.setAposentadoria(aposentadoriaRepository.findById(id));
        documentoAposentadoria.setInciso(inciso);
        String idCastor = super.setCastorFile(file, "AposentadoriaRevisaoReserva");
        documentoAposentadoria.setIdCastorFile(idCastor);
        documentoAposentadoria.setStatus(DocumentoAposentadoria.Status.Informado.getValor());
        documentoAposentadoria.setRevisao("S");
        documentoAposentadoria.setReserva("S");
        documentoAposentadoriaRepository.save(documentoAposentadoria);
        return ResponseEntity.ok().body(idCastor);
    }
    @CrossOrigin
    @GetMapping(path = {"getInciso/{id}"})
    public ResponseEntity<?> findInciso(@PathVariable BigInteger id) {
        List<Inciso> list = new ArrayList<>();
        list.add(new Inciso("I", "Ofício subscrito pela autoridade competente",
                "Ofício subscrito pela autoridade competente dirigido ao Presidente do TCE/TO dando ciência do fato", "", "Sim"));
        list.add(new Inciso("II", "Requerimento de reserva",
                "Requerimento de reserva devidamente preenchido e assinado pelo interessado", "", "Sim"));
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
        list.add(new Inciso("", "Outros",
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
        DocumentoAposentadoria list = documentoAposentadoriaRepository.buscarDocumentoRevisaoReserva(inciso, id).get(0);
        return ResponseEntity.ok().body(list);
    }
}
