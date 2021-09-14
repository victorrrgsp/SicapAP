// getCertificadoAssinatura: async function () {
//     var self = this;
//     var texto = '[{"id":"12036","nome":"SEBASTIANA COSTA SANTANA","tipo":"APOSENTADORIA","CargoNome":"AUXILIAR ADMINISTRATIVO","meuCargo":"4","ato":"0006082020"},{"id":"12037","nome":"FERNANDO DE ALMEIDA MACHADO","tipo":"APOSENTADORIA","CargoNome":"MEDICO","meuCargo":"4","ato":"0007372020"}]';
//     this.textoBase64 = btoa(texto);
//     $.ajax({
//         url: 'https://dev2.tce.to.gov.br/assinador/api/AssinadorWeb/obterToken',
//         type: 'POST',
//         success: function (data) {
//             var token = data['token'];
//             var tokenAssinado = data['tokenAssinado'];
//             self.conectarWebsocket(token, tokenAssinado)
//
//         },
//         error: function (data) {
//             console.log(data);
//         }
//     });
// },
//
// conectarWebsocket: async function (token, tokenAssinado) {
//     var self = this;
//     this.socket = new WebSocket("wss://localhost:57201/");
//     this.socket.onopen = function () {
//         console.log('[open] Connection established');
//         self.socket.send('{"token":"' + token + '","tokenAssinado":"' + tokenAssinado + '","comando":"verificar"}');
//         self.socket.send('{"nome_funcao_retorno_fim_assinatura":"finalizar","url_arquivo_mensagens_interface":"","certificado_filtro_tipo":"A1, A3","certificado_utilizar_repositorio_pkcs11":"false","certificado_diretorio_biblioteca_pkcs11":"","certificado_detectar_smart_card":"false","certificado_filtro_ac_raiz":"0","certificado_filtro_cnpj":"","certificado_filtro_cpf":"","certificado_filtrar_invalidos":"true","certificado_hash_selecionar":"","certificado_alerta_qtn_dias_expirar":"120","lista_ids_entrada_dados":"input_assinatura_inicializado","barra_progresso_tipo":"HTML","assinatura_permitir_lote":"true","lista_ids_saida_dados":"input_assinatura_saida","assinatura_operacao":"ASSINATURA","formato_saida_dados":"HEXADECIMAL","formato_entrada_dados":"HEXADECIMAL","assinatura_algoritmo_hash_conteudo":"SHA256","assinatura_pdf_contato":"Responsavel pela assinatura","assinatura_pdf_imagem_altura_largura":"15,15","assinatura_pdf_imagem_pagina":"1","assinatura_pdf_imagem_posicao_x_y":"15,15","assinatura_pdf_local":"Assinador PDF TCE-TO","assinatura_pdf_nomes_campos_assinaturas":"TCE-TO-Assinatura-Envio","assinatura_pdf_razao":"Assinatura de documento para envio ao TCE-TO","comando":"parametros"}');
//         self.socket.send('{"comando":"certificados"}');
//     };
//     this.socket.onmessage = function (event) {
//         if (event.data.includes('certificadoJSONDefault') == true) {
//             self.decodificarCertificadoJson(event.data);
//         }
//         if (event.data.includes('setParametroID') == true) {
//             self.setAssinatura(event.data);
//         }
//         if (event.data.includes('Não há certificados em seu repositório pessoal') == true) {
//             var nome = "Nenhum certificado foi encontrado no computador.";
//             document.getElementById("nome").value = nome;
//         }
//     };
//     this.socket.onclose = function (event) {
//         if (event.wasClean) {
//             console.log('[close] Connection closed cleanly: ' + event.reason);
//         } else {
//             console.log('[close] Connection died');
//         }
//     };
//     this.socket.onerror = function (error) {
//         console.log('[error] ' + error.message);
//     };
// },
//
// decodificarCertificadoJson: async function (event_data) {
//     event_data = JSON.parse(event_data);
//     event_data = event_data['certificadoJSONDefault'];
//     var certificado_json = JSON.parse(event_data);
//     // var nome = certificado_json['cn'];
//     // document.getElementById("nome").value = nome;
//     this.certificado = certificado_json['certificado'];
//     this.hash = certificado_json['hash'];
//     this.AssinadorWeb_InicializarAssinatura();
// },
//
// AssinadorWeb_InicializarAssinatura: async function () {
//     var self = this;
//     $.ajax({
//         url: "http://localhost:8081/assinarRemessa/inicializarAssinatura", // TROCAR PRA PRODUCAO
//         headers: {"Accept": "application/json", "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"},
//         type: 'POST',
//         data: {"certificado": this.certificado, "original": this.textoBase64},
//         success: function (data) {
//             self.desafio = data['sha256'];
//             self.socket.send('{"comando":"assinarPKCS1","hash":"' + self.hash + '","input_assinatura_inicializado":' + JSON.stringify(data['inicializado']) + ',"input_autenticacao_saida":""}');
//         },
//         error: function (data) {
//             console.log(data);
//         }
//     });
// },
//
// setAssinatura: async function (event_data) {
//     event_data = JSON.parse(event_data);
//     this.assinatura = event_data['input_assinatura_saida'];
//     this.AssinadorWeb_FinalizarAssinatura();
// },
//
// AssinadorWeb_FinalizarAssinatura: async function () {
//     var self = this;
//     $.ajax({
//         url: "http://localhost:8081/assinarRemessa/assinadorWebfinalizarAssinatura", // TROCAR PRA PRODUCAO
//         headers: {"Accept": "application/json", "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"},
//         type: 'POST',
//         data: {"desafio": this.desafio, "assinatura": self.assinatura, "original": this.textoBase64},
//
//         success: function (data) {
//             //alert(data['msg']);
//             data = data['dados'];
//             self.saida = data['assinatura'];
//             self.assinadorSicapApFinalizaAssinatura();
//         },
//         error: function (data) {
//             console.log(data);
//         }
//     });
// },
//
// assinadorSicapApFinalizaAssinatura: async function () {
//     //var self = this;
//     var processos = "W3siaWQiOiIxMjAzNiIsIm5vbWUiOiJTRUJBU1RJQU5BIENPU1RBIFNBTlRBTkEiLCJ0aXBvIjoiQVBPU0VOVEFET1JJQSIsIkNhcmdvTm9tZSI6IkFVWElMSUFSIEFETUlOSVNUUkFUSVZPIiwibWV1Q2FyZ28iOiI0IiwiYXRvIjoiMDAwNjA4MjAyMCJ9LHsiaWQiOiIxMjAzNyIsIm5vbWUiOiJGRVJOQU5ETyBERSBBTE1FSURBIE1BQ0hBRE8iLCJ0aXBvIjoiQVBPU0VOVEFET1JJQSIsIkNhcmdvTm9tZSI6Ik1FRElDTyIsIm1ldUNhcmdvIjoiNCIsImF0byI6IjAwMDczNzIwMjAifV0=";
//
//     var entrada = "W3siaWQiOiIxMjAzNiIsIm5vbWUiOiJTRUJBU1RJQU5BIENPU1RBIFNBTlRBTkEiLCJ0aXBvIjoiQVBPU0VOVEFET1JJQSIsIkNhcmdvTm9tZSI6IkFVWElMSUFSIEFETUlOSVNUUkFUSVZPIiwibWV1Q2FyZ28iOiI0IiwiYXRvIjoiMDAwNjA4MjAyMCJ9LHsiaWQiOiIxMjAzNyIsIm5vbWUiOiJGRVJOQU5ETyBERSBBTE1FSURBIE1BQ0hBRE8iLCJ0aXBvIjoiQVBPU0VOVEFET1JJQSIsIkNhcmdvTm9tZSI6Ik1FRElDTyIsIm1ldUNhcmdvIjoiNCIsImF0byI6IjAwMDczNzIwMjAifV0=";
//
//     $.ajax({
//         url: "http://localhost:8081/assinarRemessa/assinadorSicapApFinalizaAssinatura", // TROCAR PRA PRODUCAO
//         type: 'POST',
//         data: {"processos": processos, "entrada": entrada, "saida": this.saida},
//
//         success: function (data) {
//             data = JSON.parse(data);
//             alert(data['msg'])
//         },
//
//         error: function (data) {
//             console.log(data);
//         }
//     });
// },