package com.example.sicapweb.repository.remessa;

import br.gov.to.tce.model.InfoRemessa;
import br.gov.to.tce.util.JayReflection;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.util.StaticMethods;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class AssinarRemessaRepository extends DefaultRepository<String, String> {
    @PersistenceContext
    private EntityManager entityManager;

    public  enum Tabela {
        InfoRemessa(1,"InfoRemessa","br.gov.to.tce.repository"),
        FolhaPagamento(2,"FolhaPagamento","br.gov.to.tce.repository.ap.folha"),
        Admissao(3,"Admissao","br.gov.to.tce.repository.ap.pessoal"),
        Cargo(4,"Cargo","br.gov.to.tce.repository.ap.relacional"),
        Servidor(5,"Servidor","br.gov.to.tce.repository.ap.pessoal"),
        Ato(6,"Ato","br.gov.to.tce.repository.ap.relacional"),
        Lei(7,"Lei","br.gov.to.tce.repository.ap.relacional"),
        UnidadeAdministrativa(8,"UnidadeAdministrativa","br.gov.to.tce.repository.ap.relacional"),
        Lotacao(9,"Lotacao","br.gov.to.tce.repository.ap.relacional"),
        Desligamento(10,"Desligamento","br.gov.to.tce.repository.ap.pessoal"),
        Licenca(11,"Licenca","br.gov.to.tce.repository.ap.pessoal"),
        Aprovado(12,"EditalAprovado","br.gov.to.tce.repository.ap.concurso"),
        Cessao(13,"Cessao","br.gov.to.tce.repository.ap.pessoal"),
        DemonstrativoPrevidenciario(14,"DemonstrativoPrevidenciario","br.gov.to.tce.repository.ap.folha"),
        DesignacaoFuncao(15,"DesignacaoFuncao","br.gov.to.tce.repository.ap.pessoal"),
        Disponibilidade(16,"Disponibilidade","br.gov.to.tce.repository.ap.pessoal"),
        Edital(17,"Edital","br.gov.to.tce.repository.ap.concurso"),
        FolhaItem(18,"FolhaItem","br.gov.to.tce.repository.ap.relacional"),
        Pensao(19,"Pensao","br.gov.to.tce.repository.ap.pessoal"),
        Pensionista(20,"Pensionista","br.gov.to.tce.repository.ap.pessoal"),
        Readaptacao(21,"Readaptacao","br.gov.to.tce.repository.ap.pessoal"),
        Reconducao(22,"Reconducao","br.gov.to.tce.repository.ap.pessoal"),
        Reintegracao(23,"Reintegracao","br.gov.to.tce.repository.ap.pessoal"),
        Aproveitamento(24,"Aproveitamento","br.gov.to.tce.repository.ap.pessoal"),
        Aposentadoria(25,"Aposentadoria","br.gov.to.tce.repository.ap.pessoal"),
        RecolhimentoPrevidenciario(26,"RecolhimentoPrevidenciario","br.gov.to.tce.repository.ap.folha"),
        EmpresaOrganizadora(27,"EmpresaOrganizadora","br.gov.to.tce.repository.ap.concurso")
        ;
        private final int id;
        private final String label;

        private final String packageRepository;

        Tabela(int id, String label,String packageRepository) {
            this.id = id;
            this.label = label;
            this.packageRepository = packageRepository;
        }

        public int getId() {
            return id;
        }

        public String getLabel() {
            return label;
        }

        public String getpackageRepository() {
            return packageRepository;
        }
    }

    public AssinarRemessaRepository(EntityManager em) {
        super(em);
    }

    public AssinarRemessaRepository() {

    }



    public Object buscarResponsavelAssinatura(Integer tipoCargo, InfoRemessa infoRemessa) {
        try {
            Query query = entityManager.createNativeQuery(
                    "select nome, cpf, max(status) status, max(data) data, idCargo as CodigoCargo " +
                            "from (" +
                            "         select distinct pf.nome, pf.cpf, null status, null data, upc.idCargo" +
                            "         from cadun.dbo.vwUnidadesPessoasCargos upc" +
                            "                  join cadun.dbo.vwpessoa pf on pf.id = upc.idPessoaFisica" +
                            "                  join Cadun.dbo.PessoaJuridica pj on upc.idPessoaJuridica = pj.Codigo" +
                            "         where upc.idCargo in (:tipo)" +
                            "           and (dataInicio <= :date and (datafim is null or datafim >= :date))" +
                            "           and pj.CNPJ = :unidade " +
                            "         union" +
                            "         select distinct pf.nome, pf.cpf, b.DataAssinatura, b.DataAssinatura, upc.CodigoCargo" +
                            "         from cadun.dbo.UnidadePessoaCargo upc" +
                            "                  join cadun.dbo.vwpessoa pf on pf.id = upc.CodigoPessoaFisica" +
                            "                  join AutenticacaoAssinatura..UsuarioAplicacao ua on ua.Usuario = pf.cpf" +
                            "                  join AutenticacaoAssinatura..Assinatura b on ua.Usuario = b.Usuario" +
                            "                  join AutenticacaoAssinatura..InfoAssinatura c on c.Assinatura = b.OID and ua.Aplicacao = c.Aplicacao" +
                            "                  join Cadun.dbo.PessoaJuridica pj on upc.CodigoPessoaJuridica = pj.Codigo " +
                            "                  join SICAPAP21..AdmAssinatura ad on ad.idAssinatura = b.OID " +
                            "                  join SICAPAP21..InfoRemessa i on i.chave = ad.chave" +
                            "         where upc.CodigoCargo in (:tipo)" +
                            "           and (dataInicio <= :date and (datafim is null or datafim >= :date))" +
                            "           and i.idUnidadeGestora = :unidade" +
                            "           and pj.CNPJ = :unidade " +
                            "           and c.Exercicio = :exercicio" +
                            "           and c.Bimestre = :remessa" +
                            "           and ua.Aplicacao = 29" +
                            "     ) as v " +
                            "group by nome, cpf, idCargo;");
            query.setParameter("tipo", tipoCargo);
            query.setParameter("date", new Date());
            query.setParameter("unidade", User.getUser(super.request).getUnidadeGestora().getId());
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
                            " where (select count(DISTINCT a.idCargo) from AdmAssinatura a where a.chave = i.chave) < 3" +
                            " and i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "'", InfoRemessa.class).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public InfoRemessa buscarRemessaFechada() {
        try {
            return (InfoRemessa) entityManager.createNativeQuery(
                    "select * from InfoRemessa i" +
                            " where (select count(DISTINCT a.idCargo) from AdmAssinatura a where a.chave = i.chave) = 3" +
                            " and i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "'", InfoRemessa.class).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean remessaValida(InfoRemessa infoRemessa) {
        Query query = getEntityManager().createNativeQuery("" +
                "select 1 podeAssinar " +
                "from SICAPAP21..InfoRemessa a " +
                "         join SICAPAP21..AdmFilaRecebimento b on a.idFilaRecebimento = b.id " +
                "where b.status = 2 " +
                "  and a.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "'" +
                "  and a.exercicio = " + infoRemessa.getExercicio() +
                "  and a.remessa = " + infoRemessa.getRemessa());
        try {
            query.getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    BigDecimal idArquivo;
    BigDecimal idAssinatura;

    public void insertArquivo() {
        Query query = entityManager.createNativeQuery(
                "INSERT INTO AutenticacaoAssinatura.dbo.Arquivo(MIME, Nome, URLASS, Label, DataEntrada, cpf) " +
                        "VALUES ('application/octet-stream', 'Remessa SICAP AP', 'Remessa SICAP AP', 'Remessa SICAP AP', :data, :cpf)");
        query.setParameter("data", new br.gov.to.tce.util.Date().toStringDateAndHourDatabaseFormat2());
        query.setParameter("cpf", User.getUser(super.request).getCpf());
        query.executeUpdate();

        Query query1 = entityManager.createNativeQuery(
                "SELECT @@IDENTITY ");

        idArquivo = (BigDecimal) query1.getSingleResult();
    }

    public void insertAssinatura() {
        Query query = entityManager.createNativeQuery(
                "INSERT INTO AutenticacaoAssinatura.dbo.Assinatura(Usuario, Arquivo, DataAssinatura) " +
                        "VALUES (:cpf, :arquivo, :data)");
        query.setParameter("cpf", User.getUser(super.request).getCpf());
        query.setParameter("arquivo", idArquivo);
        query.setParameter("data", new br.gov.to.tce.util.Date().toStringDateAndHourDatabaseFormat2());
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
        query.setParameter("unidade", User.getUser(super.request).getUnidadeGestora().getId());
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
        query.setParameter("cargo", User.getUser(super.request).getCargo().getValor());
        query.executeUpdate();
        entityManager.flush();
    }

    public void insertUsuarioAplicacao(InfoRemessa infoRemessa) {
        Query query = entityManager.createNativeQuery(
                "INSERT INTO AutenticacaoAssinatura.dbo.UsuarioAplicacao(Usuario, Aplicacao, UnidadeGestora, Exercicio, Remessa, Cargo) " +
                        "VALUES (:usuario, 29, :unidade, :exercicio, :remessa, :cargo)");
        query.setParameter("usuario", User.getUser(super.request).getCpf());
        query.setParameter("unidade", User.getUser(super.request).getUnidadeGestora().getId());
        query.setParameter("exercicio", infoRemessa.getExercicio());
        query.setParameter("remessa", infoRemessa.getRemessa());
        query.setParameter("cargo", User.getUser(super.request).getCargo().getValor());
        query.executeUpdate();
        entityManager.flush();
    }


    public String getSearchDecoded(String searchParams) throws UnsupportedEncodingException {
        String search = "";
        if (searchParams.length() > 11) {
            search =" where ";
            String parametroBusca[] = searchParams.split("&");
            search = search+"  " + parametroBusca[0].split("=")[1] + "  = '" + URLDecoder.decode(parametroBusca[1].split("=")[1], StandardCharsets.UTF_8.toString())     + "'  ";
        }
        return search;
    }

    public    PaginacaoUtil<HashMap<String,Object>> getExtratoDadosRemessa(Pageable paginacaoFrontEnd  , Integer remessa, Integer exercicio, Integer idTabelaRemessa, String serachEncoded , boolean paraExportarExcell)   {
        Query queryTabelaRemessa;
        Query queryTabelaQuantidade;
        String filtroWhere;
        try{
            filtroWhere = getSearchDecoded(serachEncoded);
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
            throw  new RuntimeException("valor incorreto de coluna na busca !!");
        }

        String CaminhoCompletoRepository = Arrays.stream(Tabela.values()).filter(tabelaRepository -> tabelaRepository.getId()==idTabelaRemessa).map(tabelaRepository -> tabelaRepository.getpackageRepository()+'.'+tabelaRepository.getLabel()+"Repository").findFirst().get();
        if (CaminhoCompletoRepository == null)
            throw  new RuntimeException("id de tabela não encontrado ");
        try{
            queryTabelaRemessa= getEntityManager().createNativeQuery((String) JayReflection.executeStaticMethod(CaminhoCompletoRepository,"getQueryExtratoRemessa")+filtroWhere);
            queryTabelaQuantidade = getEntityManager().createNativeQuery((String) JayReflection.executeStaticMethod(CaminhoCompletoRepository,"getQueryCountExtratoRemessa"));
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("não encontrou consulta para buscar dados em tabela !!");
        }

        try {

            queryTabelaRemessa.setParameter("remessa",remessa).setParameter("exercicio",exercicio).setParameter("ug",User.getUser(super.getRequest()).getUnidadeGestora().getId());

            long totalRegistros = (Integer) queryTabelaQuantidade
                    .setParameter("remessa",remessa)
                    .setParameter("exercicio",exercicio)
                    .setParameter("ug",User.getUser(super.getRequest()).getUnidadeGestora().getId()).getSingleResult();
            int pagina = Integer.valueOf(paginacaoFrontEnd.getPageNumber());
            int tamanhoPorPagina =  (filtroWhere.isEmpty())? Integer.valueOf(paginacaoFrontEnd.getPageSize()) : (int) totalRegistros ;

            //libera a limitação de paginas para busca sem filtro where ou total registros inferior a quantidade de paginas
            if (totalRegistros > tamanhoPorPagina  && filtroWhere.isEmpty() && !paraExportarExcell ){
                queryTabelaRemessa.setFirstResult(pagina).setMaxResults(tamanhoPorPagina);
            }
            List<HashMap<String,Object>> ExtratoTabelaRemessa = StaticMethods.getHashmapFromQuery( queryTabelaRemessa );
            long totalPaginas = (totalRegistros + (tamanhoPorPagina - 1)) / tamanhoPorPagina;
            return new PaginacaoUtil<>(tamanhoPorPagina, pagina, totalPaginas, totalRegistros, ExtratoTabelaRemessa);
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Problema ao rodar consulta pra tabela escolhida");
        }
    }

    public List<HashMap<String,Object>> getResumoGeralRemessa(Integer remessa,Integer exercicio,List<Integer> excluirListagem ){

        List<HashMap<String,Object>> resumoGeralRemessa = Arrays.stream(Tabela.values()).filter(item -> !excluirListagem.contains(item.getId()) ).map( itemTabela -> {

            try {
                HashMap<String, Object> item = new LinkedHashMap<>();
                item.put("idtabela",itemTabela.getId());
                item.put("tabela",itemTabela.getLabel());
                Query queryTabelaQuantidade = getEntityManager().createNativeQuery((String) JayReflection.executeStaticMethod(itemTabela.getpackageRepository()+'.'+itemTabela.getLabel()+"Repository","getQueryCountExtratoRemessa"));
                queryTabelaQuantidade.setParameter("remessa",remessa)
                        .setParameter("exercicio",exercicio)
                        .setParameter("ug",User.getUser(super.getRequest()).getUnidadeGestora().getId());
                item.put("quantidade",(Integer) queryTabelaQuantidade.getSingleResult());
                return item;
            } catch (Exception e) {
                return null;
            }
        }).collect(Collectors.toList());

        return resumoGeralRemessa;
    }


}
