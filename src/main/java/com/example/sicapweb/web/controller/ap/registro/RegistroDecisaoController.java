package com.example.sicapweb.web.controller.ap.registro;

import br.gov.to.tce.model.ap.pessoal.Admissao;
import br.gov.to.tce.model.ap.pessoal.Aposentadoria;
import br.gov.to.tce.model.ap.pessoal.Pensao;
import br.gov.to.tce.model.ap.registro.Registro;
import br.gov.to.tce.model.ap.registro.RegistroAdmissao;
import br.gov.to.tce.model.ap.registro.RegistroAposentadoria;
import br.gov.to.tce.model.ap.registro.RegistroPensao;
import br.gov.to.tce.model.ap.relacional.Ato;
import com.example.sicapweb.exception.InvalitInsert;
import com.example.sicapweb.repository.concessao.AposentadoriaRepository;
import com.example.sicapweb.repository.concessao.PensaoRepository;
import com.example.sicapweb.repository.geral.AtoRepository;
import com.example.sicapweb.repository.movimentacaoDePessoal.AdmissaoRepository;
import com.example.sicapweb.repository.registro.RegistroAdmissaoRepository;
import com.example.sicapweb.repository.registro.RegistroAposentadoriaRepository;
import com.example.sicapweb.repository.registro.RegistroPensaoRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/registro")
public class RegistroDecisaoController  {

    @Autowired
    private RegistroAposentadoriaRepository registroAposentadoriaRepository;

    @Autowired
    private RegistroPensaoRepository registroPensaoRepository;

    @Autowired
    private RegistroAdmissaoRepository registroAdmissaoRepository;

    @Autowired
    private AposentadoriaRepository aposentadoriaRepository;

    @Autowired
    private PensaoRepository pensaoRepository;

    @Autowired
    private AdmissaoRepository admissaoRepository;

    @Autowired
    private AtoRepository atoRepository;

    public static final List<Registro.Tipo> tiposRegistrosNaTabelaAposentadoria = Arrays.asList(
                Registro.Tipo.Aposentadoria,Registro.Tipo.Reserva,Registro.Tipo.Reforma,Registro.Tipo.Reversao,
                Registro.Tipo.RevisaoReforma,Registro.Tipo.RevisaoAposentadoria, Registro.Tipo.RevisaoReserva
    );

    public static final List<Registro.Tipo> tiposRegistrosNaTabelaPensao  = Arrays.asList(Registro.Tipo.Pensao);

    public static final List<Registro.Tipo> tiposRegistrosNaTabelaAdmissao  = Arrays.asList(Registro.Tipo.Efetivos);


    @PostMapping (path="/movimentacao/{tipoRegistro}/pagination")
    public ResponseEntity<PaginacaoUtil<HashMap<String,Object>>> buscaMovimentos(Pageable pageable,
                                                                                 @RequestHeader("Authorization") String bearerToken,
                                                                                 @RequestBody HashMap<String, String> jsonFiltro ,
                                                                                 @PathVariable Integer tipoRegistro) {
        HashMap<String,Object> UserInfo = getInfoUserFromToken(bearerToken);
        return ResponseEntity.ok().body(
                Objects.requireNonNullElse(
                        this.getMovimentosPorTipo(pageable,tipoRegistro,jsonFiltro) ,
                        new PaginacaoUtil<>(0, 1, 1, 0, new ArrayList<>())
                )
        );
    }

    @PostMapping ("/{tipoRegistro}/")
    @Transactional
    public void salvarRegistrar( @RequestHeader("Authorization") String bearerToken, @RequestBody  List<Registro> registros, @PathVariable Integer tipoRegistro){
        HashMap<String,Object> UserInfo = getInfoUserFromToken(bearerToken);
        for (Registro registroAtual : registros ){
            setInformacoesDoUsuarioNoRegistro(registroAtual,UserInfo);
            validarMovimentacao(UserInfo,registroAtual.getIdMovimentacao(),tipoRegistro);
            gravarRegistro(registroAtual,tipoRegistro);
        }
    }

    @CrossOrigin
    @Transactional
    @PutMapping( "/{tipoRegistro}")
    public void alterarRegistro(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody  Registro registro,
            @PathVariable Integer tipoRegistro) {
        HashMap<String,Object> UserInfo = getInfoUserFromToken(bearerToken);
        alterarRegistro(registro,tipoRegistro);
    }

