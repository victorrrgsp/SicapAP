package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.UnidadeGestora;
import br.gov.to.tce.validation.ValidationException;
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

//    @CrossOrigin
//    @GetMapping(path = {"/sessions"})
//    public ResponseEntity<?> find() {
//        String teste = config.jedis.get("0c77bfa7-0e59-42b8-91c1-36d479ee6488");
//
//        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
//        attr.getRequest().getSession().getAttribute("01499673140");
//        return ResponseEntity.ok().body("warley");
//    }


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
            //System.out.println("autenticar(): " + resposta);
            JsonNode respostaJson = new ObjectMapper().readTree(resposta);

            List<Object> lista = usuarioRepository.getUser(respostaJson.get("validacaoAssinatura").get("dados").get("cpf").asText(),
                    user.getSistema());
            if (lista == null || lista.isEmpty())
                throw new ValidationException("Usuário sem permissão ou certificado inválido");

            User userLogado = new User();

            lista.forEach(res -> {
                userLogado.setId(java.util.UUID.randomUUID().toString());
                userLogado.setCpf(respostaJson.get("validacaoAssinatura").get("dados").get("cpf").toString().replace("\"", ""));
                userLogado.setUserName(userLogado.getCpf());
                userLogado.setNome(respostaJson.get("validacaoAssinatura").get("dados").get("nome").toString());
                userLogado.setCertificado(resposta);
                userLogado.getDateEnd().addHours(2);
                userLogado.setUnidadeGestora(new UnidadeGestora(((Object[]) res)[1].toString(), ((Object[]) res)[2].toString(),
                        Integer.parseInt(((Object[]) res)[3].toString())));
                userLogado.setUnidadeGestoraList(userLogado.getUnidadeGestora());

                userLogado.setCargoByInteger(Integer.parseInt(((Object[]) res)[4].toString()));
            });
            //System.out.println(cpf);

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
            //MIIG%252FDCCBOSgAwIBAgIIfh8hBCk5wM0wDQYJKoZIhvcNAQELBQAwWTELMAkGA1UEBhMCQlIxEzARBgNVBAoTCklDUC1CcmFzaWwxFTATBgNVBAsTDEFDIFNPTFVUSSB2NTEeMBwGA1UEAxMVQUMgU09MVVRJIE11bHRpcGxhIHY1MB4XDTIxMDQyOTEzMzgwMFoXDTI0MDQyOTEzMzgwMFowgbcxCzAJBgNVBAYTAkJSMRMwEQYDVQQKEwpJQ1AtQnJhc2lsMR4wHAYDVQQLExVBQyBTT0xVVEkgTXVsdGlwbGEgdjUxFzAVBgNVBAsTDjMyMzg2MDg3MDAwMTczMRMwEQYDVQQLEwpQcmVzZW5jaWFsMRowGAYDVQQLExFDZXJ0aWZpY2FkbyBQRiBBMzEpMCcGA1UEAxMgV0FSTEVZIEZFUlJFSVJBIEdPSVM6MDE0OTk2NzMxNDAwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCiKqiznrUX%252BfHbHtLxgC0SqK%252F7lasjFfxN%252B6f26FTdGvxFjEV4Oo6slkgr9dWZB8FZ4fjNYZf0wkZwn2mgFfwEp%252B%252B%252FL%252FrZIIbIiYuUxopt7g5ZXqjzDH6bpMDCAsRfpdOy5JSXNpPtFAlRvo4wPLsIa0ApwKPtJPb440EGkIe9OAbaQgH2fheiXOoVQQjWgS%252FNpAAuVum6A9EN3twbuFs%252FArIkTEnIVEpB2DDOClxeAtkyFolKU2mSmeM5O2xh9lpWtHTcfGG34kSxg5I%252F39KHHPYMLkSOetyUUO0ayqWVcIvE9%252F5IkIdvNuCv7fN%252F%252BQFUXTuYbChf4EFVJWtx0rnTAgMBAAGjggJnMIICYzAJBgNVHRMEAjAAMB8GA1UdIwQYMBaAFMVS7SWACd%252BcgsifR8bdtF8x3bmxMFQGCCsGAQUFBwEBBEgwRjBEBggrBgEFBQcwAoY4aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvbGNyL2FjLXNvbHV0aS1tdWx0aXBsYS12NS5wN2IwgZYGA1UdEQSBjjCBi4EWd2FybGV5Zi5nb2lzQGdtYWlsLmNvbaA4BgVgTAEDAaAvEy0zMDAzMTk4NjAxNDk5NjczMTQwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDCgFwYFYEwBAwagDhMMMDAwMDAwMDAwMDAwoB4GBWBMAQMFoBUTEzAwMDAwMDAwMDAwMDAwMDAwMDAwXQYDVR0gBFYwVDBSBgZgTAECAyUwSDBGBggrBgEFBQcCARY6aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvZG9jcy9kcGMtYWMtc29sdXRpLW11bHRpcGxhLnBkZjApBgNVHSUEIjAgBggrBgEFBQcDAgYIKwYBBQUHAwQGCisGAQQBgjcUAgIwgYwGA1UdHwSBhDCBgTA%252BoDygOoY4aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvbGNyL2FjLXNvbHV0aS1tdWx0aXBsYS12NS5jcmwwP6A9oDuGOWh0dHA6Ly9jY2QyLmFjc29sdXRpLmNvbS5ici9sY3IvYWMtc29sdXRpLW11bHRpcGxhLXY1LmNybDAdBgNVHQ4EFgQUfrciVM4ebVGlvQ3gMCYaoBzz1kwwDgYDVR0PAQH%252FBAQDAgXgMA0GCSqGSIb3DQEBCwUAA4ICAQAASFGySyuNBckwYMuE6XiJ%252FGFRkKOMNAKFuv6ZLq18cVbzU7rcYK%252FFJ5LHZ1k1ZNbfBnyEpaG8yqjzQi6qHKT%252FbJn8Umj%252FgSS4WykCYWUbPZHN3d4Gd9Mhuwndl2m3eQVWkuVjVwVH39oRyaaOos2HwEuvztEsXrOwHGphpgkyYlqKPLvv1yKmCJER8XJR8Lb%252FjfCBe3cTDZQ4pNim29i4aybB1SQeI8xHQa%252BXzc5tWcSm3H5npL7We28DLpD6ZgYwIDR71ZLRzF%252Fesrdvd3Lrj9rSG068PfXKHcpe%252FsvqkfbdCvJF8ssuaAh%252BphjwbbXpW5oFnCNuVXuhdfiA7j45aESIHdZWuibXbBEhF%252Fe9iS1XRBiHrG6lfoPnR2L3TTvw1MLnWgEDdfCfDNlF4y3gNrwVnECrS3sOmUMoT05MCiiwt%252BftNEgJlw9QHeux3uc1cNyKFSldJLpjASNJr0B3F1k4Zi7eDNthr9e7LKLnxBznbefT81P6rO3HE5SYsubtRi8yM1naNGRiwO8bn1rIrF63YUzRwJyiJ5vP6A3FY7fh8E9IDMC76Q9Myw4hZZPhKCvdLZechj6wg1SzCyy7G7yNg1eavCe3C70dFHnSTC%252Bd1Xj0NMtGdr3KINqZVLothSwv1mw%252FrtBWStDwOxtsFEnD94LuSfrT9CK5DprJZg%253D%253D
            //certificado = " MIIG%2FDCCBOSgAwIBAgIIfh8hBCk5wM0wDQYJKoZIhvcNAQELBQAwWTELMAkGA1UEBhMCQlIxEzARBgNVBAoTCklDUC1CcmFzaWwxFTATBgNVBAsTDEFDIFNPTFVUSSB2NTEeMBwGA1UEAxMVQUMgU09MVVRJIE11bHRpcGxhIHY1MB4XDTIxMDQyOTEzMzgwMFoXDTI0MDQyOTEzMzgwMFowgbcxCzAJBgNVBAYTAkJSMRMwEQYDVQQKEwpJQ1AtQnJhc2lsMR4wHAYDVQQLExVBQyBTT0xVVEkgTXVsdGlwbGEgdjUxFzAVBgNVBAsTDjMyMzg2MDg3MDAwMTczMRMwEQYDVQQLEwpQcmVzZW5jaWFsMRowGAYDVQQLExFDZXJ0aWZpY2FkbyBQRiBBMzEpMCcGA1UEAxMgV0FSTEVZIEZFUlJFSVJBIEdPSVM6MDE0OTk2NzMxNDAwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCiKqiznrUX%2BfHbHtLxgC0SqK%2F7lasjFfxN%2B6f26FTdGvxFjEV4Oo6slkgr9dWZB8FZ4fjNYZf0wkZwn2mgFfwEp%2B%2B%2FL%2FrZIIbIiYuUxopt7g5ZXqjzDH6bpMDCAsRfpdOy5JSXNpPtFAlRvo4wPLsIa0ApwKPtJPb440EGkIe9OAbaQgH2fheiXOoVQQjWgS%2FNpAAuVum6A9EN3twbuFs%2FArIkTEnIVEpB2DDOClxeAtkyFolKU2mSmeM5O2xh9lpWtHTcfGG34kSxg5I%2F39KHHPYMLkSOetyUUO0ayqWVcIvE9%2F5IkIdvNuCv7fN%2F%2BQFUXTuYbChf4EFVJWtx0rnTAgMBAAGjggJnMIICYzAJBgNVHRMEAjAAMB8GA1UdIwQYMBaAFMVS7SWACd%2BcgsifR8bdtF8x3bmxMFQGCCsGAQUFBwEBBEgwRjBEBggrBgEFBQcwAoY4aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvbGNyL2FjLXNvbHV0aS1tdWx0aXBsYS12NS5wN2IwgZYGA1UdEQSBjjCBi4EWd2FybGV5Zi5nb2lzQGdtYWlsLmNvbaA4BgVgTAEDAaAvEy0zMDAzMTk4NjAxNDk5NjczMTQwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDCgFwYFYEwBAwagDhMMMDAwMDAwMDAwMDAwoB4GBWBMAQMFoBUTEzAwMDAwMDAwMDAwMDAwMDAwMDAwXQYDVR0gBFYwVDBSBgZgTAECAyUwSDBGBggrBgEFBQcCARY6aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvZG9jcy9kcGMtYWMtc29sdXRpLW11bHRpcGxhLnBkZjApBgNVHSUEIjAgBggrBgEFBQcDAgYIKwYBBQUHAwQGCisGAQQBgjcUAgIwgYwGA1UdHwSBhDCBgTA%2BoDygOoY4aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvbGNyL2FjLXNvbHV0aS1tdWx0aXBsYS12NS5jcmwwP6A9oDuGOWh0dHA6Ly9jY2QyLmFjc29sdXRpLmNvbS5ici9sY3IvYWMtc29sdXRpLW11bHRpcGxhLXY1LmNybDAdBgNVHQ4EFgQUfrciVM4ebVGlvQ3gMCYaoBzz1kwwDgYDVR0PAQH%2FBAQDAgXgMA0GCSqGSIb3DQEBCwUAA4ICAQAASFGySyuNBckwYMuE6XiJ%2FGFRkKOMNAKFuv6ZLq18cVbzU7rcYK%2FFJ5LHZ1k1ZNbfBnyEpaG8yqjzQi6qHKT%2FbJn8Umj%2FgSS4WykCYWUbPZHN3d4Gd9Mhuwndl2m3eQVWkuVjVwVH39oRyaaOos2HwEuvztEsXrOwHGphpgkyYlqKPLvv1yKmCJER8XJR8Lb%2FjfCBe3cTDZQ4pNim29i4aybB1SQeI8xHQa%2BXzc5tWcSm3H5npL7We28DLpD6ZgYwIDR71ZLRzF%2Fesrdvd3Lrj9rSG068PfXKHcpe%2FsvqkfbdCvJF8ssuaAh%2BphjwbbXpW5oFnCNuVXuhdfiA7j45aESIHdZWuibXbBEhF%2Fe9iS1XRBiHrG6lfoPnR2L3TTvw1MLnWgEDdfCfDNlF4y3gNrwVnECrS3sOmUMoT05MCiiwt%2BftNEgJlw9QHeux3uc1cNyKFSldJLpjASNJr0B3F1k4Zi7eDNthr9e7LKLnxBznbefT81P6rO3HE5SYsubtRi8yM1naNGRiwO8bn1rIrF63YUzRwJyiJ5vP6A3FY7fh8E9IDMC76Q9Myw4hZZPhKCvdLZechj6wg1SzCyy7G7yNg1eavCe3C70dFHnSTC%2Bd1Xj0NMtGdr3KINqZVLothSwv1mw%2FrtBWStDwOxtsFEnD94LuSfrT9CK5DprJZg%3D%3D";
//
//            String resposta = Login.getDesafio(user.getCertificado());

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


}
