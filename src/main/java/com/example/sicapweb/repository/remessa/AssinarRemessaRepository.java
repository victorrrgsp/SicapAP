package com.example.sicapweb.repository.remessa;

import br.gov.to.tce.model.InfoRemessa;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;

@Repository
public class AssinarRemessaRepository extends DefaultRepository<String, String> {
    @PersistenceContext
    private EntityManager entityManager;

    public AssinarRemessaRepository(EntityManager em) {
        super(em);
    }

    public AssinarRemessaRepository() {

    }

    public Object buscarResponsavelAssinatura(Integer tipoCargo, InfoRemessa infoRemessa) {
        try {
            Query query = entityManager.createNativeQuery(
                    "select nome, cpf, max(status) status, max(data) data, CodigoCargo " +
                            "from (" +
                            "         select distinct pf.nome, pf.cpf, null status, null data, upc.CodigoCargo" +
                            "         from cadun.dbo.UnidadePessoaCargo upc" +
                            "                  join cadun.dbo.vwpessoa pf on pf.id = upc.CodigoPessoaFisica" +
                            "                  join AutenticacaoAssinatura..UsuarioAplicacao ua on ua.Usuario = pf.cpf" +
                            "         join Cadun.dbo.PessoaJuridica pj on upc.CodigoPessoaJuridica = pj.Codigo" +
                            "         where upc.CodigoCargo in (:tipo)" +
                            "           and (dataInicio <= :date and (datafim is null or datafim >= :date))" +
                            "           and pj.CNPJ = :unidade and ua.Aplicacao = 29" +
                            "         union" +
                            "         select distinct pf.nome, pf.cpf, b.DataAssinatura, b.DataAssinatura, upc.CodigoCargo" +
                            "         from cadun.dbo.UnidadePessoaCargo upc" +
                            "                  join cadun.dbo.vwpessoa pf on pf.id = upc.CodigoPessoaFisica" +
                            "                  join AutenticacaoAssinatura..UsuarioAplicacao ua on ua.Usuario = pf.cpf" +
                            "                  join AutenticacaoAssinatura..Assinatura b on ua.Usuario = b.Usuario" +
                            "                  join AutenticacaoAssinatura..InfoAssinatura c on c.Assinatura = b.OID and ua.Aplicacao = c.Aplicacao" +
                            "         join Cadun.dbo.PessoaJuridica pj on upc.CodigoPessoaJuridica = pj.Codigo" +
                            "         where upc.CodigoCargo in (:tipo)" +
                            "           and (dataInicio <= :date and (datafim is null or datafim >= :date))" +
                            "           and pj.CNPJ = :unidade" +
                            "           and c.Exercicio = :exercicio" +
                            "           and c.Bimestre = :remessa" +
                            "           and ua.Aplicacao = 29" +
                            "     ) as v " +
                            "group by nome, cpf, CodigoCargo;");
            query.setParameter("tipo", tipoCargo);
            query.setParameter("date", new Date());
            query.setParameter("unidade", User.getUser().getUnidadeGestora().getId());
            query.setParameter("exercicio", infoRemessa.getExercicio());
            query.setParameter("remessa", infoRemessa.getRemessa());
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public InfoRemessa buscarRemessaAberta() {
        try {
            return (InfoRemessa) entityManager.createNativeQuery(
                    "select * from InfoRemessa i" +
                            " where (select count(*) from AdmAssinatura a where a.chave = i.chave) < 3" +
                            " and i.idUnidadeGestora = '" + User.getUser().getUnidadeGestora().getId() + "'", InfoRemessa.class).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    BigDecimal idArquivo;
    BigDecimal idAssinatura;

    public void insertArquivo() {
        Query query = entityManager.createNativeQuery(
                "INSERT INTO AutenticacaoAssinatura.dbo.Arquivo(MIME, Nome, URLASS, Label, DataEntrada, cpf) " +
                        "VALUES ('application/octet-stream', 'Remessa SICAP AP', 'Remessa SICAP AP', 'Remessa SICAP AP', :data, :cpf)");
        query.setParameter("data", new br.gov.to.tce.util.Date().toStringDateAndHourDatabaseFormat());
        query.setParameter("cpf", User.getUser().getCpf());
        query.executeUpdate();

        Query query1 = entityManager.createNativeQuery(
                "SELECT @@IDENTITY ");

        idArquivo = (BigDecimal) query1.getSingleResult();
    }

    public void insertAssinatura() {
        Query query = entityManager.createNativeQuery(
                "INSERT INTO AutenticacaoAssinatura.dbo.Assinatura(Usuario, Arquivo, DataAssinatura) " +
                        "VALUES (:cpf, :arquivo, :data)");
        query.setParameter("cpf", User.getUser().getCpf());
        query.setParameter("arquivo", idArquivo);
        query.setParameter("data", new br.gov.to.tce.util.Date().toStringDateAndHourDatabaseFormat());
        query.executeUpdate();

        Query query1 = entityManager.createNativeQuery(
                "SELECT @@IDENTITY ");

        idAssinatura = (BigDecimal) query1.getSingleResult();
        entityManager.flush();
    }

    public void insertInfoAssinatura(InfoRemessa infoRemessa) {
        Query query = entityManager.createNativeQuery(
                "INSERT INTO AutenticacaoAssinatura.dbo.InfoAssinatura(CodUndGestora, Bimestre, Exercicio, Assinatura, Aplicacao) " +
                        "VALUES (:unidade, :remessa, :exercicio, :assinatura, 29)");
        query.setParameter("unidade", User.getUser().getUnidadeGestora().getId());
        query.setParameter("remessa", infoRemessa.getRemessa());
        query.setParameter("exercicio", infoRemessa.getExercicio());
        query.setParameter("assinatura", idAssinatura);
        query.executeUpdate();
        entityManager.flush();
    }

    public void insertAdmAssinatura(String chave) {
        Query query = entityManager.createNativeQuery(
                "INSERT INTO AdmAssinatura(chave, idAssinatura, idCargo) " +
                        "VALUES (:chave, :assinatura, :cargo)");
        query.setParameter("chave", chave);
        query.setParameter("assinatura", idAssinatura);
        query.setParameter("cargo", User.getUser().getCargo().getValor());
        query.executeUpdate();
    }
}
