package com.example.sicapweb.service;

import com.example.sicapweb.repository.geral.UnidadeGestoraRepository;
import com.google.gson.*;
import okhttp3.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import redis.clients.jedis.util.Hashing;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class AssinarCertificado {

    public static Map salvarAssinatura(JsonElement processo, String entrada, String saida) {

        Map resultado = new HashMap();

        try {
            int idProcesso = processo.getAsJsonObject().get("id").getAsInt();
            String cpf = "123.456.789-00";
            Map assinatura = verificarAssinatura(idProcesso);

            if (assinatura.get("temAssinatura").equals(true)) {
                resultado.put("sucess", false);
                resultado.put("motivo", 0); // ja foi adicionado
                resultado.put("idAssinatura", assinatura.get("idAssinatura"));

            } else {
                String sqlEnvio = "INSERT INTO AdmAssinatura (envioId, cargoId, cpf, hash_assinante, hash_assinado, data_assinatura) values (?,?,?,?,?,GETDATE())";

                Map proc = verificarAssinatura(idProcesso);

                resultado.put("sucess", true);
                resultado.put("idAssinatura", proc.get("idAssinatura"));

            }

            return resultado;
        } catch (Exception excecao) {
            resultado.put("sucess", false);
            resultado.put("motivo", 1);
            resultado.put("erro", excecao.toString());

            return resultado;
        }

    }

    public static Map verificarAssinatura(int idProcesso) {
        Map resultado = new HashMap();

        try {
            String sqlId = "SELECT id FROM AdmAssinatura WHERE envioId = " + idProcesso;
            //System.out.println(sqlId);

            int sqlIdResultado = 70;

            String sqlQtd = "SELECT count(*) as qtd FROM AdmAssinatura WHERE envioId = " + idProcesso;
            //System.out.println(sqlQtd);

            int sqlQtdResultado = 0;

            resultado.put("sucess", true);
            resultado.put("temAssinatura", sqlQtdResultado == 0 ? true : false);
            resultado.put("idAssinatura", sqlIdResultado);

        } catch (Exception excecao) {
            resultado.put("sucess", false);
            resultado.put("erro", excecao.toString());

        }

        return resultado;
    }

    public static Map gerarProcesso(int idProcesso) {
        Map resultado = new HashMap();

        resultado.put("sucess", true);
        resultado.put("processo", "123456789");
        resultado.put("ano", "1999");
        resultado.put("msg", "O Processo foi gerado corretamente. O acompanhamento será através do sistema eContas.");

        return resultado;
    }

    public static Map apagarAssinaturas(List<Integer> arrayAssinaturas) {
        Map resultado = new HashMap();

        String sqlIN;
        for (int id : arrayAssinaturas) {
            System.out.println(id);
        }

        try {
            resultado.put("sucess", true);
            return resultado;

        } catch (Exception excecao) {
            resultado.put("sucess", false);
            resultado.put("msg", excecao.toString());

            return resultado;
        }
    }


    public static Map finalizaAssinatura(String processos, String entrada, String saida) {
        List<Integer> arrayAssinaturas = new ArrayList<Integer>();

        Map resultado = new HashMap();

        try {
            String processosBase64Decoded = new String(Base64.decodeBase64(processos.getBytes()));

            JsonArray processosJson = new JsonParser().parse(processosBase64Decoded).getAsJsonArray();
            Iterator<JsonElement> processosJsonIterator = processosJson.iterator();

            String procSucesso = "";

            while (processosJsonIterator.hasNext()) {
                JsonElement processo = processosJsonIterator.next();
                Map assinatura = salvarAssinatura(processo, entrada, saida);

                ////
                int idProcesso2 = processo.getAsJsonObject().get("id").getAsInt();
                Map resultadoGerarProcesso2 = gerarProcesso(idProcesso2);
                procSucesso += processo.getAsJsonObject().get("nome").getAsString() + " (" + processo.getAsJsonObject().get("tipo").getAsString() + ") - " + resultadoGerarProcesso2.get("processo") + "/" + resultadoGerarProcesso2.get("ano") + ", ";

                if (assinatura.get("sucess").equals(true)) {
                    arrayAssinaturas.add((Integer) assinatura.get("idAssinatura"));
                    int idProcesso = (Integer) assinatura.get("idAssinatura");
                    Map resultadoGerarProcesso = gerarProcesso(idProcesso);

                    if (resultadoGerarProcesso.get("sucess").equals(false)) {
                        apagarAssinaturas(arrayAssinaturas);

                        resultado.put("sucess", false);
                        resultado.put("msg", resultadoGerarProcesso.get("msg"));

                        return resultado;
                    }
                    procSucesso += processo.getAsJsonObject().get("nome");
                    System.out.println(procSucesso);
                } else {
                    if (assinatura.get("motivo").equals(0)) {
                        //processo ja foram assinados
                    } else {
                        resultado.put("sucess", false);
                        resultado.put("msg", "Erro ao assinar processo!");
                    }
                }
            }

            resultado.put("sucess", true);
            resultado.put("msg", "Os seguintes processos foram gerados corretamente: " + procSucesso + "O acompanhamento será através do sistema eContas.");
            return resultado;
        } catch (Exception excecao) {
            resultado.put("sucess", false);
            resultado.put("msg", excecao.toString());
            return resultado;
        }
    }

    public static String inicializarAssinatura(String certificado, String original) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "certificado=" + certificado + "&original=" + original);

        Request request = new Request.Builder()
                .url("https://app.tce.to.gov.br/assinador/app/controllers/?&c=TCE_Assinador_AssinadorWeb&m=inicializarAssinatura")
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
                .url("https://app.tce.to.gov.br/assinador/app/controllers/?&c=TCE_Assinador_AssinadorWeb&m=finalizarAssinatura")
                .method("POST", body)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Referer", "https://app.tce.to.gov.br/")
                .build();
        Response response = client.newCall(request).execute();
        String resposta = response.body().string();
        return resposta;
    }
}
