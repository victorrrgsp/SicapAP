package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.util.Date;
import br.gov.to.tce.model.ap.concurso.ConcursoEnvioAssinatura;
import com.example.sicapweb.exception.InvalitInsert;
import com.example.sicapweb.repository.DefaultRepository;
import net.bytebuddy.asm.Advice;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;
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
public class ConcursoEnvioAssinaturaRepository  extends DefaultRepository<ConcursoEnvioAssinatura, BigInteger>  {

    public ConcursoEnvioAssinaturaRepository(EntityManager em) {
        super(em);
    }


    public Integer insertProtocolo(String matricula,Integer ano, LocalDateTime dh_protocolo, Integer id_end_origem) throws NoSuchAlgorithmException {
        Integer idProtocolo;
        try {
            Query query = entityManager.createNativeQuery(
                    "INSERT INTO SCP..PROTOCOLOS(ID_PROTOCOLO, ANO, DH_PROTOCOLO,MATRICULA,ID_ENT_ORIGEM,HASH) " +
                            "VALUES (:ID_PROTOCOLO, :ANO, CONVERT(datetime2, '" + dh_protocolo + "' ),:MATRICULA,:ID_ENT_ORIGEM,:HASH)");


            Query query1 = entityManager.createNativeQuery(
                    "select coalesce(MAX(ID_PROTOCOLO), 0)+1 from SCP..PROTOCOLOS where ANO = YEAR(GETDATE()) "
            );
            idProtocolo = (Integer) query1.getSingleResult();
            query.setParameter("ID_PROTOCOLO", idProtocolo);
            query.setParameter("ANO", ano);
            query.setParameter("MATRICULA", matricula);
            query.setParameter("ID_ENT_ORIGEM", id_end_origem);
            query.setParameter("HASH", GenerateHashBase64fromProtocolo(idProtocolo));
            query.executeUpdate();
        }catch (RuntimeException e){
            e.printStackTrace();
            throw new InvalitInsert("problema ao inserir protocolo no econtas. Favor contate o administrador do sicap!");
        }

        return idProtocolo;
    }


