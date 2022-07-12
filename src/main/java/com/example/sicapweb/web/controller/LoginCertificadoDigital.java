package com.example.sicapweb.web.controller;

import okhttp3.*;

import java.io.IOException;
import java.util.Base64;

class LoginCertificadoDigital {

        public static String getDesafio(String certificado) throws IOException {

                try {
                        OkHttpClient client = new OkHttpClient().newBuilder().build();

                        String postdata = "certificado=" + certificado;
                        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                        RequestBody body = RequestBody.create(mediaType, postdata);
                        Request request = new Request.Builder()
                                .url("https://app.tce.to.gov.br/autenticar/app/controllers/?c=TCE_Autenticar_Login&m=getDesafio")
                                .method("POST", body).addHeader("Accept", "application/json")
                                .addHeader("Content-Type", "application/x-www-form-urlencoded").build();
                        Response response = client.newCall(request).execute();
                        String resposta = response.body().string();

                        //JsonNode respostaJson = new ObjectMapper().readTree(resposta);
                        //String desafio = respostaJson.get("sha256").asText();
                        // System.out.println(desafio);

                        return resposta;

                } catch (Exception e) {
                        System.out.println("[falha]: " + e.toString());
                }

                return null;
        }

        public static void main(String[]args) throws IOException {
                System.out.println("Welcome");
                String certificado1 = "MIIFXDCCBESgAwIBAgIIS2ENR25fSmcwDQYJKoZIhvcNAQELBQAwgY0xCzAJBgNVBAYTAkJSMQswCQYDVQQIEwJUTzEPMA0GA1UEBxMGUGFsbWFzMQ8wDQYDVQQKEwZUQ0UtVE8xDjAMBgNVBAsTBUNPREVTMRswGQYDVQQDExJDQSBDT0RFUyBUQ0UtVE8gVjExIjAgBgkqhkiG9w0BCQEWE2NvZGVzQHRjZS50by5nb3YuYnIwHhcNMjIwNjIxMTcxMjAwWhcNMjMwNDE5MTcyNDAwWjCBnDELMAkGA1UEBhMCQlIxCzAJBgNVBAgTAlRPMQ8wDQYDVQQHEwZQYWxtYXMxDzANBgNVBAoTBlRDRS1UTzEOMAwGA1UECxMFQ09ERVMxKDAmBgNVBAMTH0pPU0UgUk9CRVJUTyBNQVJUSU5TIENBVkFMQ0FOVEkxJDAiBgkqhkiG9w0BCQEWFWpvc2VybWNAdGNlLnRvLmdvdi5icjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAL8ZlYQPvl8UUp87AfwXbMPb0ACnWWmBsMALEXvIrVNyqga4GpLtLtM/Qr2DD+CZRTb9kQKemnq/X+WC6bGpuLrxLuJuoCQCHWKt5xTAvSm7pUabrIAcWs0V1q2aP4v59oYYZCT2BAglY7nLet+qxvx2fHOGOr+M96EXyi7TAz8hBdiymKfqTL9tO3Q4XgMv/tzOeC9/tgQqAFasa87WsnBS+sT+rO62bKL+rd0YL9k+kYer5uRb85H7RNvSeJTP+tPGrMcNQVu9/ALlZUE53li3gvCQYfHjm3zxqS9SAXu1e970H53ESHezQfHqBHXNrKZ1LEC1SkzOPTbBPjUvlnUCAwEAAaOCAa0wggGpMAkGA1UdEwQCMAAwHwYDVR0jBBgwFoAUoF9jiwZ6VU3QqQ/ch9Fa4lJkwVQwDgYDVR0PAQH/BAQDAgXgMCcGA1UdJQQgMB4GCCsGAQUFBwMCBggrBgEFBQcDAwYIKwYBBQUHAwQwQwYDVR0RBDwwOqA4BgVgTAEDAaAvDC0wMjA4MTk4ODAxMDc0NDc5MTY1MDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwWQYDVR0fBFIwUDBOoEygSoZIaHR0cHM6Ly9hcHAudGNlLnRvLmdvdi5ici9hc3NpbmFkb3IvaW5zdGFsYWNhby9yZXBvL3Jldm9nYWRvc2NvZGVzdjEuY3JsMIGhBgNVHSAEgZkwgZYwgZMGBmBMAQIBEDCBiDA7BggrBgEFBQcCARYvaHR0cDovL2FwcC50Y2UudG8uZ292LmJyL2Fzc2luYWRvci9yZXBvL2Nwcy5wZGYwSQYIKwYBBQUHAgIwPTANFgZUQ0UtVE8wAwIBARosQ0VSVElGSUNBQ0FPIElOVEVSTkEgVVNBREEgUEVMTyBUQ0UtVE8gQ09ERVMwDQYJKoZIhvcNAQELBQADggEBACD8XXlS5Wan2T6sJ9A3UG63B3YjskUhUoEz7hzOgNTHy46AsrWZNAZQQVFepx8hdMdNrq8K0CmAo7igYC54Z7Dq+J29uzLuHD1n2+1p5DZ4isEFq8Q5m0nnXDhSarJyEVuNqDATp/jbZcEsfQy12IC/UpVqos+plQseF1Xbtjl+qHbjg7Katw+/r4vEXNP7bm1okJ4zONcuQFIXJPivsQiulhSLQU5H07JYzrdj4k9cw6cV8Am7nLO8qwpo39hs/X7eHlxQFUM71OjtWjeD+QOovFa09pnxz2SbgNC92K7EWeSTtqc2K10L7KY22dhLqdf/2HuVM03dwfNyTEANskc=";

                String certificado = "MIIG%2FDCCBOSgAwIBAgIIfh8hBCk5wM0wDQYJKoZIhvcNAQELBQAwWTELMAkGA1UEBhMCQlIxEzARBgNVBAoTCklDUC1CcmFzaWwxFTATBgNVBAsTDEFDIFNPTFVUSSB2NTEeMBwGA1UEAxMVQUMgU09MVVRJIE11bHRpcGxhIHY1MB4XDTIxMDQyOTEzMzgwMFoXDTI0MDQyOTEzMzgwMFowgbcxCzAJBgNVBAYTAkJSMRMwEQYDVQQKEwpJQ1AtQnJhc2lsMR4wHAYDVQQLExVBQyBTT0xVVEkgTXVsdGlwbGEgdjUxFzAVBgNVBAsTDjMyMzg2MDg3MDAwMTczMRMwEQYDVQQLEwpQcmVzZW5jaWFsMRowGAYDVQQLExFDZXJ0aWZpY2FkbyBQRiBBMzEpMCcGA1UEAxMgV0FSTEVZIEZFUlJFSVJBIEdPSVM6MDE0OTk2NzMxNDAwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCiKqiznrUX%2BfHbHtLxgC0SqK%2F7lasjFfxN%2B6f26FTdGvxFjEV4Oo6slkgr9dWZB8FZ4fjNYZf0wkZwn2mgFfwEp%2B%2B%2FL%2FrZIIbIiYuUxopt7g5ZXqjzDH6bpMDCAsRfpdOy5JSXNpPtFAlRvo4wPLsIa0ApwKPtJPb440EGkIe9OAbaQgH2fheiXOoVQQjWgS%2FNpAAuVum6A9EN3twbuFs%2FArIkTEnIVEpB2DDOClxeAtkyFolKU2mSmeM5O2xh9lpWtHTcfGG34kSxg5I%2F39KHHPYMLkSOetyUUO0ayqWVcIvE9%2F5IkIdvNuCv7fN%2F%2BQFUXTuYbChf4EFVJWtx0rnTAgMBAAGjggJnMIICYzAJBgNVHRMEAjAAMB8GA1UdIwQYMBaAFMVS7SWACd%2BcgsifR8bdtF8x3bmxMFQGCCsGAQUFBwEBBEgwRjBEBggrBgEFBQcwAoY4aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvbGNyL2FjLXNvbHV0aS1tdWx0aXBsYS12NS5wN2IwgZYGA1UdEQSBjjCBi4EWd2FybGV5Zi5nb2lzQGdtYWlsLmNvbaA4BgVgTAEDAaAvEy0zMDAzMTk4NjAxNDk5NjczMTQwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDCgFwYFYEwBAwagDhMMMDAwMDAwMDAwMDAwoB4GBWBMAQMFoBUTEzAwMDAwMDAwMDAwMDAwMDAwMDAwXQYDVR0gBFYwVDBSBgZgTAECAyUwSDBGBggrBgEFBQcCARY6aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvZG9jcy9kcGMtYWMtc29sdXRpLW11bHRpcGxhLnBkZjApBgNVHSUEIjAgBggrBgEFBQcDAgYIKwYBBQUHAwQGCisGAQQBgjcUAgIwgYwGA1UdHwSBhDCBgTA%2BoDygOoY4aHR0cDovL2NjZC5hY3NvbHV0aS5jb20uYnIvbGNyL2FjLXNvbHV0aS1tdWx0aXBsYS12NS5jcmwwP6A9oDuGOWh0dHA6Ly9jY2QyLmFjc29sdXRpLmNvbS5ici9sY3IvYWMtc29sdXRpLW11bHRpcGxhLXY1LmNybDAdBgNVHQ4EFgQUfrciVM4ebVGlvQ3gMCYaoBzz1kwwDgYDVR0PAQH%2FBAQDAgXgMA0GCSqGSIb3DQEBCwUAA4ICAQAASFGySyuNBckwYMuE6XiJ%2FGFRkKOMNAKFuv6ZLq18cVbzU7rcYK%2FFJ5LHZ1k1ZNbfBnyEpaG8yqjzQi6qHKT%2FbJn8Umj%2FgSS4WykCYWUbPZHN3d4Gd9Mhuwndl2m3eQVWkuVjVwVH39oRyaaOos2HwEuvztEsXrOwHGphpgkyYlqKPLvv1yKmCJER8XJR8Lb%2FjfCBe3cTDZQ4pNim29i4aybB1SQeI8xHQa%2BXzc5tWcSm3H5npL7We28DLpD6ZgYwIDR71ZLRzF%2Fesrdvd3Lrj9rSG068PfXKHcpe%2FsvqkfbdCvJF8ssuaAh%2BphjwbbXpW5oFnCNuVXuhdfiA7j45aESIHdZWuibXbBEhF%2Fe9iS1XRBiHrG6lfoPnR2L3TTvw1MLnWgEDdfCfDNlF4y3gNrwVnECrS3sOmUMoT05MCiiwt%2BftNEgJlw9QHeux3uc1cNyKFSldJLpjASNJr0B3F1k4Zi7eDNthr9e7LKLnxBznbefT81P6rO3HE5SYsubtRi8yM1naNGRiwO8bn1rIrF63YUzRwJyiJ5vP6A3FY7fh8E9IDMC76Q9Myw4hZZPhKCvdLZechj6wg1SzCyy7G7yNg1eavCe3C70dFHnSTC%2Bd1Xj0NMtGdr3KINqZVLothSwv1mw%2FrtBWStDwOxtsFEnD94LuSfrT9CK5DprJZg%3D%3D";

                String certificado2 = "MIIFVjCCBD6gAwIBAgIIID1j1bib12QwDQYJKoZIhvcNAQELBQAwgY0xCzAJBgNVBAYTAkJSMQswCQYDVQQIEwJUTzEPMA0GA1UEBxMGUGFsbWFzMQ8wDQYDVQQKEwZUQ0UtVE8xDjAMBgNVBAsTBUNPREVTMRswGQYDVQQDExJDQSBDT0RFUyBUQ0UtVE8gVjExIjAgBgkqhkiG9w0BCQEWE2NvZGVzQHRjZS50by5nb3YuYnIwHhcNMjEwNTE3MjAzNjAwWhcNMjMwNDE5MTcyNDAwWjCBljELMAkGA1UEBhMCQlIxCzAJBgNVBAgTAlRPMQ8wDQYDVQQHEwZQYWxtYXMxDzANBgNVBAoTBlRDRS1UTzEOMAwGA1UECxMFQ09ERVMxIjAgBgNVBAMTGUV3ZXJ0b24gRmVycmVpcmEgU2FudGlhZ28xJDAiBgkqhkiG9w0BCQEWFWV3ZXJ0b25mc0B0Y2V0by50Yy5icjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBANyPSKRLjgmsfBiz1BO+lhwdQATmn8KNn8kAlJzvgz1TPz0iQNcPNEZKVgyyArObFHuRpn/cDR2rVFG+rhsTx670Oan70ttHeAkm1bc19bBGmmfMykRayrR5+vAmjWoks35pP+MiNYffHq6qE0RrRg1UweR4LVWJ+kpNgclGq3dky9TANk9xDQiEi6dpNt7cN6T6lFI0fNEB+7RkfDlSZ8hconZHPqWyOTtwL0WhpaQwPvv1ycyKMRK97UNgJj+yehlbUKhGEDhBhdX5xzbGMf1Ei5tAddf1orNZgMaL13ndSSEsK1EMTGJXuHZSXwFj6SW4HVNX2Nh/COdE0KLwCCkCAwEAAaOCAa0wggGpMAkGA1UdEwQCMAAwHwYDVR0jBBgwFoAUoF9jiwZ6VU3QqQ/ch9Fa4lJkwVQwDgYDVR0PAQH/BAQDAgXgMCcGA1UdJQQgMB4GCCsGAQUFBwMCBggrBgEFBQcDAwYIKwYBBQUHAwQwQwYDVR0RBDwwOqA4BgVgTAEDAaAvDC0xODA3MTk5MTAyOTA4NDUzMTkzMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwWQYDVR0fBFIwUDBOoEygSoZIaHR0cHM6Ly9hcHAudGNlLnRvLmdvdi5ici9hc3NpbmFkb3IvaW5zdGFsYWNhby9yZXBvL3Jldm9nYWRvc2NvZGVzdjEuY3JsMIGhBgNVHSAEgZkwgZYwgZMGBmBMAQIBEDCBiDA7BggrBgEFBQcCARYvaHR0cDovL2FwcC50Y2UudG8uZ292LmJyL2Fzc2luYWRvci9yZXBvL2Nwcy5wZGYwSQYIKwYBBQUHAgIwPTANFgZUQ0UtVE8wAwIBARosQ0VSVElGSUNBQ0FPIElOVEVSTkEgVVNBREEgUEVMTyBUQ0UtVE8gQ09ERVMwDQYJKoZIhvcNAQELBQADggEBAI/AtcemwIn4H5Hef+7NY1L94vdJBtXyySbg5BZTbUx0lEfMtujUi/zsKRnAhfXZEkn4rUvZrznHfETlD1/6Z0YCMSY0h9G5aTWQaTnxsxdguAkG9E4UuVIdi+J5GQuebkaNn2wPeM2gHPkFQ9RKf9jRkdE14KhY1n55ztl+LKy5xsqAObc2c1nmPr2cv5Ihor8//XN0UGtYERYgbpcuGW/QeWyqUMVyDlosOG9llcJSNqueQj2Cw9ec4Ms+82KmTeMBWNOwTBQm3Wqkygunfs6r99L/+bZCJdtHw1DspQV1YTAmztgB9LPRENUsQxyzsEyqq6e6ebSRQYKTdQ5UrZs=";
                System.out.println("meudesario:"+getDesafio(certificado1));
                System.out.println("desafio:"+getDesafio(certificado));

                System.out.println("desafioewerton:"+getDesafio(certificado2));


//                System.out.println("certificado encodado: "+certificado);
//                byte[] decodedBytes = Base64.getDecoder().decode(certificado);
//                String decodedcertificado = new String(decodedBytes);
//                System.out.println("certificado decodificado: "+decodedcertificado);
        }
}
