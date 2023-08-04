package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.UnidadeGestora;
import br.gov.to.tce.validation.ValidationException;
import com.example.sicapweb.repository.geral.AdmSistemaRepository;
import com.example.sicapweb.repository.geral.UnidadeGestoraRepository;
import com.example.sicapweb.repository.geral.UsuarioRepository;
import com.example.sicapweb.security.Config;
import com.example.sicapweb.security.Session;
import com.example.sicapweb.security.User;
import com.example.sicapweb.service.Login;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.RequestBody;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.Key;
import java.sql.Date;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/tcetouser")
public class LoginController extends DefaultController<Login> {

    @Autowired
    private Config config;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AdmSistemaRepository admSistemaRepository;

    @Autowired
    private UnidadeGestoraRepository unidadeGestoraRepository;

    @CrossOrigin
    @Transactional
    @PostMapping(path = {"/session/"})
    public ResponseEntity<?> find(@org.springframework.web.bind.annotation.RequestBody String user) throws ValidationException {

        if (user == null || user.trim().isEmpty() || config.jedis.get(user.replace("=", "")) == null)
            throw new ValidationException("Usuário não autenticado");

        User userSession = Config.fromJson(config.jedis.get(user.replace("=", "")), User.class);
        try {
            new br.gov.to.tce.util.Date(userSession.getDateEnd().toStringDateAndHourDatabaseFormat2());
        } catch (ParseException e) {
            throw new ValidationException(e.getMessage());
        }
        if (!userSession.isValid()) {
            config.jedis.del(user.replace("=", ""));
            throw new ValidationException("Sessão inválida");
        }
        config.jedis.expire(user.replace("=", ""), 480L);
        Session.usuarioLogado = userSession;
        return ResponseEntity.ok().body(true);
    }

    @CrossOrigin
    @Transactional
    @PostMapping(path = {"/logout/"})
    public ResponseEntity<?> logout(@org.springframework.web.bind.annotation.RequestBody String user) {
        config.jedis.del(user.replace("=", ""));
        Session.usuarioLogado = null;

        return ResponseEntity.ok().body(true);
    }

    @CrossOrigin
    @Transactional
    @GetMapping(path = {"/getugs"})
    public ResponseEntity<?> getUg() {
        String user = User.getUser(usuarioRepository.getRequest()).getId();
        user = user.replace("=", "");
        User userLogado = Config.fromJson(config.jedis.get(user.replace("=", "")), User.class);
        return ResponseEntity.ok().body(userLogado.getUnidadeGestoraList());
    }

    @CrossOrigin
    @Transactional
    @PostMapping(path = {"/setug/"})
    public ResponseEntity<?> setUg(@org.springframework.web.bind.annotation.RequestBody String idUnidadeGestora) {
        String user = User.getUser(usuarioRepository.getRequest()).getId();
        user = user.replace("=", "");
        idUnidadeGestora = idUnidadeGestora.replace("=", "");

        User userLogado = Config.fromJson(config.jedis.get(user.replace("=", "")), User.class);
        String finalIdUnidadeGestora = idUnidadeGestora;
        userLogado.setUnidadeGestora(userLogado.getUnidadeGestoraList()
                .stream().filter(o -> o.getId().equalsIgnoreCase(finalIdUnidadeGestora.trim()))
                .findFirst().get());

        config.jedis.del(user.replace("=", ""));
        config.jedis.set(userLogado.getId(), Config.json(userLogado));
        return ResponseEntity.ok().body(true);
    }