    @CrossOrigin
    @Transactional
    @PostMapping  (path="/{tipoRegistro}/pagination")
    public ResponseEntity<PaginacaoUtil<HashMap<String,Object>>> buscaRegistrosPaginados(Pageable pageable,
                                                                                         @RequestHeader("Authorization") String bearerToken,
                                                                                         @PathVariable Integer tipoRegistro,
                                                                                         @RequestBody HashMap<String, String> jsonFiltro
    )
    {
        HashMap<String,Object> UserInfo = getInfoUserFromToken(bearerToken);
        setInfoUsuarioEmFiltros(jsonFiltro,UserInfo);
        return ResponseEntity.ok().body(
                Objects.requireNonNullElse(
                        this.getRegistrosPorTipo(pageable,tipoRegistro,jsonFiltro),
                        new PaginacaoUtil<>(0, 1, 1, 0, new ArrayList<>())
                )
        );
    }

    @CrossOrigin
    @Transactional
    @PostMapping  (path="/alterarMovimentacao/{tipoRegistro}")
    public void AlterarMovimentacao(@RequestHeader("Authorization") String bearerToken,
                                                                                         @PathVariable Integer tipoRegistro,
                                                                                         @RequestBody HashMap<String,Object > infoAlteraracao
    ) throws ParseException {
        HashMap<String,Object> UserInfo = getInfoUserFromToken(bearerToken);
        validarMovimentacao( UserInfo ,BigInteger.valueOf(((Integer)infoAlteraracao.get("idMovimentacao")).longValue()),tipoRegistro);
        alterarMovimentacao(infoAlteraracao,tipoRegistro);
    }

    @GetMapping(path = "/processos")
    public ResponseEntity<List<HashMap<String,Object>>> listaProcessos( @RequestHeader("Authorization") String bearerToken ){
        HashMap<String,Object> userInfo = getInfoUserFromToken(bearerToken);
        List<HashMap<String,Object>> infoProcessos =registroAposentadoriaRepository.getInforProcessosEcontas(userInfo);
        return ResponseEntity.ok().body(infoProcessos);
    }


    @GetMapping(path = "/autorizado")
    public ResponseEntity<String> autorizarRegistro( @RequestHeader("Authorization") String bearerToken ){
        HashMap<String,Object> userInfo = getInfoUserFromToken(bearerToken);
        if (Objects.requireNonNullElse( userInfo.get("setor"),"").equals("DIRAP")
                ||  Set.of("josermc","marcusop","ewertonfs","guilhermehsl","wesleyrl").contains(Objects.requireNonNullElse( userInfo.get("loginUsuario"),"")) )
            return ResponseEntity.ok().body(  "temAutorizacao");
        return ResponseEntity.ok().body("semAutorizacao");
    }


    @GetMapping(path = "/documentos/{numeroProcesso}/{anoProcesso}")
    public ResponseEntity<List<HashMap<String,Object>>> listaDocumentos( @RequestHeader("Authorization") String bearerToken,@PathVariable Integer numeroProcesso,@PathVariable Integer anoProcesso ){
        HashMap<String,Object> userInfo = getInfoUserFromToken(bearerToken);
        List<HashMap<String,Object>> infoProcessos =registroAposentadoriaRepository.getDocumentosbyProcessoEcontas(userInfo,numeroProcesso,anoProcesso);
        return ResponseEntity.ok().body(infoProcessos);
    }


    private void setInformacoesDoUsuarioNoRegistro(Registro registro,HashMap<String,Object> userInfo){
        registro.setDataCadastro(LocalDateTime.now());
        registro.setIpUsuarioCadastro(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getRemoteAddr());
        registro.setCpfUsuarioCadastro((String)userInfo.get("cpfUsuario"));
    }

    private PaginacaoUtil<HashMap<String,Object>> getRegistrosPorTipo(Pageable pageable,Integer tipoRegistro,HashMap<String, String> filtro){
         Registro.Tipo  tipoRegistroEnum =  Arrays.stream(Registro.Tipo.values()).filter(tipo -> tipo.getValor()==tipoRegistro).findFirst().get();
        if (this.tiposRegistrosNaTabelaAposentadoria.contains(tipoRegistroEnum))
            return  registroAposentadoriaRepository.getRegistros(pageable,tipoRegistroEnum,filtro);
        else if (this.tiposRegistrosNaTabelaPensao.contains(tipoRegistroEnum))
            return registroPensaoRepository.getRegistrosPensao(pageable,filtro);
        else if (this.tiposRegistrosNaTabelaAdmissao.contains(tipoRegistroEnum))
            return registroAdmissaoRepository.getRegistrosAdmissao(pageable,filtro);
        else
            throw  new RuntimeException("não encontrou tipo de registro!!");
    }

