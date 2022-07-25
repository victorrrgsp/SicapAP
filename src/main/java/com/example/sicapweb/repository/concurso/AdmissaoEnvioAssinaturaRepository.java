package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.ap.concurso.AdmissaoEnvioAssinatura;
import br.gov.to.tce.model.ap.concurso.EditalAprovado;
import br.gov.to.tce.model.ap.concurso.documento.DocumentoAdmissao;
import br.gov.to.tce.util.Date;
import com.example.sicapweb.exception.InvalitInsert;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.service.ChampionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
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
    public AdmissaoEnvioAssinaturaRepository(EntityManager em) {
        super(em);
    }

    Integer idProtocolo;
    public Integer insertProtocolo(String matricula, Integer ano, LocalDateTime dh_protocolo, Integer id_end_origem) throws NoSuchAlgorithmException {
        idProtocolo=null;
        Query query = entityManager.createNativeQuery(
                "INSERT INTO SCP..PROTOCOLOS(ID_PROTOCOLO, ANO, DH_PROTOCOLO,MATRICULA,ID_ENT_ORIGEM,HASH) " +
                        "VALUES (:ID_PROTOCOLO, :ANO, CONVERT(datetime2, '"+ dh_protocolo+"' ),:MATRICULA,:ID_ENT_ORIGEM,:HASH)");

        Query query1 = entityManager.createNativeQuery(
                "select coalesce(MAX(ID_PROTOCOLO), 0)+1 from SCP..PROTOCOLOS where ANO = YEAR(GETDATE()) "
        );
        //dh_protocolo
        idProtocolo =  (Integer)query1.getSingleResult();
        query.setParameter("ID_PROTOCOLO", idProtocolo);
        query.setParameter("ANO", ano);
        // query.setParameter("DH_PROTOCOLO", dh_protocolo);
        query.setParameter("MATRICULA", matricula);
        query.setParameter("ID_ENT_ORIGEM", id_end_origem);
        MessageDigest md = MessageDigest.getInstance("MD5");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        md.reset();
        String hashdecoded= "^TC3T0"+ (simpleDateFormat.format(new Date()))+idProtocolo+"*000003*^";
        byte[] hashdecodedbytes = hashdecoded.getBytes(Charset.forName("UTF-8"));
        byte[] bytesOfDigest = md.digest(hashdecodedbytes);
        String Hash = DatatypeConverter.printHexBinary(bytesOfDigest).substring(17,32).toUpperCase();
        query.setParameter("HASH", Hash);
        query.executeUpdate();

        //entityManager.flush();


        return idProtocolo;
    }


    public void insertProcesso(Integer procnumero, Integer ano, Integer anoreferencia,Integer processoNpai,Integer processoApai,
                               Integer relatoria,String complemento, Integer assuntocodigo, Integer classeassunto,
                               Integer idprotocolo, Integer entidadeorigem, Integer entidadevinculada, Integer  idassunto
    ){
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

        query.setParameter("procnumero",procnumero);
        query.setParameter("ano",ano);
        query.setParameter("anoreferencia",anoreferencia);
        query.setParameter("processoNpai",processoNpai);
        query.setParameter("processoApai",processoApai);
        query.setParameter("relatoria",relatoria);
        query.setParameter("complemento",complemento);
        query.setParameter("assuntocodigo",assuntocodigo);
        query.setParameter("classeassunto",classeassunto);
        query.setParameter("idprotocolo",idprotocolo);
        query.setParameter("entidadeorigem",entidadeorigem);
        query.setParameter("entidadevinculada",entidadevinculada);
        query.setParameter("idassunto",idassunto);
        query.executeUpdate();
        //entityManager.flush();


    }

    public void insertAndamentoProcesso(Integer procnumero, Integer ano){
        Query query = entityManager.createNativeQuery("INSERT INTO SCP..ProcessoAndamento(NumProc,AnoProc,Descricao,DHinsert,IdDepto)" +
                "                VALUES (:procnumero, :ano, 'AUTUACAO', GETDATE(), 55)");
        query.setParameter("procnumero",procnumero);
        query.setParameter("ano",ano);
        query.executeUpdate();
        //entityManager.flush();
    }


    public void insertPessoaInteressada(Integer procnumero, Integer ano, Integer idpessoa, Integer papel, Integer idcargo ){
        Query query = entityManager.createNativeQuery("INSERT INTO SCP..PESSOAS_PROCESSO (NUM_PROC, ANO_PROC, ID_PESSOA, ID_PAPEL, ID_CARGO)" +
                "                VALUES (:procnumero , :ano , :idpessoa , :papel , :idcargo )");
        query.setParameter("procnumero",procnumero);
        query.setParameter("ano",ano);
        query.setParameter("idpessoa",idpessoa);
        query.setParameter("papel",papel);
        query.setParameter("idcargo",idcargo);
        query.executeUpdate();
        //entityManager.flush();
    }


    public void insertHist(Integer procnumero, Integer ano, String  deptoAutuacao){
        Query query = entityManager.createNativeQuery("INSERT INTO SCP..hists" +
                "(hcodp_pnumero, hcodp_pano, hists_data,hists_hora, hists_origem, hoent_ecodc_ccodg, hoent_ecodc_ccodc," +
                "                         hoent_ecode, hdest_ldepto, hdest_llogin, hent_ecodc_ccodg, hent_ecodc_ccodc, hent_ecode, status," +
                "                         hists_dest_resp, data_receb, hora_receb, data_env_depto, hora_env_depto)" +
                "                         VALUES" +
                "(:procnumero, :ano, GETDATE(), GETDATE(), 'ENTRA', 0,0, 0, 'COPRO', '000003', 0, 0, 0, 'U', '000003', GETDATE(), GETDATE(), DATEADD(millisecond,7,getdate()), DATEADD(millisecond,7,getdate()))");
        query.setParameter("procnumero",procnumero);
        query.setParameter("ano",ano);
        query.executeUpdate();
        //entityManager.flush();

        Query query1 = entityManager.createNativeQuery("INSERT INTO SCP..hists" +
                "(hcodp_pnumero, hcodp_pano, hists_data,hists_hora, hists_origem, hoent_ecodc_ccodg, hoent_ecodc_ccodc," +
                "                         hoent_ecode, hdest_ldepto, hdest_llogin, hent_ecodc_ccodg, hent_ecodc_ccodc, hent_ecode, status," +
                "                         hists_dest_resp, data_receb, hora_receb)" +
                "                         VALUES(" +
                "                            :procnumero,:ano,DATEADD(millisecond,7,getdate()),DATEADD(millisecond,7,getdate()),'COPRO',0,0,0,:deptoAutuacao,'000003',0,0,0,'T','',null,null)");

        query1.setParameter("procnumero",procnumero);
        query1.setParameter("ano",ano);
        query1.setParameter("deptoAutuacao",deptoAutuacao);
        query1.executeUpdate();
        //entityManager.flush();
    }


    BigDecimal idDocument;
    public BigDecimal insertDocument(String tipodocumento,Integer procnumero, Integer ano, Integer evento ){
        idDocument=null;
        Query query = entityManager.createNativeQuery("INSERT INTO SCP..document(docmt_tipo,dcnproc_pnumero,dcnproc_pano,docmt_numero,docmt_ano,docmt_depto,docmt_excluido" +
                ",docmt_data,docmt_hora,login_usr,docmt_is_assinado,docmt_depto_doc,sigiloso, num_evento)" +
                "     VALUES (:tipodocumento,:procnumero,:ano,:numero,:ano,'COPRO','',getdate(),getdate(),'000003','S','COPRO','N', :evento)");
        query.setParameter("tipodocumento",tipodocumento);
        query.setParameter("procnumero",procnumero);
        //vai  precisar usar SolicitarNumeroSND para gerar o numero
        Integer idDocsnd = this.SolicitarNumeroSND(5,"COCAP",ano,"000003",null,"SICAP-AP");
        if (idDocsnd == null) throw  new InvalitInsert("numero do documento nao gerado!");
        query.setParameter("numero",idDocsnd);
        query.setParameter("ano",ano);
        query.setParameter("evento",evento);
        query.executeUpdate();
        //entityManager.flush();

        Query query2 = entityManager.createNativeQuery(
                "SELECT @@IDENTITY ");

        idDocument = (BigDecimal) query2.getSingleResult();

        return idDocument;
    }

    Integer idDoc;
    public Integer SolicitarNumeroSND(Integer tipo_doc, String CodDepartamento, Integer ano,String Emissor,String Assunto,String Sistema) {
        idDoc=null;
        Query query = entityManager.createNativeQuery("select coalesce(MAX(numero), 0)+1   as numero from Snd..documento where cod_tipo_documento = :tipo_doc  and cod_departamento = :CodDepartamento and ano= :ano ");
        query.setParameter("tipo_doc",tipo_doc);
        query.setParameter("CodDepartamento",CodDepartamento);
        query.setParameter("ano",ano);
        idDoc = (Integer) query.getSingleResult();

        if (idDoc != null ){
            Query query1 =entityManager.createNativeQuery(" insert into Snd..documento " +
                    " (cod_tipo_documento, cod_departamento, numero , data_documento , data_gravacao , ano" +
                    "  , matricula_emissor, assunto,  status ,  elaboradoPor )" +
                    " values ( :tipo_doc,:CodDepartamento,:numero ,:data_documento , :data_gravacao, :ano" +
                    " , :emissor, :assunto , :status,  :elaboradoPor   ) ");
            query1.setParameter("tipo_doc",tipo_doc);
            query1.setParameter("CodDepartamento",CodDepartamento);
            query1.setParameter("numero",idDoc);
            LocalDateTime dt =LocalDateTime.now();
            query1.setParameter("data_documento",dt);
            query1.setParameter("data_gravacao",dt);
            query1.setParameter("ano",ano);
            query1.setParameter("emissor",Emissor);
            query1.setParameter("assunto",Assunto);
            query1.setParameter("status","CONFIRMADO");
            query1.setParameter("elaboradoPor","Automatizado por Sistema de "+Sistema+"");
            query1.executeUpdate();
            return idDoc;
        }
        else {
            return null;
        }
    }


    public void insertArquivoDocument(BigDecimal id_documento, String arquivo, String  idDocumentoCastor   ){
        Query query = entityManager.createNativeQuery(" insert into SCP..DOCUMENT_ARQUIVOS (ID_DOCUMENT,NOME_ARQ,DESCRICAO,DATA,LOGIN_INSERIU,EXCLUIDO,UUID_CAS)" +
                "Values (:id_documento,:arquivo,:arquivo,GETDATE(),'000003','N',:idDocumentoCastor)");
        query.setParameter("id_documento",id_documento);
        query.setParameter("arquivo",arquivo);
        query.setParameter("idDocumentoCastor",idDocumentoCastor);
        query.executeUpdate();

    }

    public Integer getEventoProcesso(Integer procnumero,Integer ano){
        Query query = entityManager.createNativeQuery(" Select coalesce(MAX(ID_PROTOCOLO), 0)+1 from SCP..document d WHERE d.dcnproc_pano = :ano AND d.dcnproc_pnumero = :procnumero");
        query.setParameter("procnumero",procnumero);
        query.setParameter("ano",ano);
        return (Integer) query.getSingleResult();
    }

    Integer idPessoa;
    public String insertCadunPessoaInterressada(String cpf , String nome) throws IOException, URISyntaxException {

        ResponseEntity<String> e = ChampionRequest.salvarSimples(cpf, nome, "sicapap", "7ed46ae476e58c3884b6062787b6b43ca351b5d9c1b415ed1934ee5d4309dbdb", "08a64646a9343f7f5400906256d1f872400ae58ade2bb6c572ed19d7c9cdd73c");
        return e.getBody();

    }


    public Integer getidCADUNPF(String Cnpj){
        List<Integer> cadunL = entityManager.createNativeQuery("SELECT  up.idPessoaFisica FROM Cadun..PessoaJuridica pj " +
                "                LEFT JOIN cadun..vwUnidadesPessoasCargos up on pj.cnpj = up.codunidadegestora and idCargo = 4 and dataFim is null " +
                "                   WHERE pj.CNPJ ='"+Cnpj+ "' or pj.CodigoUnidadeGestora = '"+Cnpj+"' ").getResultList();
        if (cadunL.size()>0) {
            return    cadunL.get(0);
        }
        return null;
    }

    public Integer getidCADUNPJ(String Cnpj){
        Query query= entityManager.createNativeQuery("SELECT  up.idPessoaJuridica FROM Cadun..PessoaJuridica pj " +
                "                LEFT JOIN cadun..vwUnidadesPessoasCargos up on pj.cnpj = up.codunidadegestora and idCargo = 4 and dataFim is null " +
                "                   WHERE pj.CNPJ ='"+Cnpj+ "' or pj.CodigoUnidadeGestora = '"+Cnpj+"' ");
        List<Integer> cadunL = query.getResultList();
        if (cadunL.size()>0) {

                return    cadunL.get(0);

        }
        return null;
    }



}
