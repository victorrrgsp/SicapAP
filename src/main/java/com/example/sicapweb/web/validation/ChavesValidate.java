package com.example.sicapweb.web.validation;

import br.gov.to.tce.model.adm.AdmAutenticacao;
import com.example.sicapweb.exception.ChaveValidationException;
import com.example.sicapweb.repository.AdmAutenticacaoRepository;
import com.example.sicapweb.repository.geral.UnidadeGestoraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChavesValidate {

    @Autowired
    AdmAutenticacaoRepository autenticacaoRepository;

    @Autowired
    UnidadeGestoraRepository ugRepository;

    public void criar(AdmAutenticacao autenticacao){

        // PERMITIR CRIAR APENAS SE HOVER VIGÊNCIA ATIVA
        if(!existeVigenciaUnidade(autenticacao)){
            throw new ChaveValidationException("Essa unidade não possui um período de vigência ativa!");
        }

        // JÁ EXISTE UMA CHAVE PARA A REMESSA ATUAL?
        if(existeChave(autenticacao)){
            throw new ChaveValidationException("Já existe um chave cadastrada para essa remessa!");
        }

        // REMESSA ANTERIOR NÃO ASSINADA?
        if (!remessaAnteriorAssinada(autenticacao)){
            throw new ChaveValidationException("As assinaturas da remessa anterior não concluídas!");
        }
    }

    private boolean remessaAnteriorAssinada(AdmAutenticacao autenticacao){
        // EXERCICIO E REMESSA ANTERIOR
        int exercicio = autenticacao.getExercicio();
        int remessa = autenticacao.getRemessa();

        if (remessa == 1) {
            remessa = 12;
            exercicio -= 1;
        } else {
            remessa = remessa - 1;
        }

        Integer qntAssinaturas = autenticacaoRepository.getQtdAssinaturas(
                autenticacao.getUnidadeGestora().getId(),
                exercicio,
                remessa
        );

        return qntAssinaturas != null && qntAssinaturas > 2;
    }

    private boolean existeChave(AdmAutenticacao autenticacao){
        return autenticacaoRepository.getStatusChave(
                autenticacao.getUnidadeGestora().getId(),
                autenticacao.getExercicio(),
                autenticacao.getRemessa()
        );
    }

    private boolean existeVigenciaUnidade(AdmAutenticacao autenticacao){
        List<Integer> resposta = ugRepository.buscaVigenciaUnidadeGestoraPorCnpj(
                autenticacao.getUnidadeGestora().getId(),
                autenticacao.getExercicio(),
                autenticacao.getRemessa()
        );

        return resposta != null && !resposta.isEmpty() && resposta.get(0) == 1;
    }
}