    private PaginacaoUtil<HashMap<String , Object>> getMovimentosPorTipo(Pageable pageable,Integer tipoRegistro,HashMap<String, String> filtro  ){
        Registro.Tipo  tipoRegistroEnum =  Arrays.stream(Registro.Tipo.values()).filter(tipo -> tipo.getValor()==tipoRegistro).findFirst().get();
        if (this.tiposRegistrosNaTabelaAposentadoria.contains(tipoRegistroEnum))
            return  registroAposentadoriaRepository.getMovimentosParaRegistrar(pageable,filtro, tipoRegistroEnum );
        else if (this.tiposRegistrosNaTabelaPensao.contains(tipoRegistroEnum))
            return registroPensaoRepository.getMovimentosPensaoParaRegistrar(pageable,filtro);
        else if (this.tiposRegistrosNaTabelaAdmissao.contains(tipoRegistroEnum))
            return registroAdmissaoRepository.getMovimentosAdmissaoParaRegistrar(pageable,filtro);
        else
            throw  new RuntimeException("não encontrou tipo de registro definido na busca!!");
    }

    private void gravarRegistro(Registro registro, Integer tipoRegistro){
        try{
            Registro.Tipo  tipoRegistroEnum =  Arrays.stream(Registro.Tipo.values()).filter(tipo -> tipo.getValor()==tipoRegistro).findFirst().get();
            if  (this.tiposRegistrosNaTabelaAposentadoria.contains(tipoRegistroEnum))
                registroAposentadoriaRepository.save(new RegistroAposentadoria(registro, aposentadoriaRepository.findById(registro.getIdMovimentacao())));
            else if (this.tiposRegistrosNaTabelaPensao.contains(tipoRegistroEnum))
                registroPensaoRepository.save(new RegistroPensao(registro, pensaoRepository.findById(registro.getIdMovimentacao())));
            else if (this.tiposRegistrosNaTabelaAdmissao.contains(tipoRegistroEnum))
                registroAdmissaoRepository.save(new RegistroAdmissao(registro, admissaoRepository.findById(registro.getIdMovimentacao())));
            else
                throw new IllegalArgumentException("Tipo de registro não encontrado !!");
        }catch (IllegalArgumentException e){
            throw new InvalitInsert(e.getMessage());
        }
        catch (RuntimeException e){
            e.printStackTrace();
            throw new InvalitInsert("Problema ao alterar o registro da movimentação do servidor com cpf "+registro.getCpfServidor()+" !!");
        }
    }

    private void alterarRegistro( Registro registro, Integer tipoRegistro){
        try {
            Registro.Tipo  tipoRegistroEnum =  Arrays.stream(Registro.Tipo.values()).filter(tipo -> tipo.getValor()==tipoRegistro).findFirst().get();
            if  (this.tiposRegistrosNaTabelaAposentadoria.contains(tipoRegistroEnum))
                registroAposentadoriaRepository.update( registroAposentadoriaRepository.findById(registro.getId()).setCamposRegistro(registro) );
            else if (this.tiposRegistrosNaTabelaPensao.contains(tipoRegistroEnum))
                registroPensaoRepository.update(registroPensaoRepository.findById(registro.getId()).setCamposRegistro(registro));
            else if (this.tiposRegistrosNaTabelaAdmissao.contains(tipoRegistroEnum))
                registroAdmissaoRepository.update(registroAdmissaoRepository.findById(registro.getId()).setCamposRegistro(registro));
            else
                throw new IllegalArgumentException("Tipo de registro não encontrado !!");
        }catch (IllegalArgumentException e){
            throw new InvalitInsert(e.getMessage());
        }
        catch (RuntimeException e){
            e.printStackTrace();
            throw new InvalitInsert("Problema ao registro a movimentação do servidor com cpf "+registro.getCpfServidor()+" !!");
        }
    }