    public void insertProcesso(Integer numeroProcesso, Integer anoProcesso, Integer anoReferencia,Integer numeroProcessoPai,Integer anoProcessoPai,
                               Integer relatoria,String complemento, Integer codigoAssunto, Integer classeAssunto,
                               Integer idProtocolo, Integer entidadeOrigem, Integer entidadeVinculada, Integer  idAssunto
                               ){
        try {
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
            query.setParameter("assuntocodigo", codigoAssunto);
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

    public void insertAndamentoProcesso(Integer procnumero, Integer ano){
        try {
            Query query = entityManager.createNativeQuery("INSERT INTO SCP..ProcessoAndamento(NumProc,AnoProc,Descricao,DHinsert,IdDepto,MatriculaPessoa)" +
                    "                VALUES (:procnumero, :ano, 'AUTUACAO', GETDATE(), 55,0)");
            query.setParameter("procnumero", procnumero);
            query.setParameter("ano", ano);
            query.executeUpdate();
        } catch (RuntimeException e){
            e.printStackTrace();
            throw new InvalitInsert("problema ao inserir andamento do processo no econtas. Favor contate o administrador do sicap!");
        }
    }


    public void insertProcEdital(Integer procnumero, Integer ano, Integer numedital, Integer anoedital ){
        try {
        Query query = entityManager.createNativeQuery("INSERT INTO SCP..ProcEdital (NumProc, AnoProc, NumEdital, AnoEdital, MatriculaInsert, Situacao )" +
                "                VALUES (:procnumero, :ano, :numedital, :anoedital, 000003, 1 )");
        query.setParameter("procnumero",procnumero);
        query.setParameter("ano",ano);
        query.setParameter("numedital",numedital);
        query.setParameter("anoedital",anoedital);
        query.executeUpdate();
        } catch (RuntimeException e){
            e.printStackTrace();
            throw new InvalitInsert("problema ao inserir edital no processo do econtas. Favor contate o administrador do sicap!");
        }
    }

    public void insertPessoaInteressada(Integer procnumero, Integer ano, Integer idpessoa, Integer papel, Integer idcargo ){
        try {
            Query query = entityManager.createNativeQuery("INSERT INTO SCP..PESSOAS_PROCESSO (NUM_PROC, ANO_PROC, ID_PESSOA, ID_PAPEL, ID_CARGO)" +
                    "                VALUES (:procnumero , :ano , :idpessoa , :papel , :idcargo )");
            query.setParameter("procnumero", procnumero);
            query.setParameter("ano", ano);
            query.setParameter("idpessoa", idpessoa);
            query.setParameter("papel", papel);
            query.setParameter("idcargo", idcargo);
            query.executeUpdate();
        } catch (RuntimeException e){
            e.printStackTrace();
            throw new InvalitInsert("problema ao inserir pessoa interessada no processo do econtas. Favor contate o administrador do sicap!");
        }
    }


    public void insertHist(Integer numeroProcesso, Integer anoProcesso, String  deptoAutuacao, int idDeptoAutuacao){
        try {
            Query query = entityManager.createNativeQuery("INSERT INTO SCP..hists" +
                    "(hcodp_pnumero, hcodp_pano, hists_data,hists_hora, hists_origem, hoent_ecodc_ccodg, hoent_ecodc_ccodc," +
                    "                         hoent_ecode, hdest_ldepto, hdest_llogin, hent_ecodc_ccodg, hent_ecodc_ccodc, hent_ecode, status," +
                    "                         hists_dest_resp, data_receb, hora_receb, data_env_depto, hora_env_depto,idDeptoOrigem , idDeptoDestino)" +
                    "                         VALUES" +
                    "(:procnumero, :ano, GETDATE(), GETDATE(), 'ENTRA', 0,0, 0, 'COPRO', '000003', 0, 0, 0, 'U', '000003', GETDATE(), GETDATE(), DATEADD(millisecond,7,getdate()), DATEADD(millisecond,7,getdate()),169,55)");
            query.setParameter("procnumero", numeroProcesso);
            query.setParameter("ano", anoProcesso);
            query.executeUpdate();

            Query query1 = entityManager.createNativeQuery("INSERT INTO SCP..hists" +
                    "(hcodp_pnumero, hcodp_pano, hists_data,hists_hora, hists_origem, hoent_ecodc_ccodg, hoent_ecodc_ccodc," +
                    "                         hoent_ecode, hdest_ldepto, hdest_llogin, hent_ecodc_ccodg, hent_ecodc_ccodc, hent_ecode, status," +
                    "                         hists_dest_resp, data_receb, hora_receb,idDeptoOrigem , idDeptoDestino)" +
                    "                         VALUES(" +
                    "                            :procnumero,:ano,DATEADD(millisecond,7,getdate()),DATEADD(millisecond,7,getdate()),'COPRO',0,0,0,:deptoAutuacao,'000003',0,0,0,'T','',null,null,55,:idDeptoAutuacao)");

            query1.setParameter("procnumero", numeroProcesso);
            query1.setParameter("ano", anoProcesso);
            query1.setParameter("deptoAutuacao", deptoAutuacao);
            query1.setParameter("idDeptoAutuacao",idDeptoAutuacao);
            query1.executeUpdate();
        } catch (RuntimeException e){
            e.printStackTrace();
            throw new InvalitInsert("problema ao inserir historico do  processo no econtas. Favor contate o administrador do sicap!");
        }
    }


    public BigDecimal insertDocument(String tipoDocumento,Integer numeroProcesso, Integer anoProcesso, Integer eventoProcesso ){
        BigDecimal idDocument;
        try {
            Query query = entityManager.createNativeQuery("INSERT INTO SCP..document(docmt_tipo,dcnproc_pnumero,dcnproc_pano,docmt_numero,docmt_ano,docmt_depto,docmt_excluido" +
                    ",docmt_data,docmt_hora,login_usr,docmt_is_assinado,docmt_depto_doc,sigiloso, num_evento,idDeptoOrigem , idDeptoDestino)" +
                    "     VALUES (:tipodocumento,:procnumero,:ano,:procnumero,:ano,'COPRO','',getdate(),getdate(),'000003','S','COPRO','N', :evento,55,55)");
            query.setParameter("tipodocumento", tipoDocumento);
            query.setParameter("procnumero", numeroProcesso);
            query.setParameter("ano", anoProcesso);
            query.setParameter("evento", eventoProcesso);
            query.executeUpdate();
            idDocument = (BigDecimal) entityManager.createNativeQuery(
                    "SELECT @@IDENTITY ").getSingleResult();
            if (idDocument == null) throw  new InvalitInsert("não inseriu registro do documento!!");
        } catch (RuntimeException e){
            e.printStackTrace();
            throw new InvalitInsert("problema ao inserir documento do processo no econtas. Favor contate o administrador do sicap!");
        }

        return idDocument;
    }


    public void insertArquivoDocument(BigDecimal id_documento, String arquivo, String  idDocumentoCastor   ){
        try {
            Query query = entityManager.createNativeQuery(" insert into SCP..DOCUMENT_ARQUIVOS (ID_DOCUMENT,NOME_ARQ,DESCRICAO,DATA,LOGIN_INSERIU,EXCLUIDO,UUID_CAS)" +
                    "Values (:id_documento,:arquivo,:arquivo,GETDATE(),'000003','N',:idDocumentoCastor)");
            query.setParameter("id_documento", id_documento);
            query.setParameter("arquivo", arquivo);
            query.setParameter("idDocumentoCastor", idDocumentoCastor);
            query.executeUpdate();
        }catch (RuntimeException e){
            e.printStackTrace();
            throw new InvalitInsert("problema ao inserir arquivo de um documento no econtas. Favor contate o administrador do sicap!");
        }
    }

    public String GetDescricaoArquivoEdital(String inciso, Integer fase){
            if ((inciso + "-" + fase.toString()).equals("I-1") )  return "01- Ofício subscrito pela autoridade competente";
            else if ((inciso + '-' + fase.toString()).equals("II-1") )  return "02- Justificativa para a abertura do concurso público" ;
            else if ((inciso + '-' + fase.toString()).equals("III-1") )  return "03- Demonstrativo de despesa do impacto orçamentário-financeiro";
            else if ((inciso + '-' + fase.toString()).equals("IV-1") )  return "04- Declaração de despesa da autorização para realização do concurso público";
            else if ((inciso + '-'+ fase.toString()).equals("V-1") )  return "05- Demonstrativo informando o percentual da despesa total com pessoal";
            else if ((inciso + '-' + fase.toString()).equals("VI-1") )  return "06- Ato expedido pela autoridade competente designando a comissão examinadora/julgadora";
            else if ((inciso + '-'+ fase.toString()).equals("VII-1") )  return "07- Demonstrativo do quadro de pessoal efetivo e quantidade de vagas criadas por lei";
            else if ((inciso + '-'+ fase.toString()).equals("VIII-1") )  return "08- Lei(s) de criação e/ou alteração dos cargos disponibilizados no Edital";
            else if ((inciso + '-' + fase.toString()).equals("IX-1") )   return "09- Documentos da contratação da entidade promotora do certame";
            else if ((inciso + '-' + fase.toString()).equals("IX.I-1") ) return "09- Comprovante de cadastramento no SICAP-LCO";
            else if ((inciso + '-' + fase.toString()).equals("X-1") )  return "10- Edital de abertura do concurso público e o respectivo comprovante de publicação";
            else if ((inciso + '-' + fase.toString()).equals("XI-1") )  return "11- Demais editais do concurso público, quando houver";
            else if ((inciso+'-'+fase.toString()).equals("XI-2") )  return "11- Demais editais do concurso público, quando houver";
            else if ((inciso + '-' + fase.toString()).equals("XII-2") )  return "12- Relação de candidatos inscritos para o concurso público";
            else if ((inciso + '-' + fase.toString()).equals("XIII-2") )  return "13- Lista de presença dos candidatos";
            else if ((inciso + '-' + fase.toString()).equals("XIV-2") )  return "14- Ata ou relatório final dos trabalhos realizados na promoção do concurso público";
            else if ((inciso + '-' + fase.toString()).equals("XV-2") ) return "15- Ato de homologação do resultado do concurso público e lista de aprovados";
            else if ((inciso + '-' + fase.toString()).equals("XVI-2") ) return "16- Demais documentos exigidos em legislação específica de concurso público";
            else if ((inciso + '-' + fase.toString()).equals("sem-1") )  return "Outros";
            else if ((inciso + '-' + fase.toString()).equals("sem-2") )  return "outros";
            else throw new InvalitInsert("Descrição não Encontrada!");
    }



    public Integer getidCADUNPF(String Cnpj){
        List<Integer> cadunL = entityManager.createNativeQuery("SELECT  up.idPessoaFisica FROM Cadun..PessoaJuridica pj " +
                "                LEFT JOIN cadun..vwUnidadesPessoasCargos up on pj.cnpj = up.codunidadegestora and idCargo = 4 and dataFim is null " +
                "                   WHERE pj.CNPJ ='"+Cnpj+ "' or pj.CodigoUnidadeGestora = '"+Cnpj+"' ").getResultList();
        if (cadunL.size()>0) {
            return cadunL.get(0);
        }
        return null;
    }

    public Integer getidCADUNPJ(String Cnpj){
        List<Integer> cadunL = entityManager.createNativeQuery("SELECT  up.idPessoaJuridica FROM Cadun..PessoaJuridica pj " +
                "                LEFT JOIN cadun..vwUnidadesPessoasCargos up on pj.cnpj = up.codunidadegestora and idCargo = 4 and dataFim is null " +
                "                   WHERE pj.CNPJ ='"+Cnpj+ "' or pj.CodigoUnidadeGestora = '"+Cnpj+"' ").getResultList();
        if (cadunL.size()>0) {
            return cadunL.get(0);
        }
        return null;
    }


    private String GenerateHashBase64fromProtocolo(Integer idProtocolo) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        md.reset();
        String hashdecoded= "^TC3T0"+ (simpleDateFormat.format(new Date()))+idProtocolo+"*000003*^";
        byte[] hashdecodedbytes = hashdecoded.getBytes(Charset.forName("UTF-8"));
        byte[] bytesOfDigest = md.digest(hashdecodedbytes);
        return  DatatypeConverter.printHexBinary(bytesOfDigest).substring(17,32).toUpperCase();
    }

}
