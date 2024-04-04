package com.example.sicapweb.web.controller.remessa;

import br.gov.to.tce.model.InfoRemessa;
import com.example.sicapweb.repository.geral.UnidadeGestoraRepository;
import com.example.sicapweb.repository.remessa.AssinarRemessaRepository;
import com.example.sicapweb.repository.remessa.GfipRepository;
import com.example.sicapweb.repository.remessa.JustificativaGfipRepository;
import com.example.sicapweb.security.RedisConnect;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.ExcellExporter;
import com.example.sicapweb.util.PaginacaoUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ValidationException;
import java.io.IOException;
import java.util.*;



@Controller
@RestController
@RequestMapping(value = "/assinarRemessa")
public class AssinarRemessaController {

    @Autowired
    protected RedisConnect redisConnect;

    @Autowired
    private UnidadeGestoraRepository unidadeGestoraRepository;

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
        return ResponseEntity.ok().body(redisConnect.getUser(assinarRemessaRepository.getRequest()));
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
            assinarRemessaRepository.insertUsuarioAplicacao(infoRemessa);
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

        if (list.size() >= 2 || justificativaGfipRepository.buscaPorRemessa(infoRemessa.getRemessa(),infoRemessa.getExercicio()) != null)
            return ResponseEntity.ok().body("Ok");
        else
            return ResponseEntity.ok().body("pendente");
    }

    @GetMapping(path = {"/extratoDadosRemessa/excell"})
    public void getExcellFromTabelaRemessa(HttpServletResponse response, @RequestParam(name = "tabela") Integer tabela, @RequestParam(name = "remessa") Integer remessa, @RequestParam(name = "exercicio") Integer exercicio){
        try {

            String nomeTabela = Arrays.stream(AssinarRemessaRepository.Tabela.values()).filter(tabelaRepository -> tabelaRepository.getId()==tabela).map(tab -> tab.getLabel()).findFirst().get();

            List<HashMap<String,Object>> dados =assinarRemessaRepository.getExtratoDadosRemessa(PageRequest.of(0,10),remessa,exercicio,tabela, "",true).getRegistros();

            ExcellExporter ex =new ExcellExporter(dados,nomeTabela);

            ex.export(response);
        }catch (RuntimeException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
            return  ResponseEntity.ok().body(assinarRemessaRepository.getExtratoDadosRemessa(pageable,remessa,exercicio,tabela, searchParams,false));
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
            return  ResponseEntity.ok().body(assinarRemessaRepository.getResumoGeralRemessa(remessa,exercicio, (unidadeGestoraRepository.EhUnidadeGestoraRpps())? Arrays.asList(AssinarRemessaRepository.Tabela.Admissao.getId()):new ArrayList<Integer>() ));
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

