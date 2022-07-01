package com.example.sicapweb.security;

import br.gov.to.tce.model.DefaultEnum;
import br.gov.to.tce.model.UnidadeGestora;
import br.gov.to.tce.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.context.annotation.SessionScope;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SessionScope
public class User implements Serializable {
    private static final long serialVersionUID = -6455849887168288989L;

    public String userName = "01277824193";
    public String id = "";
    public String certificado = "teste";

    public String hashCertificado = "teste";

    public String nome = "CELSO SOARES REGO MORAIS";
    public String cpf = "01277824193";
    public Cargo cargo  = Cargo.Gestor;
    public Date dateStart = new Date() ;
    public Date dateEnd = new Date();
    public Integer sistema = null;
    public List<String> systems = new ArrayList<>();
    @JsonIgnore
    public List<UnidadeGestora> unidadeGestoraList = new ArrayList<>();
    public UnidadeGestora unidadeGestora = new UnidadeGestora("00299180000154", "PREFEITURA MUNICIPAL DE PARAÍSO DO TOCANTINS", 1);

    public static User getUser(String user) {
        return Config.fromJson(new Config().jedis.get(user.replace("=", "")), User.class);
    }

    public static User getUser(HttpServletRequest request) {
        return getUser(request.getHeader("user"));
    }

    public String getId() {
        return id;
    }

    public String getHashCertificado() {
        return hashCertificado;
    }

    public void setHashCertificado(String hashCertificado) {
        this.hashCertificado = hashCertificado;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getSistema() {
        return sistema;
    }

    public void setSistema(Integer sistema) {
        this.sistema = sistema;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }
    public void setCargoByInteger(Integer id) {
        Optional<Cargo> val = Arrays.stream(Cargo.values()).filter(o->o.valor == id).findFirst();
        cargo = val.orElse(null);
    }

    public List<UnidadeGestora> getUnidadeGestoraList() {
        return unidadeGestoraList;
    }

    public void setUnidadeGestoraList(List<UnidadeGestora> unidadeGestoraList) {
        this.unidadeGestoraList = unidadeGestoraList;
    }

    public String getCertificado() {
        return certificado;
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

    public boolean isValid(){
        try {
            this.dateEnd = new Date(this.getDateEnd().toStringDateAndHourDatabaseFormat2());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date dataAtual = new Date();
        return this.dateEnd.isBiggerThan(dataAtual);
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

    public void setUnidadeGestoraList(UnidadeGestora unidadeGestora) {
        this.unidadeGestoraList.add(unidadeGestora);
    }

    public enum Cargo implements DefaultEnum<Cargo>, Serializable {
        Gestor(4, "Gestor"),
        Contador(5, "Contador"),
        ControleInterno(3, "Controle Interno"),
        ResponsavelRH(32, "Responsável R.H."),
        LancamentoAP(362, "Lançamento AP");

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
