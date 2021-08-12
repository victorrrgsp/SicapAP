package com.example.sicapweb.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import br.gov.to.tce.model.adm.AdmAutenticacao;

public interface ChavesRepository extends JpaRepository<AdmAutenticacao, Long> {

    AdmAutenticacao findById(long id);

}
