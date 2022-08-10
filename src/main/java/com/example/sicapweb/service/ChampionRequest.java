package com.example.sicapweb.service;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
public class ChampionRequest {

    public static String server = "https://app.tce.to.gov.br";

    public static ResponseEntity<String> salvarSimples(String cpf, String nome, String sistema, String sso_client_id, String sso_client_secret) throws IOException, URISyntaxException {
        String servico = "/cadun/app/controllers/index.php?&c=TCE_CADUN_PessoaFisica&m=salvarSimples";

        //Aqui vem o array na ordem dos parametros da assinatura do metodo!
        String requestArray = "[\"" + cpf + "\",\"" + nome + "\",\"" + sistema + "\"]";

        return request(servico, requestArray, sso_client_id, sso_client_secret);
    }

    private static ResponseEntity<String> request(String servico, String requestArray, String sso_client_id, String sso_client_secret) throws IOException, URISyntaxException {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("Y-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("America/Araguaina"));
        String timestamp = dateFormat.format(date.getTime());
        String message = sso_client_id + "POST" + servico + timestamp;
        String hash = assinarRequest(sso_client_secret, message);

        URI url = new URI(server + servico);

        RestTemplate template = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", sso_client_id + ":" + hash + "");
        headers.add("Accept", "application/json");
        headers.add("Referer", server + servico.substring(0,servico.indexOf("controller"))+"index.php");
        headers.add("X-Timestamp", timestamp);

        HttpEntity<String> entity = new HttpEntity<String>(requestArray, headers);
        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new LoggingRequestInterceptor());
        restTemplate.setInterceptors(interceptors);

        try {
            ResponseEntity<String> response = template.exchange(url, HttpMethod.POST, entity, String.class);
            //    System.out.println(response.getBody());
            //    System.out.println(response.getStatusCode().getReasonPhrase());
            return response;
        } catch (Exception e) {
            //    System.out.println(message);
            //    System.out.println(hash);
            e.printStackTrace();
        }
        return null;
    }

    private static String assinarRequest(String key, String data) {
        try {

            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            char[] hexes = Hex.encodeHex(sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8)));
            String ok = String.copyValueOf(hexes);

            char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
            byte[] bytes = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
            char[] hexChars = new char[bytes.length * 2];
            for (int j = 0; j < bytes.length; j++) {
                int v = bytes[j] & 0xFF;
                hexChars[j * 2] = HEX_ARRAY[v >>> 4];
                hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
            }

            String r = new String(hexChars);

            return r;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return null;
    }
}


class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    final static Logger log = LoggerFactory.getLogger(LoggingRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        traceRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        traceResponse(response);
        return response;
    }

    private void traceRequest(HttpRequest request, byte[] body) throws IOException {
        log.info("===========================request begin================================================");
        log.debug("URI         : {}", request.getURI());
        log.debug("Method      : {}", request.getMethod());
        log.debug("Headers     : {}", request.getHeaders());
        log.debug("Request body: {}", new String(body, "UTF-8"));
        log.info("==========================request end================================================");
    }

    private void traceResponse(ClientHttpResponse response) throws IOException {
        StringBuilder inputStringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), "UTF-8"));
        String line = bufferedReader.readLine();
        while (line != null) {
            inputStringBuilder.append(line);
            inputStringBuilder.append('\n');
            line = bufferedReader.readLine();
        }
        log.info("============================response begin==========================================");
        log.debug("Status code  : {}", response.getStatusCode());
        log.debug("Status text  : {}", response.getStatusText());
        log.debug("Headers      : {}", response.getHeaders());
        log.debug("Response body: {}", inputStringBuilder.toString());
        log.info("=======================response end=================================================");
    }

}
