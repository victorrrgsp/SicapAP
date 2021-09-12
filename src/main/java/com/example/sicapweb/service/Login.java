package com.example.sicapweb.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.*;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.sql.Date;

public class Login {

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

    public static String obterTokenNativo(String ipUsuario) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("http://172.30.0.35:8080/AssinaturaDigitalV3/AssinadorGerarToken?ipUsuario=" + ipUsuario)
                    .method("GET", null)
                    .build();
            Response response = client.newCall(request).execute();
            String resposta = response.body().string();

            return resposta;

        } catch (Exception e) {
            // System.out.println("[falha]: " + e.toString());
        }

        return null;
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

    public static String autenticar(String ipUsuario, String desafio, String cifrado) {

        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();

            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            String postdata = "ipUsuario=" + ipUsuario + "&desafio=" + desafio + "&cifrado=" + cifrado;
            RequestBody body = RequestBody.create(mediaType, postdata);
            Request request = new Request.Builder().url("http://172.30.0.35:8080/AssinaturaDigitalV3/AutenticacaoLogin")
                    .method("POST", body).addHeader("Content-Type", "application/x-www-form-urlencoded").build();
            Response response = client.newCall(request).execute();

            String resposta = response.body().string();
            System.out.println("autenticar(): " + resposta);

            JsonNode respostaJson = new ObjectMapper().readTree(resposta);
            String cpf = respostaJson.get("validacaoAssinatura").get("dados").get("cpf").asText();
            //System.out.println(cpf);

            return cpf;

        } catch (Exception e) {
            // System.out.println("[falha]: " + e.toString());
        }

        return null;
    }

    public static String getDesafio(String certificado) throws Exception {
//MIIG%2FDCCBOSgAwIBAgIIfh8hBCk5wM0wDQYJKoZIhvcNAQELBQAwWTELMAkGA1UEBhMCQlIxEzARBgNVBAoTCklDUC1CcmFzaWwxFTATBgNVBAsTDEFDIFNPTFVUSSB2NTEeMBwGA1UEAxMVQUMgU09MVVRJIE11bHRpcGxhIHY1MB4XDTIxMDQyOTEzMzgwMFoXDTI0MDQyOTEzMzgwMFowgbcxCzAJBgNVBAYTAkJSMRMwEQYDVQQKEwpJQ1AtQnJhc2lsMR4wHAYDVQQLExVBQyBTT0xVVEkgTXVsdGlwbGEgdjUxFzAVBgNVBAsTDjMyMzg2MDg3MDAwMTczMRMwEQYDVQQLEwpQcmVzZW5jaWFsMRowGAYDVQQLExFDZXJ0aWZpY2FkbyBQRiBBMzEpMCcGA1UEAxMgV0FSTEVZIEZFUlJFSVJBIEdPSVM6MDE0OTk2NzMxNDAwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCiKqiznrUX+fHbHtLxgC0SqK%2F7lasjFfxN+6f26FTdGvxFjEV4Oo6slkgr9dWZB8FZ4fjNYZf0wkZwn2mgFfwEp++%2FL%2FrZIIbIiYuUxopt7g5ZXqjzDH6bpMDCAsRfpdOy5JSXNpPtFAlRvo4wPLsIa0ApwKPtJPb440EGkIe9OAbaQgH2fheiXOoVQQjWgS%2FNpAAuVum6A9EN3twbuFs%2FArIkTEnIVEpB2DDOClxeAtkyFolKU2mSmeM5O2xh9lpWtHTcfGG34kSxg5I%2F39KHHPYMLkSOetyUUO0ayqWVcIvE9%2F5IkIdvNuCv7fN%2F+QFUXTuYbChf4EFVJWtx0rnTAgMBAAGjggJnMIICYzAJBgNVHRMEAjAAMB8GA1UdIwQYMBaAFMVS7SWACd+cgsifR8bdtF8x3bmxMFQGCCsGAQUFBwEBBEgwRjBEBggrBgEFBQcwAoY4aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvbGNyL2FjLXNvbHV0aS1tdWx0aXBsYS12NS5wN2IwgZYGA1UdEQSBjjCBi4EWd2FybGV5Zi5nb2lzQGdtYWlsLmNvbaA4BgVgTAEDAaAvEy0zMDAzMTk4NjAxNDk5NjczMTQwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDCgFwYFYEwBAwagDhMMMDAwMDAwMDAwMDAwoB4GBWBMAQMFoBUTEzAwMDAwMDAwMDAwMDAwMDAwMDAwXQYDVR0gBFYwVDBSBgZgTAECAyUwSDBGBggrBgEFBQcCARY6aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvZG9jcy9kcGMtYWMtc29sdXRpLW11bHRpcGxhLnBkZjApBgNVHSUEIjAgBggrBgEFBQcDAgYIKwYBBQUHAwQGCisGAQQBgjcUAgIwgYwGA1UdHwSBhDCBgTA+oDygOoY4aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvbGNyL2FjLXNvbHV0aS1tdWx0aXBsYS12NS5jcmwwP6A9oDuGOWh0dHA6Ly9jY2QyLmFjc29sdXRpLmNvbS5ici9sY3IvYWMtc29sdXRpLW11bHRpcGxhLXY1LmNybDAdBgNVHQ4EFgQUfrciVM4ebVGlvQ3gMCYaoBzz1kwwDgYDVR0PAQH%2FBAQDAgXgMA0GCSqGSIb3DQEBCwUAA4ICAQAASFGySyuNBckwYMuE6XiJ%2FGFRkKOMNAKFuv6ZLq18cVbzU7rcYK%2FFJ5LHZ1k1ZNbfBnyEpaG8yqjzQi6qHKT%2FbJn8Umj%2FgSS4WykCYWUbPZHN3d4Gd9Mhuwndl2m3eQVWkuVjVwVH39oRyaaOos2HwEuvztEsXrOwHGphpgkyYlqKPLvv1yKmCJER8XJR8Lb%2FjfCBe3cTDZQ4pNim29i4aybB1SQeI8xHQa+Xzc5tWcSm3H5npL7We28DLpD6ZgYwIDR71ZLRzF%2Fesrdvd3Lrj9rSG068PfXKHcpe%2FsvqkfbdCvJF8ssuaAh+phjwbbXpW5oFnCNuVXuhdfiA7j45aESIHdZWuibXbBEhF%2Fe9iS1XRBiHrG6lfoPnR2L3TTvw1MLnWgEDdfCfDNlF4y3gNrwVnECrS3sOmUMoT05MCiiwt+ftNEgJlw9QHeux3uc1cNyKFSldJLpjASNJr0B3F1k4Zi7eDNthr9e7LKLnxBznbefT81P6rO3HE5SYsubtRi8yM1naNGRiwO8bn1rIrF63YUzRwJyiJ5vP6A3FY7fh8E9IDMC76Q9Myw4hZZPhKCvdLZechj6wg1SzCyy7G7yNg1eavCe3C70dFHnSTC+d1Xj0NMtGdr3KINqZVLothSwv1mw%2FrtBWStDwOxtsFEnD94LuSfrT9CK5DprJZg=%3D
        //MIIG%2FDCCBOSgAwIBAgIIfh8hBCk5wM0wDQYJKoZIhvcNAQELBQAwWTELMAkGA1UEBhMCQlIxEzARBgNVBAoTCklDUC1CcmFzaWwxFTATBgNVBAsTDEFDIFNPTFVUSSB2NTEeMBwGA1UEAxMVQUMgU09MVVRJIE11bHRpcGxhIHY1MB4XDTIxMDQyOTEzMzgwMFoXDTI0MDQyOTEzMzgwMFowgbcxCzAJBgNVBAYTAkJSMRMwEQYDVQQKEwpJQ1AtQnJhc2lsMR4wHAYDVQQLExVBQyBTT0xVVEkgTXVsdGlwbGEgdjUxFzAVBgNVBAsTDjMyMzg2MDg3MDAwMTczMRMwEQYDVQQLEwpQcmVzZW5jaWFsMRowGAYDVQQLExFDZXJ0aWZpY2FkbyBQRiBBMzEpMCcGA1UEAxMgV0FSTEVZIEZFUlJFSVJBIEdPSVM6MDE0OTk2NzMxNDAwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCiKqiznrUX%2BfHbHtLxgC0SqK%2F7lasjFfxN%2B6f26FTdGvxFjEV4Oo6slkgr9dWZB8FZ4fjNYZf0wkZwn2mgFfwEp%2B%2B%2FL%2FrZIIbIiYuUxopt7g5ZXqjzDH6bpMDCAsRfpdOy5JSXNpPtFAlRvo4wPLsIa0ApwKPtJPb440EGkIe9OAbaQgH2fheiXOoVQQjWgS%2FNpAAuVum6A9EN3twbuFs%2FArIkTEnIVEpB2DDOClxeAtkyFolKU2mSmeM5O2xh9lpWtHTcfGG34kSxg5I%2F39KHHPYMLkSOetyUUO0ayqWVcIvE9%2F5IkIdvNuCv7fN%2F%2BQFUXTuYbChf4EFVJWtx0rnTAgMBAAGjggJnMIICYzAJBgNVHRMEAjAAMB8GA1UdIwQYMBaAFMVS7SWACd%2BcgsifR8bdtF8x3bmxMFQGCCsGAQUFBwEBBEgwRjBEBggrBgEFBQcwAoY4aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvbGNyL2FjLXNvbHV0aS1tdWx0aXBsYS12NS5wN2IwgZYGA1UdEQSBjjCBi4EWd2FybGV5Zi5nb2lzQGdtYWlsLmNvbaA4BgVgTAEDAaAvEy0zMDAzMTk4NjAxNDk5NjczMTQwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDCgFwYFYEwBAwagDhMMMDAwMDAwMDAwMDAwoB4GBWBMAQMFoBUTEzAwMDAwMDAwMDAwMDAwMDAwMDAwXQYDVR0gBFYwVDBSBgZgTAECAyUwSDBGBggrBgEFBQcCARY6aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvZG9jcy9kcGMtYWMtc29sdXRpLW11bHRpcGxhLnBkZjApBgNVHSUEIjAgBggrBgEFBQcDAgYIKwYBBQUHAwQGCisGAQQBgjcUAgIwgYwGA1UdHwSBhDCBgTA%2BoDygOoY4aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvbGNyL2FjLXNvbHV0aS1tdWx0aXBsYS12NS5jcmwwP6A9oDuGOWh0dHA6Ly9jY2QyLmFjc29sdXRpLmNvbS5ici9sY3IvYWMtc29sdXRpLW11bHRpcGxhLXY1LmNybDAdBgNVHQ4EFgQUfrciVM4ebVGlvQ3gMCYaoBzz1kwwDgYDVR0PAQH%2FBAQDAgXgMA0GCSqGSIb3DQEBCwUAA4ICAQAASFGySyuNBckwYMuE6XiJ%2FGFRkKOMNAKFuv6ZLq18cVbzU7rcYK%2FFJ5LHZ1k1ZNbfBnyEpaG8yqjzQi6qHKT%2FbJn8Umj%2FgSS4WykCYWUbPZHN3d4Gd9Mhuwndl2m3eQVWkuVjVwVH39oRyaaOos2HwEuvztEsXrOwHGphpgkyYlqKPLvv1yKmCJER8XJR8Lb%2FjfCBe3cTDZQ4pNim29i4aybB1SQeI8xHQa%2BXzc5tWcSm3H5npL7We28DLpD6ZgYwIDR71ZLRzF%2Fesrdvd3Lrj9rSG068PfXKHcpe%2FsvqkfbdCvJF8ssuaAh%2BphjwbbXpW5oFnCNuVXuhdfiA7j45aESIHdZWuibXbBEhF%2Fe9iS1XRBiHrG6lfoPnR2L3TTvw1MLnWgEDdfCfDNlF4y3gNrwVnECrS3sOmUMoT05MCiiwt%2BftNEgJlw9QHeux3uc1cNyKFSldJLpjASNJr0B3F1k4Zi7eDNthr9e7LKLnxBznbefT81P6rO3HE5SYsubtRi8yM1naNGRiwO8bn1rIrF63YUzRwJyiJ5vP6A3FY7fh8E9IDMC76Q9Myw4hZZPhKCvdLZechj6wg1SzCyy7G7yNg1eavCe3C70dFHnSTC%2Bd1Xj0NMtGdr3KINqZVLothSwv1mw%2FrtBWStDwOxtsFEnD94LuSfrT9CK5DprJZg%3D%3D=
        //MIIG%2FDCCBOSgAwIBAgIIfh8hBCk5wM0wDQYJKoZIhvcNAQELBQAwWTELMAkGA1UEBhMCQlIxEzARBgNVBAoTCklDUC1CcmFzaWwxFTATBgNVBAsTDEFDIFNPTFVUSSB2NTEeMBwGA1UEAxMVQUMgU09MVVRJIE11bHRpcGxhIHY1MB4XDTIxMDQyOTEzMzgwMFoXDTI0MDQyOTEzMzgwMFowgbcxCzAJBgNVBAYTAkJSMRMwEQYDVQQKEwpJQ1AtQnJhc2lsMR4wHAYDVQQLExVBQyBTT0xVVEkgTXVsdGlwbGEgdjUxFzAVBgNVBAsTDjMyMzg2MDg3MDAwMTczMRMwEQYDVQQLEwpQcmVzZW5jaWFsMRowGAYDVQQLExFDZXJ0aWZpY2FkbyBQRiBBMzEpMCcGA1UEAxMgV0FSTEVZIEZFUlJFSVJBIEdPSVM6MDE0OTk2NzMxNDAwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCiKqiznrUX%2BfHbHtLxgC0SqK%27lasjFfxN+6f26FTdGvxFjEV4Oo6slkgr9dWZB8FZ4fjNYZf0wkZwn2mgFfwEp++/L/rZIIbIiYuUxopt7g5ZXqjzDH6bpMDCAsRfpdOy5JSXNpPtFAlRvo4wPLsIa0ApwKPtJPb440EGkIe9OAbaQgH2fheiXOoVQQjWgS/NpAAuVum6A9EN3twbuFs/ArIkTEnIVEpB2DDOClxeAtkyFolKU2mSmeM5O2xh9lpWtHTcfGG34kSxg5I/39KHHPYMLkSOetyUUO0ayqWVcIvE9/5IkIdvNuCv7fN/+QFUXTuYbChf4EFVJWtx0rnTAgMBAAGjggJnMIICYzAJBgNVHRMEAjAAMB8GA1UdIwQYMBaAFMVS7SWACd+cgsifR8bdtF8x3bmxMFQGCCsGAQUFBwEBBEgwRjBEBggrBgEFBQcwAoY4aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvbGNyL2FjLXNvbHV0aS1tdWx0aXBsYS12NS5wN2IwgZYGA1UdEQSBjjCBi4EWd2FybGV5Zi5nb2lzQGdtYWlsLmNvbaA4BgVgTAEDAaAvEy0zMDAzMTk4NjAxNDk5NjczMTQwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDCgFwYFYEwBAwagDhMMMDAwMDAwMDAwMDAwoB4GBWBMAQMFoBUTEzAwMDAwMDAwMDAwMDAwMDAwMDAwXQYDVR0gBFYwVDBSBgZgTAECAyUwSDBGBggrBgEFBQcCARY6aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvZG9jcy9kcGMtYWMtc29sdXRpLW11bHRpcGxhLnBkZjApBgNVHSUEIjAgBggrBgEFBQcDAgYIKwYBBQUHAwQGCisGAQQBgjcUAgIwgYwGA1UdHwSBhDCBgTA+oDygOoY4aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvbGNyL2FjLXNvbHV0aS1tdWx0aXBsYS12NS5jcmwwP6A9oDuGOWh0dHA6Ly9jY2QyLmFjc29sdXRpLmNvbS5ici9sY3IvYWMtc29sdXRpLW11bHRpcGxhLXY1LmNybDAdBgNVHQ4EFgQUfrciVM4ebVGlvQ3gMCYaoBzz1kwwDgYDVR0PAQH/BAQDAgXgMA0GCSqGSIb3DQEBCwUAA4ICAQAASFGySyuNBckwYMuE6XiJ/GFRkKOMNAKFuv6ZLq18cVbzU7rcYK/FJ5LHZ1k1ZNbfBnyEpaG8yqjzQi6qHKT/bJn8Umj/gSS4WykCYWUbPZHN3d4Gd9Mhuwndl2m3eQVWkuVjVwVH39oRyaaOos2HwEuvztEsXrOwHGphpgkyYlqKPLvv1yKmCJER8XJR8Lb/jfCBe3cTDZQ4pNim29i4aybB1SQeI8xHQa+Xzc5tWcSm3H5npL7We28DLpD6ZgYwIDR71ZLRzF/esrdvd3Lrj9rSG068PfXKHcpe/svqkfbdCvJF8ssuaAh+phjwbbXpW5oFnCNuVXuhdfiA7j45aESIHdZWuibXbBEhF/e9iS1XRBiHrG6lfoPnR2L3TTvw1MLnWgEDdfCfDNlF4y3gNrwVnECrS3sOmUMoT05MCiiwt+ftNEgJlw9QHeux3uc1cNyKFSldJLpjASNJr0B3F1k4Zi7eDNthr9e7LKLnxBznbefT81P6rO3HE5SYsubtRi8yM1naNGRiwO8bn1rIrF63YUzRwJyiJ5vP6A3FY7fh8E9IDMC76Q9Myw4hZZPhKCvdLZechj6wg1SzCyy7G7yNg1eavCe3C70dFHnSTC+d1Xj0NMtGdr3KINqZVLothSwv1mw/rtBWStDwOxtsFEnD94LuSfrT9CK5DprJZg==

        //MIIG%252FDCCBOSgAwIBAgIIfh8hBCk5wM0wDQYJKoZIhvcNAQELBQAwWTELMAkGA1UEBhMCQlIxEzARBgNVBAoTCklDUC1CcmFzaWwxFTATBgNVBAsTDEFDIFNPTFVUSSB2NTEeMBwGA1UEAxMVQUMgU09MVVRJIE11bHRpcGxhIHY1MB4XDTIxMDQyOTEzMzgwMFoXDTI0MDQyOTEzMzgwMFowgbcxCzAJBgNVBAYTAkJSMRMwEQYDVQQKEwpJQ1AtQnJhc2lsMR4wHAYDVQQLExVBQyBTT0xVVEkgTXVsdGlwbGEgdjUxFzAVBgNVBAsTDjMyMzg2MDg3MDAwMTczMRMwEQYDVQQLEwpQcmVzZW5jaWFsMRowGAYDVQQLExFDZXJ0aWZpY2FkbyBQRiBBMzEpMCcGA1UEAxMgV0FSTEVZIEZFUlJFSVJBIEdPSVM6MDE0OTk2NzMxNDAwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCiKqiznrUX%252BfHbHtLxgC0SqK%252F7lasjFfxN%252B6f26FTdGvxFjEV4Oo6slkgr9dWZB8FZ4fjNYZf0wkZwn2mgFfwEp%252B%252B%252FL%252FrZIIbIiYuUxopt7g5ZXqjzDH6bpMDCAsRfpdOy5JSXNpPtFAlRvo4wPLsIa0ApwKPtJPb440EGkIe9OAbaQgH2fheiXOoVQQjWgS%252FNpAAuVum6A9EN3twbuFs%252FArIkTEnIVEpB2DDOClxeAtkyFolKU2mSmeM5O2xh9lpWtHTcfGG34kSxg5I%252F39KHHPYMLkSOetyUUO0ayqWVcIvE9%252F5IkIdvNuCv7fN%252F%252BQFUXTuYbChf4EFVJWtx0rnTAgMBAAGjggJnMIICYzAJBgNVHRMEAjAAMB8GA1UdIwQYMBaAFMVS7SWACd%252BcgsifR8bdtF8x3bmxMFQGCCsGAQUFBwEBBEgwRjBEBggrBgEFBQcwAoY4aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvbGNyL2FjLXNvbHV0aS1tdWx0aXBsYS12NS5wN2IwgZYGA1UdEQSBjjCBi4EWd2FybGV5Zi5nb2lzQGdtYWlsLmNvbaA4BgVgTAEDAaAvEy0zMDAzMTk4NjAxNDk5NjczMTQwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDCgFwYFYEwBAwagDhMMMDAwMDAwMDAwMDAwoB4GBWBMAQMFoBUTEzAwMDAwMDAwMDAwMDAwMDAwMDAwXQYDVR0gBFYwVDBSBgZgTAECAyUwSDBGBggrBgEFBQcCARY6aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvZG9jcy9kcGMtYWMtc29sdXRpLW11bHRpcGxhLnBkZjApBgNVHSUEIjAgBggrBgEFBQcDAgYIKwYBBQUHAwQGCisGAQQBgjcUAgIwgYwGA1UdHwSBhDCBgTA%252BoDygOoY4aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvbGNyL2FjLXNvbHV0aS1tdWx0aXBsYS12NS5jcmwwP6A9oDuGOWh0dHA6Ly9jY2QyLmFjc29sdXRpLmNvbS5ici9sY3IvYWMtc29sdXRpLW11bHRpcGxhLXY1LmNybDAdBgNVHQ4EFgQUfrciVM4ebVGlvQ3gMCYaoBzz1kwwDgYDVR0PAQH%252FBAQDAgXgMA0GCSqGSIb3DQEBCwUAA4ICAQAASFGySyuNBckwYMuE6XiJ%252FGFRkKOMNAKFuv6ZLq18cVbzU7rcYK%252FFJ5LHZ1k1ZNbfBnyEpaG8yqjzQi6qHKT%252FbJn8Umj%252FgSS4WykCYWUbPZHN3d4Gd9Mhuwndl2m3eQVWkuVjVwVH39oRyaaOos2HwEuvztEsXrOwHGphpgkyYlqKPLvv1yKmCJER8XJR8Lb%252FjfCBe3cTDZQ4pNim29i4aybB1SQeI8xHQa%252BXzc5tWcSm3H5npL7We28DLpD6ZgYwIDR71ZLRzF%252Fesrdvd3Lrj9rSG068PfXKHcpe%252FsvqkfbdCvJF8ssuaAh%252BphjwbbXpW5oFnCNuVXuhdfiA7j45aESIHdZWuibXbBEhF%252Fe9iS1XRBiHrG6lfoPnR2L3TTvw1MLnWgEDdfCfDNlF4y3gNrwVnECrS3sOmUMoT05MCiiwt%252BftNEgJlw9QHeux3uc1cNyKFSldJLpjASNJr0B3F1k4Zi7eDNthr9e7LKLnxBznbefT81P6rO3HE5SYsubtRi8yM1naNGRiwO8bn1rIrF63YUzRwJyiJ5vP6A3FY7fh8E9IDMC76Q9Myw4hZZPhKCvdLZechj6wg1SzCyy7G7yNg1eavCe3C70dFHnSTC%252Bd1Xj0NMtGdr3KINqZVLothSwv1mw%252FrtBWStDwOxtsFEnD94LuSfrT9CK5DprJZg%253D%253D
        //certificado =

        // "MIIG%2FDCCBOSgAwIBAgIIfh8hBCk5wM0wDQYJKoZIhvcNAQELBQAwWTELMAkGA1UEBhMCQlIxEzARBgNVBAoTCklDUC1CcmFzaWwxFTATBgNVBAsTDEFDIFNPTFVUSSB2NTEeMBwGA1UEAxMVQUMgU09MVVRJIE11bHRpcGxhIHY1MB4XDTIxMDQyOTEzMzgwMFoXDTI0MDQyOTEzMzgwMFowgbcxCzAJBgNVBAYTAkJSMRMwEQYDVQQKEwpJQ1AtQnJhc2lsMR4wHAYDVQQLExVBQyBTT0xVVEkgTXVsdGlwbGEgdjUxFzAVBgNVBAsTDjMyMzg2MDg3MDAwMTczMRMwEQYDVQQLEwpQcmVzZW5jaWFsMRowGAYDVQQLExFDZXJ0aWZpY2FkbyBQRiBBMzEpMCcGA1UEAxMgV0FSTEVZIEZFUlJFSVJBIEdPSVM6MDE0OTk2NzMxNDAwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCiKqiznrUX%2BfHbHtLxgC0SqK%2F7lasjFfxN%2B6f26FTdGvxFjEV4Oo6slkgr9dWZB8FZ4fjNYZf0wkZwn2mgFfwEp%2B%2B%2FL%2FrZIIbIiYuUxopt7g5ZXqjzDH6bpMDCAsRfpdOy5JSXNpPtFAlRvo4wPLsIa0ApwKPtJPb440EGkIe9OAbaQgH2fheiXOoVQQjWgS%2FNpAAuVum6A9EN3twbuFs%2FArIkTEnIVEpB2DDOClxeAtkyFolKU2mSmeM5O2xh9lpWtHTcfGG34kSxg5I%2F39KHHPYMLkSOetyUUO0ayqWVcIvE9%2F5IkIdvNuCv7fN%2F%2BQFUXTuYbChf4EFVJWtx0rnTAgMBAAGjggJnMIICYzAJBgNVHRMEAjAAMB8GA1UdIwQYMBaAFMVS7SWACd%2BcgsifR8bdtF8x3bmxMFQGCCsGAQUFBwEBBEgwRjBEBggrBgEFBQcwAoY4aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvbGNyL2FjLXNvbHV0aS1tdWx0aXBsYS12NS5wN2IwgZYGA1UdEQSBjjCBi4EWd2FybGV5Zi5nb2lzQGdtYWlsLmNvbaA4BgVgTAEDAaAvEy0zMDAzMTk4NjAxNDk5NjczMTQwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDCgFwYFYEwBAwagDhMMMDAwMDAwMDAwMDAwoB4GBWBMAQMFoBUTEzAwMDAwMDAwMDAwMDAwMDAwMDAwXQYDVR0gBFYwVDBSBgZgTAECAyUwSDBGBggrBgEFBQcCARY6aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvZG9jcy9kcGMtYWMtc29sdXRpLW11bHRpcGxhLnBkZjApBgNVHSUEIjAgBggrBgEFBQcDAgYIKwYBBQUHAwQGCisGAQQBgjcUAgIwgYwGA1UdHwSBhDCBgTA%2BoDygOoY4aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvbGNyL2FjLXNvbHV0aS1tdWx0aXBsYS12NS5jcmwwP6A9oDuGOWh0dHA6Ly9jY2QyLmFjc29sdXRpLmNvbS5ici9sY3IvYWMtc29sdXRpLW11bHRpcGxhLXY1LmNybDAdBgNVHQ4EFgQUfrciVM4ebVGlvQ3gMCYaoBzz1kwwDgYDVR0PAQH%2FBAQDAgXgMA0GCSqGSIb3DQEBCwUAA4ICAQAASFGySyuNBckwYMuE6XiJ%2FGFRkKOMNAKFuv6ZLq18cVbzU7rcYK%2FFJ5LHZ1k1ZNbfBnyEpaG8yqjzQi6qHKT%2FbJn8Umj%2FgSS4WykCYWUbPZHN3d4Gd9Mhuwndl2m3eQVWkuVjVwVH39oRyaaOos2HwEuvztEsXrOwHGphpgkyYlqKPLvv1yKmCJER8XJR8Lb%2FjfCBe3cTDZQ4pNim29i4aybB1SQeI8xHQa%2BXzc5tWcSm3H5npL7We28DLpD6ZgYwIDR71ZLRzF%2Fesrdvd3Lrj9rSG068PfXKHcpe%2FsvqkfbdCvJF8ssuaAh%2BphjwbbXpW5oFnCNuVXuhdfiA7j45aESIHdZWuibXbBEhF%2Fe9iS1XRBiHrG6lfoPnR2L3TTvw1MLnWgEDdfCfDNlF4y3gNrwVnECrS3sOmUMoT05MCiiwt%2BftNEgJlw9QHeux3uc1cNyKFSldJLpjASNJr0B3F1k4Zi7eDNthr9e7LKLnxBznbefT81P6rO3HE5SYsubtRi8yM1naNGRiwO8bn1rIrF63YUzRwJyiJ5vP6A3FY7fh8E9IDMC76Q9Myw4hZZPhKCvdLZechj6wg1SzCyy7G7yNg1eavCe3C70dFHnSTC%2Bd1Xj0NMtGdr3KINqZVLothSwv1mw%2FrtBWStDwOxtsFEnD94LuSfrT9CK5DprJZg%3D%3D";
        // "MIIG%2FDCCBOSgAwIBAgIIfh8hBCk5wM0wDQYJKoZIhvcNAQELBQAwWTELMAkGA1UEBhMCQlIxEzARBgNVBAoTCklDUC1CcmFzaWwxFTATBgNVBAsTDEFDIFNPTFVUSSB2NTEeMBwGA1UEAxMVQUMgU09MVVRJIE11bHRpcGxhIHY1MB4XDTIxMDQyOTEzMzgwMFoXDTI0MDQyOTEzMzgwMFowgbcxCzAJBgNVBAYTAkJSMRMwEQYDVQQKEwpJQ1AtQnJhc2lsMR4wHAYDVQQLExVBQyBTT0xVVEkgTXVsdGlwbGEgdjUxFzAVBgNVBAsTDjMyMzg2MDg3MDAwMTczMRMwEQYDVQQLEwpQcmVzZW5jaWFsMRowGAYDVQQLExFDZXJ0aWZpY2FkbyBQRiBBMzEpMCcGA1UEAxMgV0FSTEVZIEZFUlJFSVJBIEdPSVM6MDE0OTk2NzMxNDAwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCiKqiznrUX%2BfHbHtLxgC0SqK%2F7lasjFfxN%2B6f26FTdGvxFjEV4Oo6slkgr9dWZB8FZ4fjNYZf0wkZwn2mgFfwEp%2B%2B%2FL%2FrZIIbIiYuUxopt7g5ZXqjzDH6bpMDCAsRfpdOy5JSXNpPtFAlRvo4wPLsIa0ApwKPtJPb440EGkIe9OAbaQgH2fheiXOoVQQjWgS%2FNpAAuVum6A9EN3twbuFs%2FArIkTEnIVEpB2DDOClxeAtkyFolKU2mSmeM5O2xh9lpWtHTcfGG34kSxg5I%2F39KHHPYMLkSOetyUUO0ayqWVcIvE9%2F5IkIdvNuCv7fN%2F%2BQFUXTuYbChf4EFVJWtx0rnTAgMBAAGjggJnMIICYzAJBgNVHRMEAjAAMB8GA1UdIwQYMBaAFMVS7SWACd%2BcgsifR8bdtF8x3bmxMFQGCCsGAQUFBwEBBEgwRjBEBggrBgEFBQcwAoY4aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvbGNyL2FjLXNvbHV0aS1tdWx0aXBsYS12NS5wN2IwgZYGA1UdEQSBjjCBi4EWd2FybGV5Zi5nb2lzQGdtYWlsLmNvbaA4BgVgTAEDAaAvEy0zMDAzMTk4NjAxNDk5NjczMTQwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDCgFwYFYEwBAwagDhMMMDAwMDAwMDAwMDAwoB4GBWBMAQMFoBUTEzAwMDAwMDAwMDAwMDAwMDAwMDAwXQYDVR0gBFYwVDBSBgZgTAECAyUwSDBGBggrBgEFBQcCARY6aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvZG9jcy9kcGMtYWMtc29sdXRpLW11bHRpcGxhLnBkZjApBgNVHSUEIjAgBggrBgEFBQcDAgYIKwYBBQUHAwQGCisGAQQBgjcUAgIwgYwGA1UdHwSBhDCBgTA%2BoDygOoY4aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvbGNyL2FjLXNvbHV0aS1tdWx0aXBsYS12NS5jcmwwP6A9oDuGOWh0dHA6Ly9jY2QyLmFjc29sdXRpLmNvbS5ici9sY3IvYWMtc29sdXRpLW11bHRpcGxhLXY1LmNybDAdBgNVHQ4EFgQUfrciVM4ebVGlvQ3gMCYaoBzz1kwwDgYDVR0PAQH%2FBAQDAgXgMA0GCSqGSIb3DQEBCwUAA4ICAQAASFGySyuNBckwYMuE6XiJ%2FGFRkKOMNAKFuv6ZLq18cVbzU7rcYK%2FFJ5LHZ1k1ZNbfBnyEpaG8yqjzQi6qHKT%2FbJn8Umj%2FgSS4WykCYWUbPZHN3d4Gd9Mhuwndl2m3eQVWkuVjVwVH39oRyaaOos2HwEuvztEsXrOwHGphpgkyYlqKPLvv1yKmCJER8XJR8Lb%2FjfCBe3cTDZQ4pNim29i4aybB1SQeI8xHQa%2BXzc5tWcSm3H5npL7We28DLpD6ZgYwIDR71ZLRzF%2Fesrdvd3Lrj9rSG068PfXKHcpe%2FsvqkfbdCvJF8ssuaAh%2BphjwbbXpW5oFnCNuVXuhdfiA7j45aESIHdZWuibXbBEhF%2Fe9iS1XRBiHrG6lfoPnR2L3TTvw1MLnWgEDdfCfDNlF4y3gNrwVnECrS3sOmUMoT05MCiiwt%2BftNEgJlw9QHeux3uc1cNyKFSldJLpjASNJr0B3F1k4Zi7eDNthr9e7LKLnxBznbefT81P6rO3HE5SYsubtRi8yM1naNGRiwO8bn1rIrF63YUzRwJyiJ5vP6A3FY7fh8E9IDMC76Q9Myw4hZZPhKCvdLZechj6wg1SzCyy7G7yNg1eavCe3C70dFHnSTC%2Bd1Xj0NMtGdr3KINqZVLothSwv1mw%2FrtBWStDwOxtsFEnD94LuSfrT9CK5DprJZg%3D%3D=
        // "MIIG%2FDCCBOSgAwIBAgIIfh8hBCk5wM0wDQYJKoZIhvcNAQELBQAwWTELMAkGA1UEBhMCQlIxEzARBgNVBAoTCklDUC1CcmFzaWwxFTATBgNVBAsTDEFDIFNPTFVUSSB2NTEeMBwGA1UEAxMVQUMgU09MVVRJIE11bHRpcGxhIHY1MB4XDTIxMDQyOTEzMzgwMFoXDTI0MDQyOTEzMzgwMFowgbcxCzAJBgNVBAYTAkJSMRMwEQYDVQQKEwpJQ1AtQnJhc2lsMR4wHAYDVQQLExVBQyBTT0xVVEkgTXVsdGlwbGEgdjUxFzAVBgNVBAsTDjMyMzg2MDg3MDAwMTczMRMwEQYDVQQLEwpQcmVzZW5jaWFsMRowGAYDVQQLExFDZXJ0aWZpY2FkbyBQRiBBMzEpMCcGA1UEAxMgV0FSTEVZIEZFUlJFSVJBIEdPSVM6MDE0OTk2NzMxNDAwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCiKqiznrUX+fHbHtLxgC0SqK%2F7lasjFfxN+6f26FTdGvxFjEV4Oo6slkgr9dWZB8FZ4fjNYZf0wkZwn2mgFfwEp++%2FL%2FrZIIbIiYuUxopt7g5ZXqjzDH6bpMDCAsRfpdOy5JSXNpPtFAlRvo4wPLsIa0ApwKPtJPb440EGkIe9OAbaQgH2fheiXOoVQQjWgS%2FNpAAuVum6A9EN3twbuFs%2FArIkTEnIVEpB2DDOClxeAtkyFolKU2mSmeM5O2xh9lpWtHTcfGG34kSxg5I%2F39KHHPYMLkSOetyUUO0ayqWVcIvE9%2F5IkIdvNuCv7fN%2F+QFUXTuYbChf4EFVJWtx0rnTAgMBAAGjggJnMIICYzAJBgNVHRMEAjAAMB8GA1UdIwQYMBaAFMVS7SWACd+cgsifR8bdtF8x3bmxMFQGCCsGAQUFBwEBBEgwRjBEBggrBgEFBQcwAoY4aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvbGNyL2FjLXNvbHV0aS1tdWx0aXBsYS12NS5wN2IwgZYGA1UdEQSBjjCBi4EWd2FybGV5Zi5nb2lzQGdtYWlsLmNvbaA4BgVgTAEDAaAvEy0zMDAzMTk4NjAxNDk5NjczMTQwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDCgFwYFYEwBAwagDhMMMDAwMDAwMDAwMDAwoB4GBWBMAQMFoBUTEzAwMDAwMDAwMDAwMDAwMDAwMDAwXQYDVR0gBFYwVDBSBgZgTAECAyUwSDBGBggrBgEFBQcCARY6aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvZG9jcy9kcGMtYWMtc29sdXRpLW11bHRpcGxhLnBkZjApBgNVHSUEIjAgBggrBgEFBQcDAgYIKwYBBQUHAwQGCisGAQQBgjcUAgIwgYwGA1UdHwSBhDCBgTA+oDygOoY4aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvbGNyL2FjLXNvbHV0aS1tdWx0aXBsYS12NS5jcmwwP6A9oDuGOWh0dHA6Ly9jY2QyLmFjc29sdXRpLmNvbS5ici9sY3IvYWMtc29sdXRpLW11bHRpcGxhLXY1LmNybDAdBgNVHQ4EFgQUfrciVM4ebVGlvQ3gMCYaoBzz1kwwDgYDVR0PAQH%2FBAQDAgXgMA0GCSqGSIb3DQEBCwUAA4ICAQAASFGySyuNBckwYMuE6XiJ%2FGFRkKOMNAKFuv6ZLq18cVbzU7rcYK%2FFJ5LHZ1k1ZNbfBnyEpaG8yqjzQi6qHKT%2FbJn8Umj%2FgSS4WykCYWUbPZHN3d4Gd9Mhuwndl2m3eQVWkuVjVwVH39oRyaaOos2HwEuvztEsXrOwHGphpgkyYlqKPLvv1yKmCJER8XJR8Lb%2FjfCBe3cTDZQ4pNim29i4aybB1SQeI8xHQa+Xzc5tWcSm3H5npL7We28DLpD6ZgYwIDR71ZLRzF%2Fesrdvd3Lrj9rSG068PfXKHcpe%2FsvqkfbdCvJF8ssuaAh+phjwbbXpW5oFnCNuVXuhdfiA7j45aESIHdZWuibXbBEhF%2Fe9iS1XRBiHrG6lfoPnR2L3TTvw1MLnWgEDdfCfDNlF4y3gNrwVnECrS3sOmUMoT05MCiiwt+ftNEgJlw9QHeux3uc1cNyKFSldJLpjASNJr0B3F1k4Zi7eDNthr9e7LKLnxBznbefT81P6rO3HE5SYsubtRi8yM1naNGRiwO8bn1rIrF63YUzRwJyiJ5vP6A3FY7fh8E9IDMC76Q9Myw4hZZPhKCvdLZechj6wg1SzCyy7G7yNg1eavCe3C70dFHnSTC+d1Xj0NMtGdr3KINqZVLothSwv1mw%2FrtBWStDwOxtsFEnD94LuSfrT9CK5DprJZg=%3D"

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String postdata = "certificado=" + certificado.replace("=", "");
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, postdata);
        Request request = new Request.Builder()
                .url("https://app.tce.to.gov.br/autenticar/app/controllers/?c=TCE_Autenticar_Login&m=getDesafio")
                .method("POST", body).addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/x-www-form-urlencoded").build();
        Response response = client.newCall(request).execute();
        String resposta = response.body().string();

        if (resposta.contains("\"success\":false")) {

            throw new Exception("Falha ao logar com certificado digital.");
        }

        return !resposta.contains("\"success\":false") ? resposta : null;
    }

    public static void main(String[] args) {
        String c = createJWT("sdfsfd", "sdgsdfsdf", "sfsdfsdf", -1);
        decodeJWT(c);
    }

}
