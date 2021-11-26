package com.example.sicapweb.web.controller.remessa;

import br.gov.to.tce.model.InfoRemessa;
import com.example.sicapweb.repository.remessa.GfipRepository;
import com.example.sicapweb.repository.remessa.AcompanhamentoDeRemessasRepository;
import com.example.sicapweb.repository.remessa.InfoRemessaRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
@RestController
@RequestMapping(value = "/acompanhamentoDeRemessas")
public class AcompanhamentoDeRemessasController {



    @Autowired
    private AcompanhamentoDeRemessasRepository acompanhamentoDeRemessasRepository;

    @Autowired
    private GfipRepository gfipRepository;

  @Autowired
  private InfoRemessaRepository infoRemessaRepository;



    @CrossOrigin
    @GetMapping(path = {"/close"})
    public ResponseEntity<?> findRemessaClose() {
      List<InfoRemessa> infoRemessa = acompanhamentoDeRemessasRepository.buscarRemessaFechada();

      return ResponseEntity.ok().body(Objects.requireNonNullElse(infoRemessa, "semRemessa"));
    }
  @CrossOrigin
  @GetMapping(path = {"/filtro/{exercicio}"})
  public ResponseEntity<?> filtroRemessaClose(@PathVariable String exercicio) {
    List<InfoRemessa> infoRemessa = acompanhamentoDeRemessasRepository.filtroRemessaFechada(exercicio);

    return ResponseEntity.ok().body(Objects.requireNonNullElse(infoRemessa, "semRemessa"));
  }

  @CrossOrigin
  @GetMapping(path = {"/fechado/{chave}"})
  public ResponseEntity<?> findRemessaFechado(@PathVariable String chave) {
   InfoRemessa infoRemessa = acompanhamentoDeRemessasRepository.findRemessaFechada(chave);

    return ResponseEntity.ok().body(Objects.requireNonNullElse(infoRemessa, "RemessaAberta"));
  }


    @CrossOrigin
    @GetMapping(path = {"/{cargo}/{chave}"})
    public ResponseEntity<?> findResponsavel(@PathVariable String cargo, @PathVariable String chave ) {
      Integer tipoCargo;
      switch (cargo) {
        case "Gestor":
          tipoCargo = User.Cargo.Gestor.getValor();
          break;
        case "Responsável R.H.":
          tipoCargo = User.Cargo.ResponsavelRH.getValor();
          break;
        case "Controle Interno":
          tipoCargo = User.Cargo.ControleInterno.getValor();
          break;
        default:
          tipoCargo = 0;
          break;

      }
      InfoRemessa info = infoRemessaRepository.findById(chave);
      Object resp = acompanhamentoDeRemessasRepository.buscarResponsavelAssinatura(tipoCargo, info);
      return ResponseEntity.ok().body(Objects.requireNonNullElse(resp, "semPermissao"));
    }

    @CrossOrigin
    @GetMapping(path = {"/autenticacao"})
    public ResponseEntity<User> findeUserAutenticacao() {
      User user = User.getUser(acompanhamentoDeRemessasRepository.getRequest());
      return ResponseEntity.ok().body(user);
    }
  @CrossOrigin
  @GetMapping(path = "/{searchParams}/{tipoParams}/pagination")
  public ResponseEntity<PaginacaoUtil<Object>>  listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
    var paginacaoUtil = acompanhamentoDeRemessasRepository.buscaPaginadaAcompanhamento(pageable, searchParams, tipoParams);
    return ResponseEntity.ok().body(paginacaoUtil);
  }

  @GetMapping("/gerarPdf")
  public void gerarPdf() throws Exception {

    // criação do documento
    Document document = new Document();
    try {

      PdfWriter.getInstance(document,
              new FileOutputStream("C:\\Users\\luanamm\\Downloads\\Recibo1.pdf"));
      document.open();


      document.add(new Paragraph("                                                           "));

      // adicionando um parágrafo no documento
      Paragraph aux = new Paragraph("Recibo");
      aux.setAlignment(1);
      document.add(aux);
      document.add(new Paragraph("                                                           "));


      document.add(new Paragraph("teste"));
      document.add(new Paragraph("                                                           "));


      document.close();


    } catch (DocumentException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

  }

  @CrossOrigin
  @GetMapping(path = {"/getRemessa/{exercicio}/{remessa}/{chave}"})
  public ResponseEntity<?> findByRemessa(@PathVariable Integer exercicio, @PathVariable Integer remessa, @PathVariable String chave) {
    List<Map<String, Object>> infoRemessa = acompanhamentoDeRemessasRepository.buscarAcompanhamentoRemessa(exercicio, remessa, chave);
    return ResponseEntity.ok().body(Objects.requireNonNullElse(infoRemessa, "semRemessa"));
  }
  }
