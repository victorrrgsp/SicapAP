package com.example.sicapweb.web.controller;

import okhttp3.*;

import java.io.IOException;
import java.util.Base64;

public class AssinarCertificadoDigital {

    public static String inicializarAssinatura(String certificado, String original) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "certificado=" + certificado + "&original=" + original);

       // System.out.println("body: "+body.toString());
        Request request = new Request.Builder()
                .url("https://dev2.tce.to.gov.br/assinador/app/controllers/?&c=TCE_Assinador_AssinadorWeb&m=inicializarAssinatura")
                .method("POST", body)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Referer", "https://app.tce.to.gov.br/")
                .build();

        Response response = client.newCall(request).execute();
        String resposta = response.body().string();
        return resposta;
    }

    public static String FinalizarAssinatura(String desafio, String assinatura, String original) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "desafio=" + desafio + "&assinatura=" + assinatura + "&original=" + original);
        Request request = new Request.Builder()
                .url("https://dev2.tce.to.gov.br/assinador/app/controllers/?&c=TCE_Assinador_AssinadorWeb&m=finalizarAssinatura")
                .method("POST", body)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Referer", "https://app.tce.to.gov.br/")
                .build();

        Response response = client.newCall(request).execute();
        String resposta = response.body().string();
        return resposta;
    }


    public static void main(String[] args) throws IOException {

        String certificado = "MIIG%2FDCCBOSgAwIBAgIIfh8hBCk5wM0wDQYJKoZIhvcNAQELBQAwWTELMAkGA1UEBhMCQlIxEzARBgNVBAoTCklDUC1CcmFzaWwxFTATBgNVBAsTDEFDIFNPTFVUSSB2NTEeMBwGA1UEAxMVQUMgU09MVVRJIE11bHRpcGxhIHY1MB4XDTIxMDQyOTEzMzgwMFoXDTI0MDQyOTEzMzgwMFowgbcxCzAJBgNVBAYTAkJSMRMwEQYDVQQKEwpJQ1AtQnJhc2lsMR4wHAYDVQQLExVBQyBTT0xVVEkgTXVsdGlwbGEgdjUxFzAVBgNVBAsTDjMyMzg2MDg3MDAwMTczMRMwEQYDVQQLEwpQcmVzZW5jaWFsMRowGAYDVQQLExFDZXJ0aWZpY2FkbyBQRiBBMzEpMCcGA1UEAxMgV0FSTEVZIEZFUlJFSVJBIEdPSVM6MDE0OTk2NzMxNDAwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCiKqiznrUX%2BfHbHtLxgC0SqK%2F7lasjFfxN%2B6f26FTdGvxFjEV4Oo6slkgr9dWZB8FZ4fjNYZf0wkZwn2mgFfwEp%2B%2B%2FL%2FrZIIbIiYuUxopt7g5ZXqjzDH6bpMDCAsRfpdOy5JSXNpPtFAlRvo4wPLsIa0ApwKPtJPb440EGkIe9OAbaQgH2fheiXOoVQQjWgS%2FNpAAuVum6A9EN3twbuFs%2FArIkTEnIVEpB2DDOClxeAtkyFolKU2mSmeM5O2xh9lpWtHTcfGG34kSxg5I%2F39KHHPYMLkSOetyUUO0ayqWVcIvE9%2F5IkIdvNuCv7fN%2F%2BQFUXTuYbChf4EFVJWtx0rnTAgMBAAGjggJnMIICYzAJBgNVHRMEAjAAMB8GA1UdIwQYMBaAFMVS7SWACd%2BcgsifR8bdtF8x3bmxMFQGCCsGAQUFBwEBBEgwRjBEBggrBgEFBQcwAoY4aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvbGNyL2FjLXNvbHV0aS1tdWx0aXBsYS12NS5wN2IwgZYGA1UdEQSBjjCBi4EWd2FybGV5Zi5nb2lzQGdtYWlsLmNvbaA4BgVgTAEDAaAvEy0zMDAzMTk4NjAxNDk5NjczMTQwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDCgFwYFYEwBAwagDhMMMDAwMDAwMDAwMDAwoB4GBWBMAQMFoBUTEzAwMDAwMDAwMDAwMDAwMDAwMDAwXQYDVR0gBFYwVDBSBgZgTAECAyUwSDBGBggrBgEFBQcCARY6aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvZG9jcy9kcGMtYWMtc29sdXRpLW11bHRpcGxhLnBkZjApBgNVHSUEIjAgBggrBgEFBQcDAgYIKwYBBQUHAwQGCisGAQQBgjcUAgIwgYwGA1UdHwSBhDCBgTA%2BoDygOoY4aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvbGNyL2FjLXNvbHV0aS1tdWx0aXBsYS12NS5jcmwwP6A9oDuGOWh0dHA6Ly9jY2QyLmFjc29sdXRpLmNvbS5ici9sY3IvYWMtc29sdXRpLW11bHRpcGxhLXY1LmNybDAdBgNVHQ4EFgQUfrciVM4ebVGlvQ3gMCYaoBzz1kwwDgYDVR0PAQH%2FBAQDAgXgMA0GCSqGSIb3DQEBCwUAA4ICAQAASFGySyuNBckwYMuE6XiJ%2FGFRkKOMNAKFuv6ZLq18cVbzU7rcYK%2FFJ5LHZ1k1ZNbfBnyEpaG8yqjzQi6qHKT%2FbJn8Umj%2FgSS4WykCYWUbPZHN3d4Gd9Mhuwndl2m3eQVWkuVjVwVH39oRyaaOos2HwEuvztEsXrOwHGphpgkyYlqKPLvv1yKmCJER8XJR8Lb%2FjfCBe3cTDZQ4pNim29i4aybB1SQeI8xHQa%2BXzc5tWcSm3H5npL7We28DLpD6ZgYwIDR71ZLRzF%2Fesrdvd3Lrj9rSG068PfXKHcpe%2FsvqkfbdCvJF8ssuaAh%2BphjwbbXpW5oFnCNuVXuhdfiA7j45aESIHdZWuibXbBEhF%2Fe9iS1XRBiHrG6lfoPnR2L3TTvw1MLnWgEDdfCfDNlF4y3gNrwVnECrS3sOmUMoT05MCiiwt%2BftNEgJlw9QHeux3uc1cNyKFSldJLpjASNJr0B3F1k4Zi7eDNthr9e7LKLnxBznbefT81P6rO3HE5SYsubtRi8yM1naNGRiwO8bn1rIrF63YUzRwJyiJ5vP6A3FY7fh8E9IDMC76Q9Myw4hZZPhKCvdLZechj6wg1SzCyy7G7yNg1eavCe3C70dFHnSTC%2Bd1Xj0NMtGdr3KINqZVLothSwv1mw%2FrtBWStDwOxtsFEnD94LuSfrT9CK5DprJZg%3D%3D";

        String original = "W3siaWQiOiIxMjAzNiIsIm5vbWUiOiJTRUJBU1RJQU5BIENPU1RBIFNBTlRBTkEiLCJ0aXBvIjoiQVBPU0VOVEFET1JJQSIsIkNhcmdvTm9tZSI6IkFVWElMSUFSIEFETUlOSVNUUkFUSVZPIiwibWV1Q2FyZ28iOiI0IiwiYXRvIjoiMDAwNjA4MjAyMCJ9LHsiaWQiOiIxMjAzNyIsIm5vbWUiOiJGRVJOQU5ETyBERSBBTE1FSURBIE1BQ0hBRE8iLCJ0aXBvIjoiQVBPU0VOVEFET1JJQSIsIkNhcmdvTm9tZSI6Ik1FRElDTyIsIm1ldUNhcmdvIjoiNCIsImF0byI6IjAwMDczNzIwMjAifV0=";
        //[{"id":"12036","nome":"SEBASTIANA COSTA SANTANA","tipo":"APOSENTADORIA","CargoNome":"AUXILIAR ADMINISTRATIVO","meuCargo":"4","ato":"0006082020"},{"id":"12037","nome":"FERNANDO DE ALMEIDA MACHADO","tipo":"APOSENTADORIA","CargoNome":"MEDICO","meuCargo":"4","ato":"0007372020"}]

        System.out.println("[+] resposta do assinadorWebinicializarAssinatura(): " + inicializarAssinatura(certificado, original));

        //=========================================================


    }
}
