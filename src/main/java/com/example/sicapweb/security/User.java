package com.example.sicapweb.security;

import br.gov.to.tce.model.DefaultEnum;
import br.gov.to.tce.model.UnidadeGestora;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class User {
    public String userName = "01277824193";
    public String certificado = "teste";
    public String nome = "CELSO SOARES REGO MORAIS";
    public String cpf = "01277824193";
    public Cargo cargo  = Cargo.Gestor; ///
    public Date dateStart = new Date(Calendar.getInstance().getTime().getTime());
    public Date dateEnd = new Date(Calendar.getInstance().getTime().getTime());
    public List<String> systems = new ArrayList<>();
    public List<UnidadeGestora> unidadeGestoraList = new ArrayList<>();
    public UnidadeGestora unidadeGestora = new UnidadeGestora("00299180000154", "PREFEITURA MUNICIPAL DE PARAÍSO DO TOCANTINS", 1);

    public String getCertificado() {
        return certificado;
    }

    public static User getUser(){
        try {
            ServletRequestAttributes request = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            if (request == null) return new User();

            User user = (User) request.getRequest().getSession().getAttribute("user");
            return user != null ? user : new User();
        }
        catch (Exception e){
            return new User();
        }
    }

    public void setCertificado(String certificado) {
        this.certificado = certificado;
    }

    public List<String> getSystems() {
        return systems;
    }

    public void setSystems(List<String> systems) {
        this.systems = systems;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public enum Cargo implements DefaultEnum<Cargo>, Serializable {
        Gestor(4, "Gestor"),
        Contador(5, "Contador"),
        ControleInterno(3, "Controle Interno"),
        ResponsavelRH(32, "Responsável R.H.");

        private final String label;
        private final int valor;

        Cargo(Integer valorOpcao, String labelOpcao) {
            valor = valorOpcao;
            label = labelOpcao;
        }

        public Integer getValor() {
            return valor;
        }

        public String getLabel() {
            return label;
        }

        public String toString() {
            return Integer.toString(this.valor);
        }
    }

}
