package com.example.sicapweb.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.codec.binary.Base64;

import java.util.*;

public class AssinarCertificado {

    public static Map salvarAssinatura(JsonElement processo, String entrada, String saida) {

        Map resultado = new HashMap();

        try {
            int idProcesso = processo.getAsJsonObject().get("id").getAsInt();
            String cpf = "123.456.789-00";
            Map assinatura  = verificarAssinatura(idProcesso);

            if (assinatura.get("temAssinatura").equals(true)) {
                resultado.put("sucess", false);
                resultado.put("motivo", 0); // ja foi adicionado
                resultado.put("idAssinatura", assinatura.get("idAssinatura"));

            }

            else {
                String sqlEnvio = "INSERT INTO AdmAssinatura (envioId, cargoId, cpf, hash_assinante, hash_assinado, data_assinatura) values (?,?,?,?,?,GETDATE())";

                Map proc = verificarAssinatura(idProcesso);

                resultado.put("sucess", true);
                resultado.put("idAssinatura", proc.get("idAssinatura"));

            }

            return resultado;
        }

        catch (Exception excecao) {
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
            //System.out.println(processosBase64Decoded);

            JsonArray processosJson = new JsonParser().parse(processosBase64Decoded).getAsJsonArray();
            Iterator<JsonElement> processosJsonIterator = processosJson.iterator();

            String procSucesso = "";

            while(processosJsonIterator.hasNext()){
                JsonElement processo = processosJsonIterator.next();
                //System.out.println(processo);
                Map assinatura = salvarAssinatura(processo, entrada, saida);

                ////
                int idProcesso2 = processo.getAsJsonObject().get("id").getAsInt();
                Map resultadoGerarProcesso2 = gerarProcesso(idProcesso2);
                procSucesso += processo.getAsJsonObject().get("nome").getAsString() + " (" + processo.getAsJsonObject().get("tipo").getAsString() + ") - " + resultadoGerarProcesso2.get("processo") + "/" + resultadoGerarProcesso2.get("ano") + ", ";
                System.out.println(procSucesso);
                ////

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
                }

                else {
                    if (assinatura.get("motivo").equals(0)) {
                        //processo ja foram assinados
                    }

                    else {
                        resultado.put("sucess", false);
                        resultado.put("msg", "Erro ao assinar processo!");
                    }
                }
            }

            resultado.put("sucess", true);
            resultado.put("msg", "Os seguintes processos foram gerados corretamente: " + procSucesso +  "O acompanhamento será através do sistema eContas.");
            return resultado;


        }  catch (Exception excecao) {

            resultado.put("sucess", false);
            resultado.put("msg", excecao.toString());

            return resultado;
        }


    }

    public static void main(String[] args) {
        String processos = "W3siaWQiOiIxMjAzNiIsIm5vbWUiOiJTRUJBU1RJQU5BIENPU1RBIFNBTlRBTkEiLCJ0aXBvIjoiQVBPU0VOVEFET1JJQSIsIkNhcmdvTm9tZSI6IkFVWElMSUFSIEFETUlOSVNUUkFUSVZPIiwibWV1Q2FyZ28iOiI0IiwiYXRvIjoiMDAwNjA4MjAyMCJ9LHsiaWQiOiIxMjAzNyIsIm5vbWUiOiJGRVJOQU5ETyBERSBBTE1FSURBIE1BQ0hBRE8iLCJ0aXBvIjoiQVBPU0VOVEFET1JJQSIsIkNhcmdvTm9tZSI6Ik1FRElDTyIsIm1ldUNhcmdvIjoiNCIsImF0byI6IjAwMDczNzIwMjAifV0=";
        //[{"id":"12036","nome":"SEBASTIANA COSTA SANTANA","tipo":"APOSENTADORIA","CargoNome":"AUXILIAR ADMINISTRATIVO","meuCargo":"4","ato":"0006082020"},{"id":"12037","nome":"FERNANDO DE ALMEIDA MACHADO","tipo":"APOSENTADORIA","CargoNome":"MEDICO","meuCargo":"4","ato":"0007372020"}]

        String entrada = "W3siaWQiOiIxMjAzNiIsIm5vbWUiOiJTRUJBU1RJQU5BIENPU1RBIFNBTlRBTkEiLCJ0aXBvIjoiQVBPU0VOVEFET1JJQSIsIkNhcmdvTm9tZSI6IkFVWElMSUFSIEFETUlOSVNUUkFUSVZPIiwibWV1Q2FyZ28iOiI0IiwiYXRvIjoiMDAwNjA4MjAyMCJ9LHsiaWQiOiIxMjAzNyIsIm5vbWUiOiJGRVJOQU5ETyBERSBBTE1FSURBIE1BQ0hBRE8iLCJ0aXBvIjoiQVBPU0VOVEFET1JJQSIsIkNhcmdvTm9tZSI6Ik1FRElDTyIsIm1ldUNhcmdvIjoiNCIsImF0byI6IjAwMDczNzIwMjAifV0=";
        //[{"id":"12036","nome":"SEBASTIANA COSTA SANTANA","tipo":"APOSENTADORIA","CargoNome":"AUXILIAR ADMINISTRATIVO","meuCargo":"4","ato":"0006082020"},{"id":"12037","nome":"FERNANDO DE ALMEIDA MACHADO","tipo":"APOSENTADORIA","CargoNome":"MEDICO","meuCargo":"4","ato":"0007372020"}]

        String saida = "308006092a864886f70d010702a0803080020101310d300b0609608648016503040201308006092a864886f70d010701a08024800482017057337369615751694f6949784d6a417a4e694973496d3576625755694f694a5452554a425531524a5155354249454e505531524249464e42546c5242546b45694c434a3061584276496a6f69515642505530564f5645464554314a4a51534973496b4e68636d6476546d39745a534936496b465657456c4d535546534945464554556c4f53564e55556b465553565a504969776962575631513246795a3238694f6949304969776959585276496a6f694d4441774e6a41344d6a41794d434a394c487369615751694f6949784d6a417a4e794973496d3576625755694f694a4752564a4f51553545547942455253424254453146535552424945314251306842524538694c434a3061584276496a6f69515642505530564f5645464554314a4a51534973496b4e68636d6476546d39745a534936496b314652456c4454794973496d316c64554e68636d6476496a6f694e434973496d463062794936496a41774d44637a4e7a49774d6a41696656303d000000000000a080308206fc308204e4a00302010202087e1f21042939c0cd300d06092a864886f70d01010b05003059310b300906035504061302425231133011060355040a130a4943502d42726173696c31153013060355040b130c414320534f4c555449207635311e301c06035504031315414320534f4c555449204d756c7469706c61207635301e170d3231303432393133333830305a170d3234303432393133333830305a3081b7310b300906035504061302425231133011060355040a130a4943502d42726173696c311e301c060355040b1315414320534f4c555449204d756c7469706c6120763531173015060355040b130e333233383630383730303031373331133011060355040b130a50726573656e6369616c311a3018060355040b1311436572746966696361646f20504620413331293027060355040313205741524c455920464552524549524120474f49533a303134393936373331343030820122300d06092a864886f70d01010105000382010f003082010a0282010100a22aa8b39eb517f9f1db1ed2f1802d12a8affb95ab2315fc4dfba7f6e854dd1afc458c45783a8eac96482bf5d59907c159e1f8cd6197f4c246709f69a015fc04a7efbf2ffad92086c8898b94c68a6dee0e595ea8f30c7e9ba4c0c202c45fa5d3b2e494973693ed140951be8e303cbb086b4029c0a3ed24f6f8e341069087bd3806da4201f67e17a25cea154108d6812fcda4002e56e9ba03d10ddedc1bb85b3f02b2244c49c8544a41d830ce0a5c5e02d93216894a53699299e3393b6c61f65a56b474dc7c61b7e244b183923fdfd2871cf60c2e448e7adc9450ed1acaa595708bc4f7fe4890876f36e0afedf37ff901545d3b986c285fe04155256b71d2b9d30203010001a38202673082026330090603551d1304023000301f0603551d23041830168014c552ed258009df9c82c89f47c6ddb45f31ddb9b1305406082b0601050507010104483046304406082b060105050730028638687474703a2f2f6363642e6163736f6c7574692e636f6d2e62722f6c63722f61632d736f6c7574692d6d756c7469706c612d76352e7037623081960603551d1104818e30818b81167761726c6579662e676f697340676d61696c2e636f6da0380605604c010301a02f132d333030333139383630313439393637333134303030303030303030303030303030303030303030303030303030a0170605604c010306a00e130c303030303030303030303030a01e0605604c010305a015131330303030303030303030303030303030303030305d0603551d200456305430520606604c010203253048304606082b06010505070201163a687474703a2f2f6363642e6163736f6c7574692e636f6d2e62722f646f63732f6470632d61632d736f6c7574692d6d756c7469706c612e70646630290603551d250422302006082b0601050507030206082b06010505070304060a2b06010401823714020230818c0603551d1f048184308181303ea03ca03a8638687474703a2f2f6363642e6163736f6c7574692e636f6d2e62722f6c63722f61632d736f6c7574692d6d756c7469706c612d76352e63726c303fa03da03b8639687474703a2f2f636364322e6163736f6c7574692e636f6d2e62722f6c63722f61632d736f6c7574692d6d756c7469706c612d76352e63726c301d0603551d0e041604147eb72254ce1e6d51a5bd0de030261aa01cf3d64c300e0603551d0f0101ff0404030205e0300d06092a864886f70d01010b05000382020100004851b24b2b8d05c93060cb84e97889fc615190a38c340285bafe992ead7c7156f353badc60afc52792c767593564d6df067c84a5a1bccaa8f3422eaa1ca4ff6c99fc5268ff8124b85b290261651b3d91cdddde0677d321bb09dd9769b779055692e563570547dfda11c9a68ea2cd87c04bafced12c5eb3b01c6a61a60932625a8a3cbbefd722a6089111f17251f0b6ff8df0817b77130d9438a4d8a6dbd8b86b26c1d5241e23cc4741af97cdce6d59c4a6dc7e67a4bed67b6f032e90fa66063020347bd592d1cc5fdeb2b76f7772eb8fdad21b4ebc3df5ca1dca5efecbea91f6dd0af245f2cb2e68087ea618f06db5e95b9a059c236e557ba175f880ee3e396844881dd656ba26d76c112117f7bd892d57441887ac6ea57e83e74762f74d3bf0d4c2e75a010375f09f0cd945e32de036bc159c40ab4b7b0e9943284f4e4c0a28b0b7e7ed344809970f501debb1dee73570dc8a15295d24ba63012349af4077175938662ede0cdb61afd7bb2ca2e7c41ce76de7d3f353faacedc7139498b2e6ed462f323359da346462c0ef1b9f5ac8ac5eb7614cd1c09ca2279bcfe80dc563b7e1f04f480cc0bbe90f4ccb0e216593e1282bdd2d979c863eb08354b30b2cbb1bbc8d83579abc27b70bbd1d1479d24c2f9dd578f434cb4676bdca20da9954ba2d852c2fd66c3faed0564ad0f03b1b6c1449c3f782ee49fad3f422b90e9ac9660000318203ad308203a902010130653059310b300906035504061302425231133011060355040a130a4943502d42726173696c31153013060355040b130c414320534f4c555449207635311e301c06035504031315414320534f4c555449204d756c7469706c6120763502087e1f21042939c0cd300b0609608648016503040201a082021d301806092a864886f70d010903310b06092a864886f70d010701301c06092a864886f70d010905310f170d3231303930393135353930365a302f06092a864886f70d01090431220420c2c85fc850cf692ed7e3079a736c190468bb8809dba0a0ebd3d2ea9c62584d8d3072060b2a864886f70d0109100204316330610c54436f6e74656e742d547970653a20746578742f706c61696e0d0a436f6e74656e742d446973706f736974696f6e3a206174746163686d656e743b66696c656e616d653d22646f63756d656e746f2e747874220d0a06092a864886f70d010701308194060b2a864886f70d010910020f3181843081810608604c010701010202302f300b060960864801650304020104200f6fa2c6281981716c95c79899039844523b1c61c2c962289cdac7811feee29e30443042060b2a864886f70d01091005011633687474703a2f2f706f6c6974696361732e69637062726173696c2e676f762e62722f50415f41445f52425f76325f322e6465723081a6060b2a864886f70d010910022f31819630819330819030818d0420e23de13f8ad66c762098a796b2d337a2f44c8d84e4b1065fed04d860f09757453069305da45b3059310b300906035504061302425231133011060355040a130a4943502d42726173696c31153013060355040b130c414320534f4c555449207635311e301c06035504031315414320534f4c555449204d756c7469706c6120763502087e1f21042939c0cd300b06092a864886f70d01010b0482010088e3e106d5cfd6f323ebb698832f37e2c1efe5971a089e02b4d3439ec1afd5f778e2fa59afe09359c90b84dbe66f1edfdd770499155c3d296aeaa9761c7aa59f3f5e53d073f3bb4b17003e698bb3e2d4fcf34d3a3da7dfea03d94db6e408f6cbdcf71670995398d6a32102689099ae7a74f5280f2a72f53e75c7f9bff823e6bf0ff8ad5f3d1a1199eef9ffe47b016107656efd00b84b4c708a51572f4ce459d7e163a9eb2448094fae4a727d29d96ab99245c759f6b0324ac858f0a5ad0d9831c1d9d1a49306dff14c6c07160e79489f358a762d4afd99561161f33196be4cca13360849301acd957bb07e2a228e2616b741d415f8af3ef016de62c0cc6ffd6e000000000000";

        System.out.println("finalizaAssinatura(): " + finalizaAssinatura(processos, entrada, saida));

    }

}
