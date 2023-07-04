package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.ap.concurso.EditalAprovado;
import com.example.sicapweb.model.EditalAprovadoConcurso;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.*;

@Repository
public class EditalAprovadoRepository extends DefaultRepository<EditalAprovado, BigInteger> {
    public EditalAprovadoRepository(EntityManager em) {
        super(em);
    }


    public String getSearch(String searchParams, Integer tipoParams) {
        String search = "";
        if (searchParams.length() > 3) {
            List<String> lparam = new ArrayList(Arrays.asList(searchParams.split("&")));
            for (String param : lparam) {
                if (tipoParams == 0) { //entra para tratar a string
                    String arrayOfStrings[] = param.split("=");
                    if (arrayOfStrings[0].equals("cpf")) {
                        search = search + " AND     a." + arrayOfStrings[0] + " = '" + arrayOfStrings[1] + "'  ";
                    } else if (arrayOfStrings[0].equals("numeroEdital")) {
                        search = search + " AND   exists ( " +
                                " select 1 " +
                                " from EditalVaga ev " +
                                "          join Edital e on " +
                                "              ev.id = a.idEditalVaga and " +
                                "              ev.idEdital = e.id and e.numeroEdital = '"+arrayOfStrings[1]+"' ) ";
                    }

                } else {
                    search = " AND " + searchParams + "   ";
                }
            }
        }
        return search;
    }

    public PaginacaoUtil<EditalAprovado> buscaPaginada(Pageable pageable, String searchParams, Integer tipoParams) {

        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        var query = getEntityManager()
                .createNativeQuery("with edt as ( " +
                        "        select a.cpf,a.numeroInscricao, i.idUnidadeGestora ,max(a.id)  max_id " +
                        "             from EditalAprovado a  join infoRemessa i on a.chave = i.chave  and i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "'  group by " +
                        "                a.cpf,a.numeroInscricao , i.idUnidadeGestora " +
                        "                         ) " +
                        "select   a.* from EditalAprovado a  join infoRemessa i on a.chave = i.chave join edt b on a.id= b.max_id and i.idUnidadeGestora=b.idUnidadeGestora " +
                        " where 1=1 " + search + " ORDER BY " + campo, EditalAprovado.class);
        List<EditalAprovado> listAprovados = query.setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();
        long totalRegistros = quantidadeAprovados(search);
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        return new PaginacaoUtil<>(tamanho, pagina, totalPaginas, totalRegistros, listAprovados);
    }

    public Integer quantidadeAprovados(String search) {
        return (Integer) getEntityManager()
                .createNativeQuery("with edt as ( " +
                        "        select a.cpf,a.numeroInscricao, i.idUnidadeGestora ,max(a.id)  max_id " +
                        "             from EditalAprovado a  join infoRemessa i on a.chave = i.chave  and i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "'  group by " +
                        "                a.cpf,a.numeroInscricao , i.idUnidadeGestora " +
                        "                         ) " +
                        "select   count(1) " +
                        "from EditalAprovado a " +
                        "join infoRemessa i on a.chave = i.chave join edt b on a.id= b.max_id and " +
                        "i.idUnidadeGestora=b.idUnidadeGestora where 1=1 " + search).getSingleResult();
    }

