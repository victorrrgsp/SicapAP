package com.example.sicapweb.repository.concessao;

import br.gov.to.tce.model.adm.AdmEnvioAssinatura;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Map;

@Repository
public class AdmEnvioAssinaturaRepository extends DefaultRepository<AdmEnvioAssinatura, BigInteger> {

    public AdmEnvioAssinaturaRepository(EntityManager em) {
        super(em);
    }

    public Integer gerarProtocolo() {
        try {
            Query query = getEntityManager().createNativeQuery("" +
                    "SELECT coalesce(MAX(ID_PROTOCOLO), 0)+1 as idprotocolo from SCP..PROTOCOLOS where ANO = YEAR(GETDATE())");
            return (Integer) query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public void salvarProtocolo(Map<String, Object> protocolo) {
        try {
            Query query = getEntityManager().createNativeQuery(
                    "INSERT INTO SCP..PROTOCOLOS([ID_PROTOCOLO],[ANO],[DH_PROTOCOLO],[MATRICULA],[ID_ENT_ORIGEM],[HASH]) " +
                            "   VALUES (:idprotocolo, :ano, GETDATE(), :matricula, :entidadeorigem, :hash)");
            query.setParameter("idprotocolo", protocolo.get("idprotocolo"));
            query.setParameter("ano", Calendar.getInstance().get(Calendar.YEAR));
            query.setParameter("matricula", protocolo.get("matricula"));
            query.setParameter("entidadeorigem", protocolo.get("entidadeorigem"));
            query.setParameter("hash", protocolo.get("hash"));
            query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void salvarProcesso(Map<String, Object> processo) {
        try {
            Query query = getEntityManager().createNativeQuery("" +
                    "INSERT INTO SCP..processo ( " +
                    "     processo_dtaass, processo_numero, processo_ano, pentids_ecodc_ccodg, pentids_ecodc_ccodc, " +
                    "     pentids_ecode, processo_modelo, processo_interes, processo_assunto, processo_mrefer, " +
                    "     processo_arefer, processo_dtaaut, processo_haut, processo_qtdvol, processo_status, " +
                    "     processo_dtafin, processo_usuario, processo_dtausu, processo_husu, processo_npai, " +
                    "     processo_apai, processo_origem, processo_aorigem, processo_julgmt, processo_nunreg, " +
                    "     processo_dtareg, processo_vlrcove, processo_numcov, processo_anocov, processo_vlrcov, " +
                    "     processo_dtinccv, processo_dtfincv, processo_tpconv, processo_coger, processo_numaps, " +
                    "     processo_anoaps, processo_cxcoofi, processo_obscoof, processo_relator, processo_relato1, " +
                    "     processo_distrib, processo_relpaut, processo_ev_codigo, processo_ev_classe, processo_ev_entids, " +
                    "     processo_ev_interes, processo_complemento, processo_assunto_codigo, processo_assunto_classe_assunto, " +
                    "     processo_nconv, processo_aconv, processo_codclassea, processo_codassunto, ult_depto, ult_resp, ult_data, " +
                    "     ult_hora, processo_fornec, proc_num_anexo, proc_ano_anexo, processo_datacad, processo_sigiloso, processo_alteracao_status_login, " +
                    "     processo_qtd_pag, processo_dta_tran_julg, processo_microfilmado, julgado, status_julg, data_microfilmagem, " +
                    "     data_digitalizacao, numero_microfilme, processo_descartado, processo_serie_cod, processo_cpf_resp, " +
                    "     num_protocolo, id_entidade_origem,id_entidade_vinc, id_assunto, ano_protocolo, processo_eletronico)" +
                    "VALUES( " +
                    "     NULL, " +
                    "     :procnumero, " +
                    "     :ano,  0,0, 0, 0, '', '', 0, " +
                    "     :anoreferencia,  getdate(), getdate(), 1, 'TRAMT', null, '', null, null, " +
                    "     :processoNpai, " +
                    "     :processoApai, 0, 0, '',  0, null, 0, 0, 0,0,null,null,'','',null,null,0,'',0,0, " +
                    "     :relatoria,  0, null, null, null, '', " +
                    "     :complemento, " +
                    "     :assuntocodigo, " +
                    "     :classeassunto,  0, 0, " +
                    "     :classeassunto, " +
                    "     :assuntocodigo, '', '', null, null, '', null, null, null, 'N', '', 0,  null, '', '', '', null, null, '', '', 0, '', " +
                    "     :idprotocolo, " +
                    "     :entidadeorigem, " +
                    "     :entidadevinculada, " +
                    "     :idassunto, " +
                    "     :ano, " +
                    "     'S')");
            query.setParameter("procnumero", processo.get("procnumero"));
            query.setParameter("ano", processo.get("ano"));
            query.setParameter("anoreferencia", processo.get("anoreferencia"));
            query.setParameter("processoNpai", processo.get("processoNpai"));
            query.setParameter("processoApai", processo.get("processoApai"));
            query.setParameter("relatoria", processo.get("relatoria"));
            query.setParameter("complemento", processo.get("complemento"));
            query.setParameter("assuntocodigo", processo.get("assuntocodigo"));
            query.setParameter("classeassunto", processo.get("classeassunto"));
            query.setParameter("classeassunto", processo.get("classeassunto"));
            query.setParameter("assuntocodigo", processo.get("assuntocodigo"));
            query.setParameter("idprotocolo", processo.get("idprotocolo"));
            query.setParameter("entidadeorigem", processo.get("entidadeorigem"));
            query.setParameter("entidadevinculada", processo.get("entidadevinculada"));
            query.setParameter("idassunto", processo.get("idassunto"));
            query.setParameter("ano", processo.get("ano"));
            query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void salvarAndamentoProcesso(Map<String, Object> processo) {
        try {
            Query query = getEntityManager().createNativeQuery(
                    "INSERT INTO SCP..ProcessoAndamento([NumProc],[AnoProc],[Descricao],[MatriculaPessoa],[DHinsert],[IdDepto]) " +
                            "                VALUES (:procnumero, :ano, 'AUTUACAO', 0, GETDATE(), 55)");
            query.setParameter("procnumero", processo.get("procnumero"));
            query.setParameter("ano", processo.get("ano"));
            query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void salvarPessoasInteressadas(Map<String, Object> interessado) {
        try {
            Query query = getEntityManager().createNativeQuery(
                    "INSERT INTO SCP..PESSOAS_PROCESSO (NUM_PROC, ANO_PROC, ID_PESSOA, ID_PAPEL, ID_CARGO) " +
                            "                VALUES (:procnumero , :ano, :idpessoa, :papel, :idcargo)");
            query.setParameter("procnumero", interessado.get("procnumero"));
            query.setParameter("ano", interessado.get("ano"));
            query.setParameter("idpessoa", interessado.get("idpessoa"));
            query.setParameter("papel", interessado.get("papel"));
            query.setParameter("idcargo", interessado.get("idcargo"));
            query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void salvarHistorico1(Map<String, Object> processo) {
        try {
            Query query = getEntityManager().createNativeQuery(
                    "INSERT INTO SCP..hists " +
                            "(hcodp_pnumero, hcodp_pano, hists_data, hists_hora, hists_origem, hoent_ecodc_ccodg, hoent_ecodc_ccodc, " +
                            " hoent_ecode, hdest_ldepto, hdest_llogin, hent_ecodc_ccodg, hent_ecodc_ccodc, hent_ecode, status, " +
                            " hists_dest_resp, data_receb, hora_receb, data_env_depto, hora_env_depto) " +
                            "VALUES (:procnumero, :ano, GETDATE(), GETDATE(), 'ENTRA', 0, 0, 0, 'COPRO', '000003', 0, 0, 0, 'U', '000003', GETDATE(), GETDATE(), " +
                            "        DATEADD(millisecond, 7, getdate()), DATEADD(millisecond, 7, getdate()))");
            query.setParameter("procnumero", processo.get("procnumero"));
            query.setParameter("ano", processo.get("ano"));
            query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void salvarHistorico2(Map<String, Object> processo) {
        try {
            Query query = getEntityManager().createNativeQuery(
                    "INSERT INTO SCP..hists " +
                            "(hcodp_pnumero, hcodp_pano, hists_data, hists_hora, hists_origem, hoent_ecodc_ccodg, hoent_ecodc_ccodc, " +
                            " hoent_ecode, hdest_ldepto, hdest_llogin, hent_ecodc_ccodg, hent_ecodc_ccodc, hent_ecode, status, " +
                            " hists_dest_resp, data_receb, hora_receb) " +
                            "VALUES (:procnumero, :ano, DATEADD(millisecond, 7, getdate()), DATEADD(millisecond, 7, getdate()), 'COPRO', 0, 0, 0, 'COCAP', " +
                            "        '000003', 0, 0, 0, 'T', '', null, null)");
            query.setParameter("procnumero", processo.get("procnumero"));
            query.setParameter("ano", processo.get("ano"));
            query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void salvarDocumento(Map<String, Object> processo) {
        try {
            Query query = getEntityManager().createNativeQuery(
                    "INSERT INTO SCP..[document]( [docmt_tipo], [dcnproc_pnumero], [dcnproc_pano], [docmt_numero], [docmt_ano], [docmt_depto] " +
                            "                           , [docmt_excluido], [docmt_data], [docmt_hora], [login_usr], [docmt_is_assinado] " +
                            "                           , [docmt_depto_doc], [sigiloso], [num_evento]) " +
                            "VALUES ('TA', :procnumero, :ano, :procnumero, :ano, 'COPRO', '', getdate(), getdate(), '000003', 'S', 'COPRO', 'N', '1')");
            query.setParameter("procnumero", processo.get("procnumero"));
            query.setParameter("ano", processo.get("ano"));
            query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Integer buscarUltimoIdDocumento() {
        try {
            Query query = getEntityManager().createNativeQuery(
                    "select MAX(docmt_id) as id_documento from SCP..[document]");
            return (Integer) query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public void salvarArquivosDocumentos(Map<String, Object> arquivo, Integer idDocumento) {
        try {
            Query query = getEntityManager().createNativeQuery(
                    "insert into SCP..DOCUMENT_ARQUIVOS (ID_DOCUMENT,NOME_ARQ,DESCRICAO,DATA,LOGIN_INSERIU,EXCLUIDO,UUID_CAS) " +
                            "Values (:idDocumento,:arquivo,:arquivo,GETDATE(),'000003','N',:documentoCastor)");
            query.setParameter("idDocumento", idDocumento);
            query.setParameter("arquivo", arquivo.get("arquivo"));
            query.setParameter("documentoCastor", arquivo.get("idCastorFile"));
            query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
