package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.ap.concurso.AdmissaoEnvioAssinatura;
import br.gov.to.tce.model.ap.concurso.EditalAprovado;
import br.gov.to.tce.model.ap.concurso.documento.DocumentoAdmissao;
import br.gov.to.tce.util.Date;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.xml.bind.DatatypeConverter;
import java.math.BigDecimal;
import java.math.BigInteger;
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
                "INSERT INTO SICAPAP21W..PROTOCOLOS(ID_PROTOCOLO, ANO, DH_PROTOCOLO,MATRICULA,ID_ENT_ORIGEM,HASH) " +
                        "VALUES (:ID_PROTOCOLO, :ANO, CONVERT(datetime2, '"+ dh_protocolo+"' ),:MATRICULA,:ID_ENT_ORIGEM,:HASH)");

        Query query1 = entityManager.createNativeQuery(
                "select coalesce(MAX(ID_PROTOCOLO), 0)+1 from SICAPAP21W..PROTOCOLOS where ANO = YEAR(GETDATE()) "
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
                " INSERT INTO SICAPAP21W..processo (" +
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
        Query query = entityManager.createNativeQuery("INSERT INTO SICAPAP21W..ProcessoAndamento(NumProc,AnoProc,Descricao,DHinsert,IdDepto)" +
                "                VALUES (:procnumero, :ano, 'AUTUACAO', GETDATE(), 55)");
        query.setParameter("procnumero",procnumero);
        query.setParameter("ano",ano);
        query.executeUpdate();
        //entityManager.flush();
    }


    public void insertPessoaInteressada(Integer procnumero, Integer ano, Integer idpessoa, Integer papel, Integer idcargo ){
        Query query = entityManager.createNativeQuery("INSERT INTO SICAPAP21W..PESSOAS_PROCESSO (NUM_PROC, ANO_PROC, ID_PESSOA, ID_PAPEL, ID_CARGO)" +
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
        Query query = entityManager.createNativeQuery("INSERT INTO SICAPAP21W..hists" +
                "(hcodp_pnumero, hcodp_pano, hists_data,hists_hora, hists_origem, hoent_ecodc_ccodg, hoent_ecodc_ccodc," +
                "                         hoent_ecode, hdest_ldepto, hdest_llogin, hent_ecodc_ccodg, hent_ecodc_ccodc, hent_ecode, status," +
                "                         hists_dest_resp, data_receb, hora_receb, data_env_depto, hora_env_depto)" +
                "                         VALUES" +
                "(:procnumero, :ano, GETDATE(), GETDATE(), 'ENTRA', 0,0, 0, 'COPRO', '000003', 0, 0, 0, 'U', '000003', GETDATE(), GETDATE(), DATEADD(millisecond,7,getdate()), DATEADD(millisecond,7,getdate()))");
        query.setParameter("procnumero",procnumero);
        query.setParameter("ano",ano);
        query.executeUpdate();
        //entityManager.flush();

        Query query1 = entityManager.createNativeQuery("INSERT INTO SICAPAP21W..hists" +
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
        Query query = entityManager.createNativeQuery("INSERT INTO SICAPAP21W..document(docmt_tipo,dcnproc_pnumero,dcnproc_pano,docmt_numero,docmt_ano,docmt_depto,docmt_excluido" +
                ",docmt_data,docmt_hora,login_usr,docmt_is_assinado,docmt_depto_doc,sigiloso, num_evento)" +
                "     VALUES (:tipodocumento,:procnumero,:ano,:numero,:ano,'COPRO','',getdate(),getdate(),'000003','S','COPRO','N', :evento)");
        query.setParameter("tipodocumento",tipodocumento);
        query.setParameter("procnumero",procnumero);
        //vai  precisar usar SolicitarNumeroSND para gerar o numero
        query.setParameter("numero",procnumero+evento);
        query.setParameter("ano",ano);
        query.setParameter("evento",evento);
        query.executeUpdate();
        //entityManager.flush();

        Query query2 = entityManager.createNativeQuery(
                "SELECT @@IDENTITY ");

        idDocument = (BigDecimal) query2.getSingleResult();

        return idDocument;
    }

    public BigDecimal SolicitarNumeroSND(String tipo_doc, String CodDepartamento, Integer ano,String Emissor,String Assunto,String Sistema) {
        Query query = entityManager.createNativeQuery("select ");

        return (BigDecimal.valueOf(0));
    }


    public void insertArquivoDocument(BigDecimal id_documento, String arquivo, String  idDocumentoCastor   ){
        Query query = entityManager.createNativeQuery(" insert into SICAPAP21W..DOCUMENT_ARQUIVOS (ID_DOCUMENT,NOME_ARQ,DESCRICAO,DATA,LOGIN_INSERIU,EXCLUIDO,UUID_CAS)" +
                "Values (:id_documento,:arquivo,:arquivo,GETDATE(),'000003','N',:idDocumentoCastor)");
        query.setParameter("id_documento",id_documento);
        query.setParameter("arquivo",arquivo);
        query.setParameter("idDocumentoCastor",idDocumentoCastor);
        query.executeUpdate();
        //entityManager.flush();

    }

    public Integer getEventoProcesso(Integer procnumero,Integer ano){
        Query query = entityManager.createNativeQuery(" Select coalesce(MAX(ID_PROTOCOLO), 0)+1 from SICAPAP21W..document d WHERE d.dcnproc_pano = :ano AND d.dcnproc_pnumero = :procnumero");
        query.setParameter("procnumero",procnumero);
        query.setParameter("ano",ano);
        return (Integer) query.getSingleResult();
    }

    Integer idPessoa;
    public Integer insertCadunPessoaInterressada(String cpf , String nome,String ip,String usuario){
        idPessoa=null;
        Integer idpessoacad = null;
            List<Integer> lp = entityManager.createNativeQuery(" Select Codigo from SICAPAP21W..PessoaFisica d where cpf = :cpf " ).setParameter("cpf",cpf).getResultList();
            if (lp.size()>0) {
                 idpessoacad=lp.get(0);
            }
        if ( idpessoacad ==null){
                Query query1 = entityManager.createNativeQuery(" Select coalesce(MAX(Codigo), 0)+1 from SICAPAP21W..PessoaFisica d ");
                idPessoa =  (Integer)query1.getSingleResult() ;

                Query query = entityManager.createNativeQuery(" insert into SICAPAP21W..PessoaFisica (Codigo,cpf,Nome,data_cr,ip_cr,usuario_cr)" +
                        "Values (:Codigo,:cpf,:nome,GETDATE(),:ip , :usuario)");
                query.setParameter("Codigo",idPessoa);
                query.setParameter("cpf",cpf);
                query.setParameter("nome",nome);
                query.setParameter("ip",ip);
                query.setParameter("usuario",usuario);
                query.executeUpdate();
            }
            else{
                idPessoa = (idpessoacad);
            }
            return idPessoa;

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
