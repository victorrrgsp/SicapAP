package com.example.sicapweb.web.validation;


import br.gov.to.tce.model.adm.AdmEnvio;
import com.example.sicapweb.exception.SicapApValidationException;
import com.example.sicapweb.repository.concessao.AdmEnvioRepository;
import com.example.sicapweb.repository.registro.ProcessoCanceladoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProcessoCanceladoValidate {

    @Autowired
    ProcessoCanceladoRepository processoCanceladoRepository;

    @Autowired
    AdmEnvioRepository admEnvioRepository;


  public AdmEnvio buscaProcesso(String processo, String anoProcesso){

      // Verifica se o processo esta disponivel para cancelamento
      if(!existeProcessoAdmEnvio(processo,anoProcesso)){
          throw  new SicapApValidationException("processo", "O Processo '"+processo+"/"+anoProcesso+"' não disponível para Cancelamento!");
      }

      AdmEnvio resposta = admEnvioRepository.getProcessoAdmEnvio(processo,anoProcesso);
      return resposta;

    }

    private  boolean existeProcessoAdmEnvio(String processo,String anoProcesso){

         AdmEnvio resposta = admEnvioRepository.getProcessoAdmEnvio(processo,anoProcesso);
         return resposta != null;
    }



}