    public PaginacaoUtil<EditalAprovadoConcurso> buscaPaginadaAprovadosDto(Pageable pageable, String searchParams, Integer tipoParams) {

        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String campo = String.valueOf(pageable.getSort()).replace(":", "");
        String search = getSearch(searchParams, tipoParams);

        var query  = getEntityManager()
                .createNativeQuery(
                        "with  Admissao1 as " +
                        "(select a.* from Admissao a join InfoRemessa i on a.chave = i.chave and i.idUnidadeGestora=:ug and a.tipoAdmissao=1 ), " +
                        "  Servidor1 as " +
                        "(select a.* from Servidor a join InfoRemessa i on a.chave = i.chave and i.idUnidadeGestora=:ug )," +
                        "      Aprovado as " +
                        "(select a.* from EditalAprovado a join InfoRemessa i on a.chave = i.chave and i.idUnidadeGestora=:ug )" +
                        " " +
                        "select distinct a.* from Aprovado  a join EditalVaga b on a.idEditalVaga = b.id where (select count(1) from  Admissao1 ad join Servidor1 s  on  ad.idServidor = s.id and s.cpfServidor = a.cpf )=0  " + 
                        search + 
                        " ORDER BY " + campo, EditalAprovado.class);
        List<EditalAprovado> listAprovados =  query.setParameter("ug", User.getUser(super.request).getUnidadeGestora().getId())
                                                        .setFirstResult(pagina)
                                                        .setMaxResults(tamanho)
                                                        .getResultList();

        long totalRegistros = countAprovadosSemAdmissao(search);
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        List<EditalAprovadoConcurso> listaAprovadosDto = new ArrayList<EditalAprovadoConcurso>();
        for (Integer i = 0; i < listAprovados.size(); i++) {
            EditalAprovadoConcurso editalAprovadoConcurso = new EditalAprovadoConcurso();
            editalAprovadoConcurso.setNome(listAprovados.get(i).getNome());
            editalAprovadoConcurso.setNumeroEdital(listAprovados.get(i).getNumeroEdital());
            editalAprovadoConcurso.setClassificacao(listAprovados.get(i).getClassificacao());
            editalAprovadoConcurso.setCodigoVaga(listAprovados.get(i).getCodigoVaga());
            editalAprovadoConcurso.setCpf(listAprovados.get(i).getCpf());
            editalAprovadoConcurso.setEditalAprovado(listAprovados.get(i));
            String situacao = (String) getEntityManager().createNativeQuery(
                    "SELECT\n" +
                            "    CASE\n" +
                            "        WHEN COUNT(CASE\n" +
                            "                    WHEN da.opcaoDesistencia = 3 THEN null\n" +
                            "                    WHEN da.opcaoDesistencia = 6 THEN null\n" +
                            "                    WHEN da.opcaoDesistencia is null THEN null\n" +
                            "                    ELSE 1\n" +
                            "                END)  > 0 THEN 'Inapto'\n" +
                            "        WHEN COUNT(CASE\n" +
                            "                    WHEN da.opcaoDesistencia = 3 THEN 3\n" +
                            "                    WHEN da.opcaoDesistencia = 6 THEN 6\n" +
                            "                    ELSE NULL\n" +
                            "                END) > 0 THEN 'Em suspensão'\n" +
                            "        WHEN COUNT(1) > 0 THEN 'Admitido'\n" +
                            "        ELSE 'Apto para envio'\n" +
                            "    END AS status\n" +
                            " from DocumentoAdmissao da\n" +
                            "    where status > 0\n" +
                            "    and da.idAprovado = :idAprovado\n" +
                            "    --GROUP BY da.idAprovado;").setParameter("idAprovado", listAprovados.get(i).getId()).getSingleResult();
            //TODO refatorar pra fazer em uma unica consulta no banco de dados
            editalAprovadoConcurso.setSituacao( situacao );
            listaAprovadosDto.add(editalAprovadoConcurso);
        }
        return new PaginacaoUtil<>(tamanho, pagina, totalPaginas, totalRegistros, listaAprovadosDto);
    }

    public Integer countAprovadosSemAdmissao(String search) {

        Query query = getEntityManager()
                .createNativeQuery("with  Admissao1 as " +
                        "(select a.* from Admissao a join InfoRemessa i on a.chave = i.chave and i.idUnidadeGestora=:ug and a.tipoAdmissao=1 ), " +
                        "  Servidor1 as " +
                        "(select a.* from Servidor a join InfoRemessa i on a.chave = i.chave and i.idUnidadeGestora=:ug )," +
                        "      Aprovado as " +
                        "(select a.* from EditalAprovado a join InfoRemessa i on a.chave = i.chave and i.idUnidadeGestora=:ug )" +
                        " " +
                        "select distinct count(1) from Aprovado a join EditalVaga b on a.idEditalVaga = b.id where (select count(1) from  Admissao1 ad join Servidor1 s  on  ad.idServidor = s.id and s.cpfServidor = a.cpf )=0  " + search)
                .setParameter("ug", User.getUser(super.request).getUnidadeGestora().getId());
        return (Integer) query.getSingleResult();
    }

    public EditalAprovado buscarAprovadoPorCpf(String cpf) {
        try{
        return (EditalAprovado) getEntityManager().createNativeQuery(
                "SELECT  c.* " +
                        "FROM EditalAprovado c " +
                        " join InfoRemessa  i on c.chave=i.chave  and  i.idUnidadeGestora = :ug " +
                        " WHERE   c.cpf = :cpf  "
                , EditalAprovado.class)
                .setParameter("cpf",cpf)
                .setParameter("ug",User.getUser(super.request).getUnidadeGestora().getId()).setMaxResults(1).getSingleResult();
        }catch (NoResultException e){
            return null;
        }
    }

