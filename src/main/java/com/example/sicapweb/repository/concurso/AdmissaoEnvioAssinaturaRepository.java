package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.ap.concurso.AdmissaoEnvioAssinatura;
import br.gov.to.tce.util.Date;
import com.example.sicapweb.exception.InvalitInsert;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.service.ChampionRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class AdmissaoEnvioAssinaturaRepository  extends DefaultRepository<AdmissaoEnvioAssinatura, BigInteger>
{
    @Value("${sso.oauth2.client_id}")
    private String sso_client_id;

    @Value("${sso.oauth2.client_secret}")
    private String sso_client_secret;
    public AdmissaoEnvioAssinaturaRepository(EntityManager em) {
        super(em);
    }


    public Integer insertProtocolo(String matricula, Integer ano, LocalDateTime dh_protocolo, Integer id_end_origem)  {
        Integer idProtocolo;
        try{

            Query query = entityManager.createNativeQuery(
                    "INSERT INTO SCP..PROTOCOLOS(ID_PROTOCOLO, ANO, DH_PROTOCOLO,MATRICULA,ID_ENT_ORIGEM,HASH) " +
                            "VALUES (:ID_PROTOCOLO, :ANO, CONVERT(datetime2, '"+ dh_protocolo+"' ),:MATRICULA,:ID_ENT_ORIGEM,:HASH)");

            Query query1 = entityManager.createNativeQuery(
                    " select coalesce(MAX(ID_PROTOCOLO), 0)+1 from SCP..PROTOCOLOS where ANO = YEAR(GETDATE()) "
            );
            idProtocolo =  (Integer)query1.getSingleResult();
            query.setParameter("ID_PROTOCOLO", idProtocolo);
            query.setParameter("ANO", ano);
            query.setParameter("MATRICULA", matricula);
            query.setParameter("ID_ENT_ORIGEM", id_end_origem);
            query.setParameter("HASH", generateHashBase64fromProtocolo(idProtocolo));
            query.executeUpdate();

         } catch (RuntimeException e){
            e.printStackTrace();
            throw new InvalitInsert("problema ao inserir protocolo no econtas. Favor contate o administrador do sicap!");
        } catch (NoSuchAlgorithmException a){
            a.printStackTrace();
            throw new InvalitInsert("problema ao codigo hash com base no id do protocolo . Favor contate o administrador do sicap!");
        }
        return idProtocolo;
    }


    public void insertProcesso(Integer numeroProcesso, Integer anoProcesso, Integer anoReferencia,Integer numeroProcessoPai,Integer anoProcessoPai,
                               Integer relatoria,String complemento, Integer assuntoCodigo, Integer classeAssunto,
                               Integer idProtocolo, Integer entidadeOrigem, Integer entidadeVinculada, Integer  idAssunto
    ) {
        try{
        Query query = entityManager.createNativeQuery(
                " INSERT INTO SCP..processo (" +
                        "                    processo_dtaass, processo_numero, processo_ano, pentids_ecodc_ccodg, pentids_ecodc_ccodc," +
                        "                    pentids_ecode, processo_modelo, processo_interes, processo_assunto, processo_mrefer," +
                        "                    processo_arefer, processo_dtaaut, processo_haut, processo_qtdvol, processo_status," +
                        "                    processo_dtafin, processo_usuario, processo_dtausu, processo_husu, processo_npai," +
                        "                    processo_apai, processo_origem, processo_aorigem, processo_julgmt, processo_nunreg," +
                        "                    processo_dtareg, processo_vlrcove, processo_numcov, processo_anocov, processo_vlrcov," +
                        "                    processo_dtinccv, processo_dtfincv, processo_tpconv, processo_coger, processo_numaps," +
                        "                    processo_anoaps, processo_cxcoofi, processo_obscoof, processo_relator, processo_relato1," +
                        "                    processo_distrib, processo_relpaut, processo_ev_codigo, processo_ev_classe, processo_ev_entids," +
                        "                    processo_ev_interes, processo_complemento, processo_assunto_codigo, processo_assunto_classe_assunto," +
                        "                    processo_nconv, processo_aconv, processo_codclassea, processo_codassunto, ult_depto, ult_resp, ult_data," +
                        "                    ult_hora, processo_fornec, proc_num_anexo, proc_ano_anexo, processo_datacad, processo_sigiloso, processo_alteracao_status_login," +
                        "                    processo_qtd_pag, processo_dta_tran_julg, processo_microfilmado, julgado, status_julg, data_microfilmagem," +
                        "                    data_digitalizacao, numero_microfilme, processo_descartado, processo_serie_cod, processo_cpf_resp," +
                        "                    num_protocolo, id_entidade_origem,id_entidade_vinc, id_assunto, ano_protocolo, processo_eletronico)" +
                        "                VALUES(" +
                        "                    NULL," +
                        "                    :procnumero," +
                        "                    :ano,  0,0, 0, 0, '', '', 0," +
                        "                    :anoreferencia,  getdate(), getdate(), 1, 'TRAMT', null, '', null, null," +
                        "                    :processoNpai," +
                        "                    :processoApai, 0, 0, '',  0, null, 0, 0, 0,0,null,null,'','',null,null,0,'',0,0," +
                        "                    :relatoria,  0, null, null, null, ''," +
                        "                    :complemento," +
                        "                    :assuntocodigo," +
                        "                    :classeassunto,  0, 0," +
                        "                    :classeassunto," +
                        "                    :assuntocodigo, '', '', null, null, '', null, null, null, 'N', '', 0,  null, '', '', '', null, null, '', '', 0, ''," +
                        "                    :idprotocolo," +
                        "                    :entidadeorigem," +
                        "                    :entidadevinculada," +
                        "                    :idassunto," +
                        "                    :ano," +
                        "                    'S') ");

        query.setParameter("procnumero", numeroProcesso);
        query.setParameter("ano", anoProcesso);
        query.setParameter("anoreferencia", anoReferencia);
        query.setParameter("processoNpai", numeroProcessoPai);
        query.setParameter("processoApai", anoProcessoPai);
        query.setParameter("relatoria", relatoria);
        query.setParameter("complemento", complemento);
        query.setParameter("assuntocodigo", assuntoCodigo);
        query.setParameter("classeassunto", classeAssunto);
        query.setParameter("idprotocolo", idProtocolo);
        query.setParameter("entidadeorigem", entidadeOrigem);
        query.setParameter("entidadevinculada", entidadeVinculada);
        query.setParameter("idassunto", idAssunto);
        query.executeUpdate();
    } catch (RuntimeException e){
            e.printStackTrace();
            throw new InvalitInsert("problema ao inserir processo no econtas. Favor contate o administrador do sicap!");
        }
    }

    public void insertAndamentoProcesso(Integer numeroProcesso, Integer anoProcesso){
        try {
            Query query = entityManager.createNativeQuery("INSERT INTO SCP..ProcessoAndamento(NumProc,AnoProc,Descricao,DHinsert,IdDepto)" +
                    "                VALUES (:procnumero, :ano, 'AUTUACAO', GETDATE(), 55)");
            query.setParameter("procnumero", numeroProcesso);
            query.setParameter("ano", anoProcesso);
            query.executeUpdate();
        }catch (RuntimeException e){
            e.printStackTrace();
            throw new InvalitInsert("problema ao inserir andamento do processo no econtas. Favor contate o administrador do sicap!");
        }
    }


    public void insertPessoaInteressada(Integer numeroProcesso, Integer anoProcesso, Integer idPessoa, Integer papelEmProcesso, Integer idCargo ){
        try {
        Query query = entityManager.createNativeQuery("INSERT INTO SCP..PESSOAS_PROCESSO (NUM_PROC, ANO_PROC, ID_PESSOA, ID_PAPEL, ID_CARGO)" +
                "                VALUES (:procnumero , :ano , :idpessoa , :papel , :idcargo )");
        query.setParameter("procnumero",numeroProcesso);
        query.setParameter("ano",anoProcesso);
        query.setParameter("idpessoa",idPessoa);
        query.setParameter("papel",papelEmProcesso);
        query.setParameter("idcargo",idCargo);
        query.executeUpdate();
        }catch (RuntimeException e){
            e.printStackTrace();
            throw new InvalitInsert("problema ao inserir pessoa interessada do processo no econtas. Favor contate o administrador do sicap!");
        }
    }


    public void insertHist(Integer numeroProcesso, Integer anoProcesso, String  deptoAutuacao,int idDeptAutuacao){
        try {
            Query query = entityManager.createNativeQuery("INSERT INTO SCP..hists" +
                    "(hcodp_pnumero, hcodp_pano, hists_data,hists_hora, hists_origem, hoent_ecodc_ccodg, hoent_ecodc_ccodc," +
                    "                         hoent_ecode, hdest_ldepto, hdest_llogin, hent_ecodc_ccodg, hent_ecodc_ccodc, hent_ecode, status," +
                    "                         hists_dest_resp, data_receb, hora_receb, data_env_depto, hora_env_depto,idDeptoOrigem , idDeptoDestino)" +
                    "                         VALUES" +
                    "(:procnumero, :ano, GETDATE(), GETDATE(), 'ENTRA', 0,0, 0, 'COPRO', '000003', 0, 0, 0, 'U', '000003', GETDATE(), GETDATE(), DATEADD(millisecond,7,getdate()), DATEADD(millisecond,7,getdate()), 55, 55)");
            query.setParameter("procnumero", numeroProcesso);
            query.setParameter("ano", anoProcesso);
            query.executeUpdate();

            Query query1 = entityManager.createNativeQuery("INSERT INTO SCP..hists" +
                    "(hcodp_pnumero, hcodp_pano, hists_data,hists_hora, hists_origem, hoent_ecodc_ccodg, hoent_ecodc_ccodc," +
                    "                         hoent_ecode, hdest_ldepto, hdest_llogin, hent_ecodc_ccodg, hent_ecodc_ccodc, hent_ecode, status," +
                    "                         hists_dest_resp, data_receb, hora_receb,idDeptoOrigem , idDeptoDestino)" +
                    "                         VALUES(" +
                    "                            :procnumero,:ano,DATEADD(millisecond,7,getdate()),DATEADD(millisecond,7,getdate()),'COPRO',0,0,0,:deptoAutuacao,'000003',0,0,0,'T','',null,null,55,:idDeptAutuacao)");

            query1.setParameter("procnumero", numeroProcesso);
            query1.setParameter("ano", anoProcesso);
            query1.setParameter("deptoAutuacao", deptoAutuacao);
            query1.setParameter("idDeptAutuacao",idDeptAutuacao);
            query1.executeUpdate();
        } catch (RuntimeException e ){
            e.printStackTrace();
            throw new InvalitInsert("problema ao inserir historico do processo no econtas. Favor contate o administrador do sicap!");

        }
    }



    public BigDecimal insertDocument(String tipoDocumento,Integer numeroProcesso, Integer anoProcesso, Integer eventoProcesso ){
        BigDecimal idDocument;
        try {
            Query query = entityManager.createNativeQuery("INSERT INTO SCP..document(docmt_tipo,dcnproc_pnumero,dcnproc_pano,docmt_numero,docmt_ano,docmt_depto,docmt_excluido" +
                    ",docmt_data,docmt_hora,login_usr,docmt_is_assinado,docmt_depto_doc,sigiloso, num_evento,idDeptoCriador , idDeptoJuntada)" +
                    "     VALUES (:tipodocumento,:procnumero,:ano,:numero,:ano,'COPRO','',getdate(),getdate(),'000003','S','COPRO','N', :evento,55,55)");
            query.setParameter("tipodocumento", tipoDocumento);
            query.setParameter("procnumero", numeroProcesso);
            //vai  precisar usar SolicitarNumeroSND para gerar o numero
            Integer idDocsnd = this.SolicitarNumeroDocumentoAoSistemaSND(5, "COCAP", anoProcesso, "000003");
            if (idDocsnd == null) throw new InvalitInsert("numero do documento não gerado!");
            query.setParameter("numero", idDocsnd);
            query.setParameter("ano", anoProcesso);
            query.setParameter("evento", eventoProcesso);
            query.executeUpdate();
            idDocument = (BigDecimal) entityManager.createNativeQuery("SELECT @@IDENTITY ").getSingleResult();
            if (idDocument==null)
                throw new InvalitInsert("Nao gerou documento tipo:"+tipoDocumento+" processo:"+numeroProcesso+'/'+anoProcesso);
            return idDocument;
        }catch (RuntimeException e ){
            e.printStackTrace();
            throw new InvalitInsert("problema ao inserir arquivo no processo do econtas. Favor contate o administrador do sicap!");
        }
    }

    public Integer SolicitarNumeroDocumentoAoSistemaSND(Integer tipoDoc, String codDepartamento, Integer ano, String Emissor) {
        Integer idDoc ;
        try {
            Query queryQuePegaNovoNumero = entityManager.createNativeQuery("select coalesce(MAX(numero), 0)+1   as numero from Snd..documento where cod_tipo_documento = :tipo_doc  and cod_departamento = :CodDepartamento and ano= :ano ");
            queryQuePegaNovoNumero.setParameter("tipo_doc", tipoDoc);
            queryQuePegaNovoNumero.setParameter("CodDepartamento", codDepartamento);
            queryQuePegaNovoNumero.setParameter("ano", ano);
            idDoc = (Integer) queryQuePegaNovoNumero.getSingleResult();
            if (idDoc != null) {
                Query queryQueInseriEmDocumentos = entityManager.createNativeQuery(" insert into Snd..documento " +
                        " (cod_tipo_documento, cod_departamento, numero , data_documento , data_gravacao , ano" +
                        "  , matricula_emissor,  status ,  elaboradoPor )" +
                        " values ( :tipo_doc,:CodDepartamento,:numero ,:data_documento , :data_gravacao, :ano" +
                        " , :emissor , :status, cast( :elaboradoPor  as text )  ) ");

                queryQueInseriEmDocumentos.setParameter("tipo_doc", tipoDoc);
                queryQueInseriEmDocumentos.setParameter("CodDepartamento", codDepartamento);
                queryQueInseriEmDocumentos.setParameter("numero", idDoc);
                LocalDateTime dt = LocalDateTime.now();
                queryQueInseriEmDocumentos.setParameter("data_documento", dt);
                queryQueInseriEmDocumentos.setParameter("data_gravacao", dt);
                queryQueInseriEmDocumentos.setParameter("ano", ano);
                queryQueInseriEmDocumentos.setParameter("emissor", Emissor);
                queryQueInseriEmDocumentos.setParameter("status", "CONFIRMADO");
                queryQueInseriEmDocumentos.setParameter("elaboradoPor", "Automatizado por Sistema de SICAP-AP");
                queryQueInseriEmDocumentos.executeUpdate();
            }
            return idDoc;
        } catch (RuntimeException e ){
            e.printStackTrace();
            throw new InvalitInsert("problema ao gerar um numero novo de documento do processo no econtas. Favor contate o administrador do sicap!");
        }
    }


    public void insertArquivoDocument(BigDecimal idDocumento, String arquivo, String  idDocumentoCastor   ){
        try {
            Query query = entityManager.createNativeQuery(" insert into SCP..DOCUMENT_ARQUIVOS (ID_DOCUMENT,NOME_ARQ,DESCRICAO,DATA,LOGIN_INSERIU,EXCLUIDO,UUID_CAS)" +
                    "Values (:id_documento,:arquivo,:arquivo,GETDATE(),'000003','N',:idDocumentoCastor)");
            query.setParameter("id_documento",idDocumento);
            query.setParameter("arquivo",arquivo);
            query.setParameter("idDocumentoCastor",idDocumentoCastor);
            query.executeUpdate();
        } catch (RuntimeException e ){
            e.printStackTrace();
            throw new InvalitInsert("problema ao inserir documento do processo no econtas. Favor contate o administrador do sicap!");
        }
    }

    public String insertCadunPessoaInterressada(String cpf , String nome) throws IOException, URISyntaxException {
        try {
            return ChampionRequest.salvarSimples(cpf, nome, "sicapap", sso_client_id, sso_client_secret).getBody();
        }catch (RuntimeException e ){
            e.printStackTrace();
            throw new InvalitInsert("problema ao gravar interessado no cadun!!");
        }
    }


    public Integer getIdPessoaFisicaNoCadun(String Cnpj) {
        try {
            return (Integer) entityManager.createNativeQuery("SELECT   up.idPessoaFisica FROM Cadun..PessoaJuridica pj " +
                    "                LEFT JOIN cadun..vwUnidadesPessoasCargos up on pj.cnpj = up.codunidadegestora and idCargo = 4 and dataFim is null " +
                    "                   WHERE pj.CNPJ ='" + Cnpj + "' or pj.CodigoUnidadeGestora = '" + Cnpj + "' ").setMaxResults(1).getSingleResult();
        }
        catch (NoResultException r){
            throw new RuntimeException("não achou pessoa fisica no cadun!!");
        }
        catch (RuntimeException e){
            throw new RuntimeException("Problema ao consultar Pessoa fisica no Cadun. Favor entrar em contato com o respável pelo Cadun!! ");
        }
    }

    public Integer getIdPessoaJuridicaNoCadun(String cnpj) {
        try {
            return (Integer) entityManager.createNativeQuery("SELECT  up.idPessoaJuridica FROM Cadun..PessoaJuridica pj " +
                    "                LEFT JOIN cadun..vwUnidadesPessoasCargos up on pj.cnpj = up.codunidadegestora and idCargo = 4 and dataFim is null " +
                    "                   WHERE pj.CNPJ ='" + cnpj + "' or pj.CodigoUnidadeGestora = '" + cnpj + "' ")
                    .setMaxResults(1)
                    .getSingleResult();
        }
        catch (NoResultException r){
            throw new RuntimeException("não achou pessoa Juridica no cadun!!");
        }
        catch (RuntimeException e){
            throw new RuntimeException("Problema ao consultar Pessoa Juridica no Cadun. Favor entrar em contato com o respável pelo Cadun!! ");
        }
    }

    private String generateHashBase64fromProtocolo(Integer idProtocolo) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        md.reset();
        String hashdecoded= "^TC3T0"+ (simpleDateFormat.format(new Date()))+idProtocolo+"*000003*^";
        byte[] hashdecodedbytes = hashdecoded.getBytes(Charset.forName("UTF-8"));
        byte[] bytesOfDigest = md.digest(hashdecodedbytes);
        return DatatypeConverter.printHexBinary(bytesOfDigest).substring(17,32).toUpperCase();
    }

}