    private void alterarMovimentacao(HashMap<String,Object> camposParaAlterar, Integer tipoRegistro ) throws ParseException {
        Registro.Tipo  tipoRegistroEnum =  Arrays.stream(Registro.Tipo.values()).filter(tipo -> tipo.getValor()==tipoRegistro).findFirst().get();
        //Informaçoes que serão alterados no movimento
        String numeroAto = (String) camposParaAlterar.get("numeroAto");
        Integer tipoAto = (Integer)camposParaAlterar.get("tipoAto");
        String idUnidadeGestora = (String)camposParaAlterar.get("idUnidadeGestora");
        String veiculoPublicacao = (String)camposParaAlterar.get("veiculoPublicacao");
        java.sql.Date dataPublicacao =
                new java.sql.Date( (new SimpleDateFormat("yyyy-MM-dd" ))
                        .parse((String)camposParaAlterar.get("dataPublicacao")).getTime() );
        BigInteger idMovimentacao =   BigInteger.valueOf(((Integer)camposParaAlterar.get("idMovimentacao")).longValue());
        //tratando as alteracoes de ato 
        Ato atoPrecastrado = atoRepository.buscarAtoPorNumeroECnpj(numeroAto, tipoAto,idUnidadeGestora );
        Ato novoAto;
        if(atoPrecastrado ==  null){
            novoAto = new Ato(numeroAto ,idUnidadeGestora , veiculoPublicacao , dataPublicacao, tipoAto );
            novoAto.setChave(atoRepository.buscarPrimeiraRemessa(idUnidadeGestora));
            atoRepository.save(novoAto);
            novoAto.setId(atoRepository.buscarAtoPorNumeroECnpj(numeroAto, tipoAto,idUnidadeGestora ).getId());
        }else{
            atoPrecastrado.setDataPublicacao(dataPublicacao);
            novoAto = atoPrecastrado;
        }

        if  (this.tiposRegistrosNaTabelaAposentadoria.contains(tipoRegistroEnum)){
            //movimentos derivados da tabela Aposentadoria
            Integer tipoAposentadoria = (Integer) camposParaAlterar.get("tipoAposentadoria");
            Aposentadoria aposentadoria = aposentadoriaRepository.findById(idMovimentacao);
            aposentadoria.setAto(novoAto);
            aposentadoria.setTipoAposentadoria(tipoAposentadoria);
            aposentadoriaRepository.update(aposentadoria);
        } else if (this.tiposRegistrosNaTabelaPensao.contains(tipoRegistroEnum)) {
            //movimentos derivados da tabela Pensao
            Pensao pensao = pensaoRepository.findById(idMovimentacao);
            pensao.setAto(novoAto);
            pensaoRepository.update(pensao);
        } else if (this.tiposRegistrosNaTabelaAdmissao.contains(tipoRegistroEnum)) {
            //movimentos derivados da tabela Admissao
            Admissao admissao = admissaoRepository.findById(idMovimentacao);
            admissao.setAto(novoAto);
            admissaoRepository.update(admissao);
        } else {
            if (atoPrecastrado == null) {
                atoRepository.deleteRestrito(novoAto.getId());
            }
            throw new IllegalArgumentException("Tipo de movimentacao não definido para alteração !!");
        
        }
    }