    public EditalAprovado buscarAprovadoPorInscricao(String inscricao) {
        try {

            return  (EditalAprovado) getEntityManager().createNativeQuery(
                            "SELECT  c.* " +
                                    "FROM EditalAprovado c " +
                                    " join InfoRemessa  i on c.chave=i.chave and i.idUnidadeGestora= :ug " +
                                    " WHERE   c.numeroInscricao = :inscricao "
                            , EditalAprovado.class)
                    .setParameter("inscricao",inscricao)
                    .setParameter("ug",User.getUser(super.request).getUnidadeGestora().getId())
                    .setMaxResults(1).getSingleResult();
        }catch (NoResultException e){
            return null;
        }
    }

    public EditalAprovado buscarAprovadoPorClassificacaoConc(BigInteger idvaga, String Classificacao) {
        try{
        return (EditalAprovado) getEntityManager().createNativeQuery(
                "SELECT  ea.* " +
                        "FROM EditalAprovado ea " +
                        " join InfoRemessa  i on ea.chave =i.chave and i.idUnidadeGestora =  :ug " +
                        " WHERE ea.idEditalVaga= :idvaga and  ea.classificacao = :classificacao    "
                , EditalAprovado.class)
                .setParameter("idvaga",idvaga)
                .setParameter("classificacao",Classificacao)
                .setParameter("ug",User.getUser(super.request).getUnidadeGestora().getId()).setMaxResults(1).getSingleResult();
        }catch (NoResultException e){
            return null;
        }
    }

    public List<Map<String, Object>> buscarInfoReciboAdmissao(Integer numeroProcesso, Integer anoProcesso) {

        List<Map<String, Object>> informacoesReciboAdmissao = new ArrayList<Map<String, Object>>();

        try {

            var query = entityManager.createNativeQuery(
                    "select p.nome NomeResponsavel,p.cpf CpfResponsavel,a.data_assinatura DataAssinatura, null NumeroEdital, i.nomeUnidade , i.idUnidadeGestora , pj.nomeMunicipio  , ed.nome , ed.cpf from " +
                            " AdmissaoEnvioAssinatura a" +
                            "  inner join SICAPAP21..AdmissaoEnvio env on a.idEnvio= env.id" +
                            "  inner join SICAPAP21..DocumentoAdmissao docenv on env.id= docenv.idEnvio and docenv.status > 0 " +
                            "  inner join SICAPAP21..EditalAprovado ed on docenv.idAprovado = ed.id " +
                            "  inner join cadun..vwPessoa p on a.cpf=p.cpf " +
                            "  inner join InfoRemessa i on ed.chave=i.chave  and env.cnpjUnidadeGestora = i.idUnidadeGestora " +
                            " inner join cadun..vwPessoaJuridica pj on i.idUnidadeGestora=pj.cnpj" +
                            " where env.processo='" + numeroProcesso + "/" + anoProcesso + "'");
            List<Object[]> InformacoesInteressadosAdmissao = query.getResultList();


            for (Object[] informacaoInteressadoAdmissao : InformacoesInteressadosAdmissao) {

                Map<String, Object> mapa = new HashMap<String, Object>();

                mapa.put("NomeResponsavel",  informacaoInteressadoAdmissao[0]);
                mapa.put("CpfResponsavel", informacaoInteressadoAdmissao[1]);
                mapa.put("DataAssinatura", informacaoInteressadoAdmissao[2]);
                mapa.put("NumeroEdital",  informacaoInteressadoAdmissao[3]);
                mapa.put("nomeUnidade", informacaoInteressadoAdmissao[4]);
                mapa.put("idUnidadeGestora", informacaoInteressadoAdmissao[5]);
                mapa.put("nomeMunicipio",  informacaoInteressadoAdmissao[6]);
                mapa.put("nomeInteressado",  informacaoInteressadoAdmissao[7]);
                mapa.put("cpfInteressado",  informacaoInteressadoAdmissao[8]);
                informacoesReciboAdmissao.add(mapa);

            }
            ;

            return informacoesReciboAdmissao;


        } catch (RuntimeException e) {
            throw new RuntimeException("problema ao gerar o recibo dos aprovados!!");
        }
    }
}
