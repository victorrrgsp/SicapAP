package com.example.sicapweb.repository.registro;

import br.gov.to.tce.model.ap.registro.Registro;
import br.gov.to.tce.model.ap.registro.RegistroAdmissao;

import com.example.sicapweb.exception.MovimentacaoNotFaud;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.util.StaticMethods;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Objects;

@Repository
public class RegistroRepository  extends DefaultRepository<Registro, BigInteger> {

    public RegistroRepository(EntityManager em) {
        super(em);
    }

    public String getSearch(HashMap<String,String> filtros) {
        String search = "";
        
        String nome= filtros.get("nome");
        String cpf= filtros.get("cpf");
        String numeroProcesso= filtros.get("numeroProcesso");
        
        if (numeroProcesso !=null && !numeroProcesso.isEmpty()) {
            search=" where a.processo = '"+numeroProcesso.trim()+"'";
        } else {
            throw new MovimentacaoNotFaud("Número do processo não informado");
        }

        if (nome !=null && !nome.isEmpty()){
            search+=" and s.nome like '%"+nome.trim()+"%'";
        } else if (cpf !=null && !cpf.isEmpty()) {
            search+=" and s.cpfServidor  = '"+cpf.trim()+"'";
        }

        return search;
    }
    

    public PaginacaoUtil<HashMap<String, Object>> getMovimentosParaRegistrar(
                                                                                Pageable pageable, 
                                                                                HashMap<String,String> filtros, 
                                                                                Registro.Tipo tipoMovimentacao
                                                                            ){

        String whereStatemente = getSearch(filtros);
    
        String queryTemplate = 
            "with envios as (" +
            "    select cast(substring(processo, 1, len(processo) - 5) as int) numeroProcesso, " +
            "           cast(substring(processo, len(processo) - 3, len(processo)) as int) anoProcesso, " +
            "           processo," +
            "           a.status, "+
            "           ${idMovimentacao} idMovimentacao " +
            "    from ${tableEnvio}Envio a " +
            "           left join DocumentoAdmissao b on a.id = b.idEnvio and b.status > 0 " +
            "    where a.status = :status" +
            "), " +
            "movimentosEnvios as (" +
            "    select distinct b.numeroProcesso, b.anoProcesso, b.processo ,a.*, pen.cpfPensionista " +
            "    from ${tableName} a " +
            "    join envios b on a.id = b.idMovimentacao " +
            "    left join pensao pe on pe.id = a.id" +
            "    left join Pensionista pen on pen.cpfServidor = pe.cpfServidor" +
            "), " +
            "mov as (" +
            "    select i.idUnidadeGestora, a.* " +
            "    from movimentosEnvios a " +
            "    join InfoRemessa i on a.chave = i.chave " +
            "    and i.idUnidadeGestora = :ug " +
            "    where not exists(select 1 from Registro${tableName} where id${tableName} = a.id)" +
            ") " +
            "select a.id as idMovimentacao, " +
            "       CAST( REPLACE(s.nome, '''', '') AS VARCHAR(255)) AS nome," +
            "       s.cpfServidor as cpfServidor, " +
            "       ${tipoMovimentacao} as tipoMovimentacao, " +
            "       a.${dataMovimentacao} as dataMovimentacao, " +
            "       a.numeroProcesso as numeroProcesso, " +
            "       a.anoProcesso as anoProcesso, " +
            "       c.nomeCargo as nomeCargo, " +
            "       c.codigoCargo as codigoCargo, " +
            "       c.id as idcargo, " +
            "       o.numeroAto as numeroAto, " +
            "       o.tipoAto as tipoAto, " +
            "       a.idUnidadeGestora as idUnidadeGestora, " +
            "       o.dataPublicacao as dataAto " +
            "from mov a " +
            "join Ato o on a.idAto = o.id " +
            "join Admissao ad on a.id = ad.id " +
            "join Cargo c on ad.idCargo = c.id " +
            "join Servidor s on ad.idServidor = s.id " + 
            whereStatemente;
        String tableName = tipoMovimentacao.isTipoAposentadoria() ? "Aposentadoria" : tipoMovimentacao.getLabel();
        String dataMovimentaca;
        String tipoMovimentacaoQueryCampo;
        if (tipoMovimentacao.isTipoAposentadoria()) {
            dataMovimentaca = "dataAposentadoria";
        } else if (tipoMovimentacao.equals(Registro.Tipo.Efetivos)) {
            dataMovimentaca = "dataInicio";
        } else {
            dataMovimentaca = "dataObito";
        }
        if (tipoMovimentacao.equals(Registro.Tipo.Reversao) || !tipoMovimentacao.isTipoAposentadoria()) {
            tipoMovimentacaoQueryCampo = "'"+tipoMovimentacao.getLabel().toUpperCase()+"'";
        } else {
            tipoMovimentacaoQueryCampo = "a.tipoAposentadoria";
            
        }
        String queryStr = "";

        queryStr = queryTemplate
                        .replace("${idMovimentacao}", tipoMovimentacao.equals(Registro.Tipo.Efetivos) ? "idAdmissao as " : "")
                        .replace("${tableEnvio}", tipoMovimentacao.equals(Registro.Tipo.Efetivos) ? "admissao" : "adm")
                        .replace("${tipoMovimentacao}", tipoMovimentacaoQueryCampo)
                        .replace("${tableName}", tableName)
                        .replace("${dataMovimentacao}", dataMovimentaca);
        Query queryMovimentos = getEntityManager()
                                            .createNativeQuery(queryStr)
                                            .setParameter("ug", filtros.get("ug"))
                                            .setParameter("status", tipoMovimentacao.getStatusEnvio());
        var movimentosTotatais = StaticMethods.getHashmapFromQuery(queryMovimentos);
        long totalRegistros = movimentosTotatais.size();
        int pagina = pageable.getPageNumber();
        int tamanho = pageable.getPageSize();
        int tamanhoPorPagina ;
        if (whereStatemente.isEmpty() && totalRegistros > tamanho) {
            queryMovimentos.setFirstResult(pagina).setMaxResults(tamanho);
            tamanhoPorPagina = tamanho ;
        }else{
            tamanhoPorPagina = (int) totalRegistros;
        }
        long totalPaginas = (totalRegistros + (tamanhoPorPagina - 1)) / tamanhoPorPagina;

        try {
            return new PaginacaoUtil<>(tamanhoPorPagina, pagina, totalPaginas, totalRegistros,StaticMethods.getHashmapFromQuery(queryMovimentos));
        } catch (RuntimeException e) {
            throw new RuntimeException("Problema ao consultar os movimentos para registro!!");
        }
    }
}
