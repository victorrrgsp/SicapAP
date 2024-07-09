package com.example.sicapweb.model.dto;

import br.gov.to.tce.util.Date;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
public class ProcessoCanceladoDTO {

      private final BigDecimal id;
      private final String processo;
      private final String anoProcesso;
      private final String idAdmissao;
      @NotBlank(message = "O Motivo não pode ser nulo ou vazio")
      @Size(min = 4, max = 60, message = "O tamanho do texto deve ser entre 4 e 60 caracteres.")
      private final String motivo;




    public ProcessoCanceladoDTO(BigDecimal id, String processo, String anoProcesso, String motivo,  String idAdmissao) {
        this.id = id;
        this.processo = processo;
        this.anoProcesso = anoProcesso;
        this.motivo = motivo;
        this.idAdmissao = idAdmissao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessoCanceladoDTO that = (ProcessoCanceladoDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(processo, that.processo) && Objects.equals(anoProcesso, that.anoProcesso) && Objects.equals(motivo, that.motivo)&& Objects.equals(idAdmissao, that.idAdmissao) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, processo, anoProcesso, motivo,idAdmissao);
    }

    @Override
    public String toString() {
        return "ProcessoCanceladoDTO{" +
                "id=" + id +
                ", processo='" + processo + '\'' +
                ", anoProcesso='" + anoProcesso + '\'' +
                ", motivo='" + motivo + '\'' +
                ", idAdmissao='" + idAdmissao + '\'' +
                '}';
    }
}
