package com.example.sicapweb.web.controller.remessa;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.model.ap.folha.JustificativaGfip;
import com.example.sicapweb.model.AdmissaoEnvioAssRetorno;
import com.example.sicapweb.repository.remessa.AssinarRemessaRepository;
import com.example.sicapweb.repository.remessa.GfipRepository;
import com.example.sicapweb.repository.remessa.JustificativaGfipRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.ValidationException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


@Transactional
@Controller
@RestController
@RequestMapping(value = "/assinarRemessa")
public class AssinarRemessaController {

    public InfoRemessa info;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private AssinarRemessaRepository assinarRemessaRepository;

    @Autowired
    private JustificativaGfipRepository justificativaGfipRepository;

    @Autowired
    private GfipRepository gfipRepository;

    @CrossOrigin
    @GetMapping
    public ResponseEntity<?> findRemessaOpen() {
        InfoRemessa infoRemessa = assinarRemessaRepository.buscarRemessaAberta();
        info = infoRemessa;
        return ResponseEntity.ok().body(Objects.requireNonNullElse(infoRemessa, "semRemessa"));
    }

    @CrossOrigin
    @GetMapping(path = {"/close"})
    public ResponseEntity<?> findRemessaClose() {
        InfoRemessa infoRemessa = assinarRemessaRepository.buscarRemessaFechada();
        info = infoRemessa;
        return ResponseEntity.ok().body(Objects.requireNonNullElse(infoRemessa, "semRemessa"));
    }

    @CrossOrigin
    @GetMapping(path = {"/{cargo}"})
    public ResponseEntity<?> findResponsavel(@PathVariable String cargo) {
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
        InfoRemessa infoRemessa = assinarRemessaRepository.buscarRemessaAberta();
        Object resp = assinarRemessaRepository.buscarResponsavelAssinatura(tipoCargo, infoRemessa);
        return ResponseEntity.ok().body(Objects.requireNonNullElse(resp, "semPermissao"));
    }

    @CrossOrigin
    @GetMapping(path = {"/autenticacao"})
    public ResponseEntity<User> findeUserAutenticacao(HttpSession session) {
        User user = User.getUser(assinarRemessaRepository.getRequest());
        return ResponseEntity.ok().body(user);
    }

    @CrossOrigin
    @Transactional
    @GetMapping(path = {"/insertDados"})
    public ResponseEntity<?> insertDados() {
        InfoRemessa infoRemessa = assinarRemessaRepository.buscarRemessaAberta();
        boolean isValido = assinarRemessaRepository.remessaValida(infoRemessa);

        if (isValido) {
            assinarRemessaRepository.insertArquivo();
            assinarRemessaRepository.insertAssinatura();
            assinarRemessaRepository.insertInfoAssinatura(infoRemessa);
            assinarRemessaRepository.insertAdmAssinatura(infoRemessa.getChave());
            return ResponseEntity.ok().body("Ok");
        } else {
            return ResponseEntity.ok().body("Falha");
        }
    }

    @CrossOrigin
    @GetMapping(path = {"/situacao"})
    public ResponseEntity<?> findDocumentos() {
        InfoRemessa infoRemessa = assinarRemessaRepository.buscarRemessaAberta();
        List<Integer> list = new ArrayList<>();

        if (infoRemessa != null) {
            list = gfipRepository.findDocumentos(infoRemessa.getChave() );
        }

        if (list.size() >= 3 || justificativaGfipRepository.buscaPorRemessa(infoRemessa.getRemessa(),infoRemessa.getExercicio()) != null)
            return ResponseEntity.ok().body("Ok");
        else
            return ResponseEntity.ok().body("pendente");
    }

    @CrossOrigin
    @PostMapping(path = {"/extratoDadosRemessa/pagination"})
    public   ResponseEntity<PaginacaoUtil<HashMap<String,Object>>>  getExtratoRemessa(Pageable pageable ,  @RequestBody String tabelaRemessa){
        try {
            JsonNode parametrosJson = new ObjectMapper().readTree(tabelaRemessa);
            Integer tabela = Integer.valueOf(parametrosJson.get("tabela").asText());
            Integer remessa = Integer.valueOf(parametrosJson.get("remessa").asText());
            Integer exercicio = Integer.valueOf(parametrosJson.get("exercicio").asText());
             String searchParams = parametrosJson.get("search").asText();
            return  ResponseEntity.ok().body(assinarRemessaRepository.GetExtratoDadosRemessa(pageable,remessa,exercicio,tabela, searchParams));
        }
        catch (NumberFormatException e){
            e.printStackTrace();
            throw  new RuntimeException("Parametro passado não é numerico!!");
        }

        catch (JsonMappingException e) {
            throw new RuntimeException(e);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            throw new ValidationException("problema no Extrato de dados da remessa. entre em contato com o Administrador do sicap!!");
        }

    }


    @CrossOrigin
    @PostMapping(path = {"/resumoExtratoDadosRemessa"})
    public   ResponseEntity<List<HashMap<String,Object>>>  getResumoExtratoRemessa( @RequestBody String tabelaRemessa){
        try {
            JsonNode parametrosJson = new ObjectMapper().readTree(tabelaRemessa);
            Integer remessa = Integer.valueOf(parametrosJson.get("remessa").asText());
            Integer exercicio = Integer.valueOf(parametrosJson.get("exercicio").asText());
            return  ResponseEntity.ok().body(assinarRemessaRepository.getResumoGeralRemessa(remessa,exercicio));
        }
        catch (NumberFormatException e){
            e.printStackTrace();
            throw  new RuntimeException("Parametro passado não é numerico!!");
        }
        catch (JsonMappingException e) {
            throw new RuntimeException(e);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            throw new ValidationException("problema no Extrato de dados da remessa. entre em contato com o Administrador do sicap!!");
        }

    }





}

