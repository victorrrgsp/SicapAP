package com.example.sicapweb.web.controller.externo;

import br.gov.to.tce.model.InfoRemessa;
import com.example.sicapweb.repository.externo.AcompanhamentoRemessaRepository;
import com.example.sicapweb.repository.remessa.AcompanhamentoDeRemessasRepository;
import com.example.sicapweb.repository.remessa.GfipRepository;
import com.example.sicapweb.repository.remessa.InfoRemessaRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/externo/acompanhamentoRemessa")
public class AcompanhamentoRemessaController {



    @Autowired
    private AcompanhamentoRemessaRepository acompanhamentoRemessaRepository;

    @Autowired
    private GfipRepository gfipRepository;

  @Autowired
  private InfoRemessaRepository infoRemessaRepository;



    @CrossOrigin
    @GetMapping(path = {"/all"})
    public ResponseEntity<?> findRemessaClose() {
      List<Object> infoRemessa = acompanhamentoRemessaRepository.buscarRemessaFechada();

      return ResponseEntity.ok().body(Objects.requireNonNullElse(infoRemessa, "semRemessa"));
    }
  @CrossOrigin
  @GetMapping(path = {"/filtro/{exercicio}"})
  public ResponseEntity<?> filtroRemessaClose(@PathVariable String exercicio) {
    List<InfoRemessa> infoRemessa = acompanhamentoRemessaRepository.filtroRemessaFechada(exercicio);

    return ResponseEntity.ok().body(Objects.requireNonNullElse(infoRemessa, "semRemessa"));
  }

  @CrossOrigin
  @GetMapping(path = {"/fechado/{chave}"})
  public ResponseEntity<?> findRemessaFechado(@PathVariable String chave) {
   InfoRemessa infoRemessa = acompanhamentoRemessaRepository.findRemessaFechada(chave);

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
      Object resp = acompanhamentoRemessaRepository.buscarResponsavelAssinatura(tipoCargo, info);
      return ResponseEntity.ok().body(Objects.requireNonNullElse(resp, "semPermissao"));
    }

    @CrossOrigin
    @GetMapping(path = {"/autenticacao"})
    public ResponseEntity<User> findeUserAutenticacao() {
      User user = User.getUser();
      return ResponseEntity.ok().body(user);
    }
  @CrossOrigin
  @GetMapping(path = "/{searchParams}/{tipoParams}/pagination")
  public ResponseEntity<PaginacaoUtil<InfoRemessa>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
    PaginacaoUtil<InfoRemessa> paginacaoUtil = acompanhamentoRemessaRepository.buscaPaginadaHistorico(pageable, searchParams, tipoParams);
    return ResponseEntity.ok().body(paginacaoUtil);
  }

}
