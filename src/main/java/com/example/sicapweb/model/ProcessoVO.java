package com.example.sicapweb.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProcessoVO {
    private  String unidadeGestora ;
    private Integer exercicio ;
    private String dataEnvio;
    private String dataProcesamento;
    private Integer remessa;
    private  String Status ;

    public String getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(String unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public String getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(String dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public String getDataProcesamento() {
        return dataProcesamento;
    }

    public void setDataProcesamento(String dataProcesamento) {
        this.dataProcesamento = dataProcesamento;
    }

    public Integer getRemessa() {
        return remessa;
    }

    public void setRemessa(Integer remessa) {
        this.remessa = remessa;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    private static ProcessoVO convert(Object[] registro){
        ProcessoVO retorno = new ProcessoVO();
        retorno.setUnidadeGestora((String)registro[0]);
        retorno.setExercicio((Integer)registro[1]);
        retorno.setDataEnvio((String) registro[2]);
        retorno.setDataProcesamento((String) registro[3]);
        retorno.setRemessa((Integer)registro[4]);
        retorno.setStatus((String)registro[5]);
        return retorno;
    }
    public static List<ProcessoVO> convertList(List<Object[]> registros) {
        List<ProcessoVO> retorno = new ArrayList<>();
        registros.forEach(r ->{
            ProcessoVO aux = convert(r);
            retorno.add(aux);
        });
        return retorno;



    }
}