    private void validarMovimentacao(HashMap<String,Object> infoUser ,BigInteger idMovimentacao,Integer tipoRegistro){
        Registro.Tipo  tipoRegistroEnum =  Arrays.stream(Registro.Tipo.values()).filter(tipo -> tipo.getValor()==tipoRegistro).findFirst().get();
        HashMap<String,Object> infoMovimentacao= new HashMap<>();
        infoMovimentacao.put("assuntoProcessoEcontas",tipoRegistroEnum.getAssuntoProcessoEcontas());
        if  (this.tiposRegistrosNaTabelaAposentadoria.contains(tipoRegistroEnum) ){
            //movimentos derivados da tabela Aposentadoria
            Aposentadoria aposentadoria = aposentadoriaRepository.findById(idMovimentacao);
            infoMovimentacao.put("cpf",aposentadoria.getCpfServidor());
            infoMovimentacao.put("cnpjUnidadeGestora",aposentadoria.getAdmissao().getChave().getIdUnidadeGestora());
            if (aposentadoria ==null)
                throw new InvalitInsert("não encontrou a movimentacao de tipo "+tipoRegistroEnum.getLabel()+" para o id especificado!! ");
            if (!registroAposentadoriaRepository.temProcessoEcontasPorInteressado(infoUser,infoMovimentacao ) ){
                throw new InvalitInsert("a movimentação de tipo "+tipoRegistroEnum.getLabel()+" do cpf "+aposentadoria.getCpfServidor()+" não tem processo no Econtas com decisão julgada para o usuario atual!! ");
            }
        }
        else if (this.tiposRegistrosNaTabelaPensao.contains(tipoRegistroEnum)){
            //movimentos derivados da tabela Aposentadoria
            Pensao pensao = pensaoRepository.findById(idMovimentacao);
            infoMovimentacao.put("cpf",pensao.getCpfServidor());
            infoMovimentacao.put("cnpjUnidadeGestora",pensao.getAdmissao().getChave().getIdUnidadeGestora());
            if (pensao ==null)
                throw new InvalitInsert("não encontrou a movimentacao de tipo "+tipoRegistroEnum.getLabel()+" para o id especificado!! ");
            if (!registroAposentadoriaRepository.temProcessoEcontasPorInteressado(infoUser,infoMovimentacao ) ){
                throw new InvalitInsert("a movimentação "+tipoRegistroEnum.getLabel()+" do cpf "+pensao.getCpfServidor()+" não tem processso no Econtas com decisão julgada para o usuario atual!! ");
            }
        }
        else if (this.tiposRegistrosNaTabelaAdmissao.contains(tipoRegistroEnum)){
            //movimentos derivados da tabela Admissão
            Admissao admissao = admissaoRepository.findById(idMovimentacao);
            infoMovimentacao.put("cpf",admissao.getServidor().getCpfServidor());
            infoMovimentacao.put("cnpjUnidadeGestora",admissao.getChave().getIdUnidadeGestora());
            if (admissao ==null)
                throw new InvalitInsert("não encontrou a movimentacao de tipo "+tipoRegistroEnum.getLabel()+" para o id especificado!! ");
            if (!registroAposentadoriaRepository.temProcessoEcontasPorInteressado(infoUser,infoMovimentacao ) ){
                throw new InvalitInsert("a movimentação "+tipoRegistroEnum.getLabel()+" do cpf: "+admissao.getCpfServidor()+" e nome: "+admissao+" não ter processso no econtas com decisão julgada para o usuario atual!! ");
            }
        }
        else throw new IllegalArgumentException("Tipo de registro não encontrado !!");
    }

    private BigInteger getIdUsuarioFromToken(String token){
        try {
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload=token.split("\\.")[1];
            String payloadString = new String(decoder.decode(payload));
            JsonNode idusuariojson= (new ObjectMapper()).readTree(payloadString)  ;
            return  new BigInteger(idusuariojson.get("sub").asText());
        }catch (JsonProcessingException e){
            e.printStackTrace();
            throw new RuntimeException("autenticação invalida. favor entrar em contato com o administrador!! ");
        }
    }

    public void setInfoUsuarioEmFiltros(HashMap<String,String> hashfiltro,HashMap<String,Object> hashInfoUsuario){
        if (!hashInfoUsuario.isEmpty()){
            for (Map.Entry a : hashInfoUsuario.entrySet()){
                hashfiltro.put((String) a.getKey(),a.getValue().toString());
            }
        }
    }

    public HashMap<String,Object> getInfoUserFromToken(String token){
        BigInteger idUsuario=getIdUsuarioFromToken(token.substring(7));
        HashMap<String,Object> infoUser = registroAposentadoriaRepository.getUserInfoFromIdUsuarioAutenticacao(idUsuario);
        infoUser.put("setor",registroAposentadoriaRepository.getUserSetorFromLoginNoEcontas(infoUser.get("loginUsuario").toString()));
        return infoUser;
    }

    public boolean atosIquais(Ato ato, Ato atoComparado){
          if (  atoComparado == null ||  ato==null)
              return false;
          return ato.getNumeroAto() == atoComparado.getNumeroAto()
                  && ato.getTipoAto() == atoComparado.getTipoAto()
                  && ato.getChave().getIdUnidadeGestora() == atoComparado.getChave().getIdUnidadeGestora();
    }

}