    @CrossOrigin
    @Transactional
    @PostMapping(path = {"/autenticar"})
    public ResponseEntity<?> autenticar(@org.springframework.web.bind.annotation.RequestBody User user) {

        try {
            ServletRequestAttributes getIp = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            OkHttpClient client = new OkHttpClient().newBuilder().build();

            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            String postdata = "ipUsuario=" + getIp.getRequest().getRemoteAddr() +
                    "&desafio=" + user.getUserName() + "&cifrado=" + user.getCertificado();
            RequestBody body = RequestBody.create(mediaType, postdata);
            Request request = new Request.Builder().url("http://172.30.0.35:8080/AssinaturaDigitalV3/AutenticacaoLogin")
                    .method("POST", body)
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .addHeader("Referer", "https://app.tce.to.gov.br/")
                    .build();
            Response response = client.newCall(request).execute();

            String resposta = response.body().string();
            JsonNode respostaJson = new ObjectMapper().readTree(resposta);

            User userLogado = new User();
            var cpf = respostaJson.get("validacaoAssinatura").get("dados").get("cpf").asText();
            var usuario = admSistemaRepository.buscarAdmSistema(cpf);

            if (usuario != null) {
                var unidades = unidadeGestoraRepository.buscaTodasUnidadeGestora();
                unidades.forEach(res -> {
                    userLogado.setId(java.util.UUID.randomUUID().toString());
                    userLogado.setCpf(respostaJson.get("validacaoAssinatura").get("dados").get("cpf").toString().replace("\"", ""));
                    userLogado.setUserName(userLogado.getCpf());
                    userLogado.setNome(respostaJson.get("validacaoAssinatura").get("dados").get("nome").toString());
                    userLogado.setCertificado(resposta);
                    userLogado.setHashCertificado(user.getHashCertificado());
                    userLogado.getDateEnd().addHours(2);
                    userLogado.setUnidadeGestora(res);
                    userLogado.setUnidadeGestoraList(userLogado.getUnidadeGestora());

                    userLogado.setCargoByInteger(usuario.getIdCargo());
                });
            } else {
                List<Object> lista = usuarioRepository.getUser(respostaJson.get("validacaoAssinatura").get("dados").get("cpf").asText(),
                        user.getSistema());
                if (lista == null || lista.isEmpty())
                    throw new ValidationException("Usuário sem permissão ou certificado inválido");

                lista.forEach(res -> {
                    userLogado.setId(java.util.UUID.randomUUID().toString());
                    userLogado.setCpf(respostaJson.get("validacaoAssinatura").get("dados").get("cpf").toString().replace("\"", ""));
                    userLogado.setUserName(userLogado.getCpf());
                    userLogado.setNome(respostaJson.get("validacaoAssinatura").get("dados").get("nome").toString());
                    userLogado.setCertificado(resposta);
                    userLogado.setHashCertificado(user.getHashCertificado());
                    userLogado.getDateEnd().addHours(2);
                    userLogado.setUnidadeGestora(new UnidadeGestora(((Object[]) res)[1].toString(), ((Object[]) res)[2].toString(),
                            Integer.parseInt(((Object[]) res)[3].toString())));
                    userLogado.setUnidadeGestoraList(userLogado.getUnidadeGestora());

                    userLogado.setCargoByInteger(Integer.parseInt(((Object[]) res)[4].toString()));
                });
            }

            Session.setUsuario(userLogado);
            getIp.getRequest().getSession().setAttribute(userLogado.getCpf(), userLogado);
            config.jedis.set(userLogado.getId(), Config.json(userLogado));

            return ResponseEntity.ok().body(userLogado.getId());
        } catch (Exception e) {
            System.out.println("[falha]: " + e.toString());
            e.printStackTrace();
        }

        return ResponseEntity.ok().body("SemPermissao");
    }

    @CrossOrigin
    @Transactional
    @PostMapping(path = {"/desafio/"})
    public ResponseEntity<?> getDesafio(@org.springframework.web.bind.annotation.RequestBody String user) {

        try {
            String resposta = Login.getDesafio(user);
            return !resposta.contains("\"success\":false") ?
                    ResponseEntity.ok().body(resposta) : ResponseEntity.ok().body(null);

        } catch (Exception e) {
            System.out.println("[falha]: " + e.toString());
            e.printStackTrace();
        }

        return ResponseEntity.ok().body(null);
    }


    public static String createJWT(String id, String issuer, String subject, long ttlMillis) {

        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("SECRET_KEY");
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(signatureAlgorithm, signingKey);

        //if it has been specified, let's add the expiration
        if (ttlMillis > 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    public static Claims decodeJWT(String jwt) {
        //This line will throw an exception if it is not a signed JWS (as expected)
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary("SECRET_KEY"))
                .parseClaimsJws(jwt).getBody();
        return claims;
    }

    public static void main(String[] args) {
        String c = createJWT("sdfsfd", "sdgsdfsdf", "sfsdfsdf", -1);
        decodeJWT(c);
    }


    public static String obterToken() {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url("https://app.tce.to.gov.br/assinador/api/AssinadorWeb/obterToken")
                    .method("POST", body)
                    .addHeader("Accept", "application/json")
                    .build();
            Response response = client.newCall(request).execute();
            String resposta = response.body().string();

            return resposta;

        } catch (Exception e) {
            // System.out.println("[falha]: " + e.toString());
        }

        return null;

    }


    public static String autenticar1(String ipUsuario, String desafio, String cifrado) throws IOException {

        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            String postdata = "ipUsuario=" + ipUsuario + "&desafio=" + desafio + "&cifrado=" + cifrado;
            RequestBody body = RequestBody.create(mediaType, postdata);
            Request request = new Request.Builder().url("http://172.30.0.35:8080/AssinaturaDigitalV3/AutenticacaoLogin")
                    .method("POST", body).addHeader("Content-Type", "application/x-www-form-urlencoded").build();
            Response response = client.newCall(request).execute();

            String resposta = response.body().string();
            // System.out.println(resposta);

            JsonNode respostaJson = new ObjectMapper().readTree(resposta);
            String cpf = respostaJson.get("validacaoAssinatura").get("dados").get("cpf").asText();
            //System.out.println(cpf);

            return cpf;

        } catch (Exception e) {
            // System.out.println("[falha]: " + e.toString());
        }

        return null;

    }


    //############################################ API REINICIAR SEVER #####################################################
    @GetMapping("/script")
    public void executeCommand() {
        String[] env = {"PATH=/bin:/usr/local/bin/"};

        String cmd = "reiniciarSicapWeb.sh";  //e.g test.sh -dparam1 -oout.txt
        //tratamento de erro e execução do script

        try {
            System.out.println(env);

            Process process = Runtime.getRuntime().exec(cmd, env);
            System.out.println("tste:" + process);

        } catch (IOException ex) {
            System.out.println(ex);
            //  Logger.getLogger(TecMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
