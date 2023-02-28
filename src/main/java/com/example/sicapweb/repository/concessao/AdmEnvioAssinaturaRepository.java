package com.example.sicapweb.repository.concessao;

import br.gov.to.tce.model.adm.AdmEnvioAssinatura;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;
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
                    "IF NOT EXISTS(SELECT * " +
                            "              FROM SCP..PROTOCOLOS " +
                            "              WHERE [ID_PROTOCOLO] = :idprotocolo " +
                            "                AND [ANO] = :ano " +
                            "                AND [MATRICULA] = :matricula " +
                            "                AND [ID_ENT_ORIGEM] = :entidadeorigem " +
                            "                AND [HASH] = :hash) " +
                            "    BEGIN " +
                            "        INSERT INTO SCP..PROTOCOLOS([ID_PROTOCOLO], [ANO], [DH_PROTOCOLO], [MATRICULA], [ID_ENT_ORIGEM], [HASH]) " +
                            "        VALUES (:idprotocolo, :ano, GETDATE(), :matricula, :entidadeorigem, :hash) " +
                            "    END");
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

    BigDecimal idProcesso;

    @Transactional
    public void salvarProcesso(Map<String, Object> processo) {
        try {
            Query query = getEntityManager().createNativeQuery(
                    "IF NOT EXISTS(SELECT * " +
                            "              FROM SCP..processo " +
                            "              WHERE processo_dtaass is null AND processo_numero = :procnumero AND processo_ano = :ano " +
                            "                AND pentids_ecodc_ccodg = 0 AND pentids_ecodc_ccodc = 0 AND pentids_ecode = 0 " +
                            "                AND processo_modelo = 0 AND processo_interes = '' AND processo_assunto = '' AND processo_mrefer = 0 " +
                            "                AND processo_qtdvol = 1 AND processo_status = 'TRAMT' " +
                            "                AND processo_dtafin is null AND processo_usuario = '' AND processo_dtausu is null " +
                            "                AND processo_husu is null AND processo_npai = :processoNpai AND processo_apai = :processoApai " +
                            "                AND processo_origem = 0 AND processo_aorigem = 0 AND processo_julgmt = '' AND processo_nunreg = 0 " +
                            "                AND processo_dtareg is null AND processo_vlrcove = 0 AND processo_numcov = 0 AND processo_anocov = 0 " +
                            "                AND processo_vlrcov = 0 AND processo_dtinccv is null AND processo_dtfincv is null " +
                            "                AND processo_tpconv = '' AND processo_coger = '' AND processo_numaps is null AND processo_anoaps is null " +
                            "                AND processo_cxcoofi = 0 AND processo_obscoof = '' AND processo_relator = 0 AND processo_relato1 = 0 " +
                            "                AND processo_distrib = :relatoria AND processo_relpaut = 0 AND processo_ev_codigo is null " +
                            "                AND processo_ev_classe is null AND processo_ev_entids is null AND processo_ev_interes = '' " +
                            "                AND processo_complemento = :complemento AND processo_assunto_codigo = :assuntocodigo " +
                            "                AND processo_assunto_classe_assunto = :classeassunto AND processo_nconv = 0 AND processo_aconv = 0 " +
                            "                AND processo_codclassea = :classeassunto AND processo_codassunto = :assuntocodigo AND ult_depto = '' " +
                            "                AND ult_resp = '' AND ult_data is null AND ult_hora is null AND processo_fornec = '' " +
                            "                AND proc_num_anexo is null AND proc_ano_anexo is null AND processo_datacad is null " +
                            "                AND processo_sigiloso = 'N' AND processo_alteracao_status_login = '' AND processo_qtd_pag = 0 " +
                            "                AND processo_dta_tran_julg is null AND processo_microfilmado = '' AND julgado = '' AND status_julg = '' " +
                            "                AND data_microfilmagem is null AND data_digitalizacao is null AND numero_microfilme = '' " +
                            "                AND processo_descartado = '' AND processo_serie_cod = 0 AND processo_cpf_resp = '' " +
                            "                AND num_protocolo = :idprotocolo AND id_entidade_origem = :entidadeorigem " +
                            "                AND id_entidade_vinc = :entidadevinculada AND id_assunto = :idassunto AND ano_protocolo = :ano " +
                            "                AND processo_eletronico = 'S') " +
                            "    BEGIN " +
                            "        INSERT INTO SCP..processo (processo_dtaass, processo_numero, processo_ano, pentids_ecodc_ccodg, " +
                            "                                   pentids_ecodc_ccodc, " +
                            "                                   pentids_ecode, processo_modelo, processo_interes, processo_assunto, processo_mrefer, " +
                            "                                   processo_dtaaut, processo_haut, processo_qtdvol, processo_status, " +
                            "                                   processo_dtafin, processo_usuario, processo_dtausu, processo_husu, processo_npai, " +
                            "                                   processo_apai, processo_origem, processo_aorigem, processo_julgmt, processo_nunreg, " +
                            "                                   processo_dtareg, processo_vlrcove, processo_numcov, processo_anocov, processo_vlrcov, " +
                            "                                   processo_dtinccv, processo_dtfincv, processo_tpconv, processo_coger, processo_numaps, " +
                            "                                   processo_anoaps, processo_cxcoofi, processo_obscoof, processo_relator, " +
                            "                                   processo_relato1, " +
                            "                                   processo_distrib, processo_relpaut, processo_ev_codigo, processo_ev_classe, " +
                            "                                   processo_ev_entids, " +
                            "                                   processo_ev_interes, processo_complemento, processo_assunto_codigo, " +
                            "                                   processo_assunto_classe_assunto, " +
                            "                                   processo_nconv, processo_aconv, processo_codclassea, processo_codassunto, ult_depto, " +
                            "                                   ult_resp, ult_data, " +
                            "                                   ult_hora, processo_fornec, proc_num_anexo, proc_ano_anexo, processo_datacad, " +
                            "                                   processo_sigiloso, processo_alteracao_status_login, " +
                            "                                   processo_qtd_pag, processo_dta_tran_julg, processo_microfilmado, julgado, " +
                            "                                   status_julg, data_microfilmagem, " +
                            "                                   data_digitalizacao, numero_microfilme, processo_descartado, processo_serie_cod, " +
                            "                                   processo_cpf_resp, " +
                            "                                   num_protocolo, id_entidade_origem, id_entidade_vinc, id_assunto, ano_protocolo, " +
                            "                                   processo_eletronico) " +
                            "        VALUES (NULL, " +
                            "                :procnumero, " +
                            "                :ano, 0, 0, 0, 0, '', '', 0, " +
                            "                getdate(), getdate(), 1, 'TRAMT', null, '', null, null, " +
                            "                :processoNpai, " +
                            "                :processoApai, 0, 0, '', 0, null, 0, 0, 0, 0, null, null, '', '', null, null, 0, '', 0, 0, " +
                            "                :relatoria, 0, null, null, null, '', " +
                            "                :complemento, " +
                            "                :assuntocodigo, " +
                            "                :classeassunto, 0, 0, " +
                            "                :classeassunto, " +
                            "                :assuntocodigo, '', '', null, null, '', null, null, null, 'N', '', 0, null, '', '', '', null, null, '', " +
                            "                '', 0, '', " +
                            "                :idprotocolo, " +
                            "                :entidadeorigem, " +
                            "                :entidadevinculada, " +
                            "                :idassunto, " +
                            "                :ano, " +
                            "                'S') " +
                            "    END");
            query.setParameter("procnumero", processo.get("procnumero"));
            query.setParameter("ano", processo.get("ano"));
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

            Query query1 = entityManager.createNativeQuery(
                    "SELECT @@IDENTITY ");

            idProcesso = (BigDecimal) query1.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void salvarProcAtosConcessao(String numAto) {
        try {
            Query query = getEntityManager().createNativeQuery(
                    "INSERT INTO SCP..ProcAtosConcessao (IdProc, NumAtoConc, DataInsert, MatrUsr, CodSistema) " +
                            "VALUES (:idProc, :numAto, getdate(), null, 29) ");
            query.setParameter("idProc", idProcesso);
            query.setParameter("numAto", numAto);
            query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void salvarAndamentoProcesso(Map<String, Object> processo) {
        try {
            Query query = getEntityManager().createNativeQuery(
                    "IF NOT EXISTS(SELECT * " +
                            "              FROM SCP..ProcessoAndamento " +
                            "              WHERE [NumProc] = :procnumero AND [AnoProc] = :ano AND [Descricao] = 'AUTUACAO' " +
                            "                AND [MatriculaPessoa] = 0 AND [IdDepto] = 55) " +
                            "    BEGIN " +
                            "        INSERT INTO SCP..ProcessoAndamento([NumProc], [AnoProc], [Descricao], [MatriculaPessoa], [DHinsert], " +
                            "                                           [IdDepto]) " +
                            "        VALUES (:procnumero, :ano, 'AUTUACAO', 0, GETDATE(), 55) " +
                            "    END");
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
                    "IF NOT EXISTS(SELECT * " +
                            "              FROM SCP..PESSOAS_PROCESSO " +
                            "              WHERE NUM_PROC = :procnumero AND ANO_PROC = :ano AND ID_PESSOA = :idpessoa " +
                            "                AND ID_PAPEL = :papel AND ID_CARGO = :idcargo) " +
                            "    BEGIN " +
                            "        INSERT INTO SCP..PESSOAS_PROCESSO (NUM_PROC, ANO_PROC, ID_PESSOA, ID_PAPEL, ID_CARGO) " +
                            "        VALUES (:procnumero, :ano, :idpessoa, :papel, :idcargo) " +
                            "    END");
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
                    "IF NOT EXISTS(SELECT * " +
                            "              FROM SCP..hists " +
                            "              WHERE hcodp_pnumero = :procnumero AND hcodp_pano = :ano AND hists_origem = 'ENTRA' " +
                            "                AND hoent_ecodc_ccodg = 0 AND hoent_ecodc_ccodc = 0 AND hoent_ecode = 0 " +
                            "                AND hdest_ldepto = 'COPRO' AND hdest_llogin = '000003' AND hent_ecodc_ccodg = 0 " +
                            "                AND hent_ecodc_ccodc = 0 AND hent_ecode = 0 AND status = 'U' AND hists_dest_resp = '000003' " +
                            "                AND idDeptoOrigem = 169 AND idDeptoDestino = 55) " +
                            "    BEGIN " +
                            "        INSERT INTO SCP..hists " +
                            "        (hcodp_pnumero, hcodp_pano, hists_data, hists_hora, hists_origem, hoent_ecodc_ccodg, hoent_ecodc_ccodc, " +
                            "         hoent_ecode, hdest_ldepto, hdest_llogin, hent_ecodc_ccodg, hent_ecodc_ccodc, hent_ecode, status, " +
                            "         hists_dest_resp, data_receb, hora_receb, data_env_depto, hora_env_depto, idDeptoOrigem, idDeptoDestino) " +
                            "        VALUES (:procnumero, :ano, GETDATE(), GETDATE(), 'ENTRA', 0, 0, 0, 'COPRO', '000003', 0, 0, 0, 'U', '000003', " +
                            "                GETDATE(), GETDATE(), DATEADD(millisecond, 7, getdate()), DATEADD(millisecond, 7, getdate()), 169, 55) " +
                            "    END");
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
                    "IF NOT EXISTS(SELECT * " +
                            "              FROM SCP..hists " +
                            "              WHERE hcodp_pnumero = :procnumero AND hcodp_pano = :ano AND hists_origem = 'COPRO' " +
                            "                AND hoent_ecodc_ccodg = 0 AND hoent_ecodc_ccodc = 0 AND hoent_ecode = 0 " +
                            "                AND hdest_ldepto = 'COCAP' AND hdest_llogin = '000003' AND hent_ecodc_ccodg = 0 " +
                            "                AND hent_ecodc_ccodc = 0 AND hent_ecode = 0 AND status = 'T' AND hists_dest_resp = '' " +
                            "                AND data_receb IS NULL AND hora_receb IS NULL AND idDeptoOrigem = 169 AND idDeptoDestino = 55) " +
                            "    BEGIN " +
                            "        INSERT INTO SCP..hists " +
                            "        (hcodp_pnumero, hcodp_pano, hists_data, hists_hora, hists_origem, hoent_ecodc_ccodg, hoent_ecodc_ccodc, " +
                            "         hoent_ecode, hdest_ldepto, hdest_llogin, hent_ecodc_ccodg, hent_ecodc_ccodc, hent_ecode, status, " +
                            "         hists_dest_resp, data_receb, hora_receb, idDeptoOrigem, idDeptoDestino) " +
                            "        VALUES (:procnumero, :ano, DATEADD(millisecond, 7, getdate()), DATEADD(millisecond, 7, getdate()), 'COPRO', 0, " +
                            "                0, 0, 'COCAP', '000003', 0, 0, 0, 'T', '', null, null, 169, 55) " +
                            "    END");
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
                    "IF NOT EXISTS(SELECT * " +
                            "              FROM SCP..[document] " +
                            "              WHERE [docmt_tipo] = 'TA' AND [dcnproc_pnumero] = :procnumero AND [docmt_numero] = :procnumero " +
                            "                AND [docmt_ano] = :ano AND [docmt_depto] = 'COPRO' AND [docmt_excluido] = '' " +
                            "                AND [login_usr] = '000003' AND [docmt_is_assinado] = 'S' AND [docmt_depto_doc] = 'COPRO' " +
                            "                AND [sigiloso] = 'N' AND [num_evento] = '1' AND [idDeptoCriador] = 55 AND [idDeptoJuntada] = 55) " +
                            "    BEGIN " +
                            "        INSERT INTO SCP..[document]( [docmt_tipo], [dcnproc_pnumero], [dcnproc_pano], [docmt_numero], [docmt_ano],  " +
                            "                                    [docmt_depto], [docmt_excluido], [docmt_data], [docmt_hora], [login_usr],  " +
                            "                                    [docmt_is_assinado], [docmt_depto_doc], [sigiloso], [num_evento],  " +
                            "                                    [idDeptoCriador], [idDeptoJuntada]) " +
                            "        VALUES ('TA', :procnumero, :ano, :procnumero, :ano, 'COPRO', '', getdate(), getdate(), '000003', 'S', 'COPRO', " +
                            "                'N', '1', 55, 55) " +
                            "    END");
            query.setParameter("procnumero", processo.get("procnumero"));
            query.setParameter("ano", processo.get("ano"));
            query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Integer buscarUltimoIdDocumento(Map<String, Object> processo) {
        try {
            Query query = getEntityManager().createNativeQuery(
                    "SELECT MAX(docmt_id) as id_documento " +
                            "              FROM SCP..[document] " +
                            "              WHERE [docmt_tipo] = 'TA' AND [dcnproc_pnumero] = :procnumero AND [docmt_numero] = :procnumero " +
                            "                AND [docmt_ano] = :ano AND [docmt_depto] = 'COPRO' AND [docmt_excluido] = '' " +
                            "                AND [login_usr] = '000003' AND [docmt_is_assinado] = 'S' AND [docmt_depto_doc] = 'COPRO' " +
                            "                AND [sigiloso] = 'N' AND [num_evento] = '1' AND [idDeptoCriador] = 55 AND [idDeptoJuntada] = 55");
            query.setParameter("procnumero", processo.get("procnumero"));
            query.setParameter("ano", processo.get("ano"));
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
                    "IF NOT EXISTS(SELECT * " +
                            "              FROM SCP..DOCUMENT_ARQUIVOS " +
                            "              WHERE ID_DOCUMENT = :idDocumento AND NOME_ARQ = :arquivo AND DESCRICAO = :arquivo " +
                            "                AND LOGIN_INSERIU = '000003' AND EXCLUIDO = 'N' AND UUID_CAS = :documentoCastor) " +
                            "    BEGIN " +
                            "        INSERT INTO SCP..DOCUMENT_ARQUIVOS (ID_DOCUMENT, NOME_ARQ, DESCRICAO, DATA, LOGIN_INSERIU, EXCLUIDO, UUID_CAS) " +
                            "        VALUES (:idDocumento, :arquivo, :arquivo, GETDATE(), '000003', 'N', :documentoCastor) " +
                            "    END");
            query.setParameter("idDocumento", idDocumento);
            query.setParameter("arquivo", arquivo.get("arquivo"));
            query.setParameter("documentoCastor", arquivo.get("idCastorFile"));
            query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
