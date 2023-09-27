-- Arquivo 1
select wfp.idUnidadeGestora as 'Código da UJ',
        ua.sigla as 'Sigla da UPAG',
        wfp.nome as 'Nome do servidor',
        wfp.cpfServidor as 'CPF do servidor',
        wfp.matriculaServidor as Matrícula,
       (case when wfp.Classe = 'Civil' then 1 else 2 end) as Regime,
       (CASE WHEN wfp.TipoAdmissao = 'Requisitado' THEN 'DE OUTRO ÓRGÃO' ELSE wfp.cargo END) as Cargo,
       (case
            when wfp.TipoAdmissao = 'Eletivo' then '4'
            when c.natureza = 9 then '0'
            else c.natureza end) as 'Natureza do cargo',
        wfp.DataExercicio as 'Data de exercício',
        FORMAT(ap.dataAposentadoria, 'dd/MM/yyyy') as 'Data de aposentadoria',
        FORMAT(des.dataDesligamento, 'dd/MM/yyyy') as 'Data de exclusão',
        (case when ap.cpfServidor IS NOT NULL then '0'
              else wfp.JornadaFolha end) as Jornada,
       (CASE
            WHEN wfp.TipoAdmissao = 'Efetivo' THEN 1
            WHEN wfp.TipoAdmissao = 'Comissionado' THEN 2
            WHEN wfp.TipoAdmissao = 'Contratado' THEN 8
            WHEN wfp.TipoAdmissao = 'Contratado Por Processo Seletivo' THEN 8
            WHEN wfp.TipoAdmissao = 'Eletivo' THEN 1
            WHEN wfp.TipoAdmissao = 'Estagiário' THEN 9
            WHEN wfp.TipoAdmissao = 'Estabilizado' THEN 9
            WHEN wfp.TipoAdmissao = 'Requisitado' THEN 7
            WHEN ap.dataAposentadoria is not null THEN 4
            WHEN p.dataObito is not null THEN 6 END) as 'Categoria da situação', -- Ajustar
        CONCAT(RIGHT('0' + CONVERT(VARCHAR(2), Month( wfp.Competencia )), 2) ,
            '/',
            DATEPART(year, wfp.Competencia)) as 'Mês da folha/Ano',
        REPLACE(SUM(wfp.valor), '.', ',') as 'Valor Bruto'
from SICAPAP21..vwFolhaPagamento wfp
         join SICAPAP21..Admissao ad on ad.id = wfp.idAdmissao
         join SICAPAP21..Lotacao l on ad.idLotacao = l.id
         join SICAPAP21..UnidadeAdministrativa ua on l.idUnidadeAdministrativa = ua.id
         join SICAPAP21..Cargo c on c.id = ad.idCargo and c.codigoCargo = wfp.codigoCargo
         left join SICAPAP21..Aposentadoria ap on ap.id = wfp.idAdmissao
         left join SICAPAP21..Desligamento des on wfp.idAdmissao = des.idAdmissao
         left join SICAPAP21..Pensionista pen on pen.id = wfp.idAdmissao
         left join SICAPAP21..Pensao p on p.cpfServidor = pen.cpfServidor
where wfp.remessa = 2 and wfp.exercicio = 2023 and wfp.NaturezaRubrica = 'Vantagem'
--and wfp.idUnidadeGestora = '02664384000172'
group by wfp.idUnidadeGestora, ua.sigla, wfp.nome, wfp.cpfServidor, wfp.matriculaServidor, wfp.Classe,
         wfp.cargo, wfp.TipoAdmissao, c.natureza, wfp.DataExercicio, ap.dataAposentadoria, des.dataDesligamento,
         wfp.TipoAdmissao, ap.cpfServidor, wfp.JornadaFolha, wfp.Competencia, p.dataObito
order by wfp.nome;


-- Arquivo 2
select wfp.idUnidadeGestora as 'Código da UJ',
        ua.sigla as 'Sigla da UPAG',
        wfp.cpfServidor as 'CPF do instituidor',
        FORMAT(p.dataObito, 'dd/MM/yyyy') as 'Data de óbito do instituidor',
        wfp.matriculaServidor as 'Matrícula do instituidor',
        FORMAT(pen.inicioBeneficio, 'dd/MM/yyyy') as 'Data de concessão da pensão',
        '' as 'Data de exclusão',
        pen.nome as 'Nome do pensionista',
        pen.cpfPensionista as 'CPF do pensionista',
        wfp.matriculaServidor as 'Matrícula do pensionista',
        (CASE
             WHEN pen.grauParentesco = 1 THEN 'Cônjuge ou convivente'
             WHEN pen.grauParentesco = 2 THEN 'Filho Menor'
             WHEN pen.grauParentesco = 3 THEN 'Filho inválido'
             WHEN pen.grauParentesco = 4 THEN 'Filho Menor, estudando de nível superior'
             WHEN pen.grauParentesco = 5 THEN 'Menor sob guarda'
             WHEN pen.grauParentesco = 6 THEN 'Pais (quando dependentes)'
             WHEN pen.grauParentesco = 7 THEN 'Imrão menor' END) as 'Grau de parentesco',
        (CASE
             WHEN pen.grauParentesco = 1 THEN 2
             WHEN pen.grauParentesco = 2 THEN 4
             WHEN pen.grauParentesco = 3 THEN 9
             WHEN pen.grauParentesco = 4 THEN 11
             WHEN pen.grauParentesco = 5 THEN 6
             WHEN pen.grauParentesco = 6 THEN 8
             WHEN pen.grauParentesco = 7 THEN 11 END) as 'Categoria do parentesco',
        CONCAT(RIGHT('0' + CONVERT(VARCHAR(2), Month( wfp.Competencia )), 2),
            '/',
            DATEPART(year, wfp.Competencia)) as 'Mês da folha/Ano',
        REPLACE(SUM(wfp.valor), '.', ',') as 'Valor Bruto'
from SICAPAP21..vwFolhaPagamento wfp
         join SICAPAP21..Admissao ad on ad.id = wfp.idAdmissao
         join SICAPAP21..Lotacao l on ad.idLotacao = l.id
         join SICAPAP21..UnidadeAdministrativa ua on l.idUnidadeAdministrativa = ua.id
         join SICAPAP21..Pensionista pen on pen.id = wfp.idAdmissao
         join SICAPAP21..Pensao p on p.cpfServidor = pen.cpfServidor
where wfp.remessa = 2 and wfp.exercicio = 2023 and wfp.NaturezaRubrica = 'Vantagem'
--and wfp.idUnidadeGestora = '02664384000172'
group by wfp.idUnidadeGestora, ua.sigla, wfp.cpfServidor, p.dataObito, wfp.matriculaServidor,
         pen.inicioBeneficio, pen.nome, pen.cpfPensionista, pen.grauParentesco, wfp.Competencia
order by pen.nome;

-- Arquivo 3
select wfp.idUnidadeGestora as 'Código da UJ',
        wfp.cpfServidor as 'CPF do servidor responsável',
        wfp.matriculaServidor as 'Matrícula do servidor responsável',
        pen.nome as 'Nome do dependente',
        pen.cpfPensionista as 'CPF do dependente',
        FORMAT(pen.dataNascimento, 'dd/MM/yyyy') as 'Data de nascimento do dependente',
        (CASE
             WHEN pen.grauParentesco = 1 THEN 8
             WHEN pen.grauParentesco = 2 THEN 1
             WHEN pen.grauParentesco = 3 THEN 4
             WHEN pen.grauParentesco = 4 THEN 1
             WHEN pen.grauParentesco = 5 THEN 10
             WHEN pen.grauParentesco = 6 THEN 11
             WHEN pen.grauParentesco = 7 THEN 11 END) as 'Código do parentesco',
        CONCAT(RIGHT('0' + CONVERT(VARCHAR(2), Month( wfp.Competencia )), 2),
            '/',
            DATEPART(year, wfp.Competencia)) as 'Mês da folha/Ano'
from SICAPAP21..vwFolhaPagamento wfp
         join SICAPAP21..Pensionista pen on pen.id = wfp.idAdmissao
         join SICAPAP21..Pensao p on p.cpfServidor = pen.cpfServidor
where wfp.remessa = 2 and wfp.exercicio = 2023
--and wfp.idUnidadeGestora = '02664384000172'
group by wfp.idUnidadeGestora, wfp.cpfServidor, wfp.matriculaServidor, pen.nome, pen.cpfPensionista,
         pen.dataNascimento, pen.grauParentesco, wfp.Competencia
order by pen.nome;


-- Arquivo 4
select wfp.idUnidadeGestora as 'Código da UJ',
        wfp.cpfServidor as 'CPF do servidor/pensionista',
        wfp.matriculaServidor as 'Matrícula do servidor/pensionista',
        wfp.codigoFolhaItem as 'Código da rubrica',
        wfp.FolhaItemUnidadeGestora as 'Nome da rubrica',
        CONCAT(RIGHT('0' + CONVERT(VARCHAR(2), Month( wfp.Competencia )), 2),
            '/',
            DATEPART(year, wfp.Competencia)) as 'Mês da folha/Ano',
        REPLACE((wfp.valor), '.', ',') as Valor,
       (CASE WHEN wfp.NaturezaRubrica = 'Vantagem' THEN 1 ELSE 2 END) as 'Indicador de rendimento/desconto',
        (CASE
             WHEN DATEDIFF(month, wfp.Competencia, CONCAT('01', '/', '0', wfp.remessa, '/', wfp.exercicio)) = 0 THEN 0
             ELSE 1 END) as 'Indicador de folha',
        '' as 'Classificação da rubrica'
from SICAPAP21..vwFolhaPagamento wfp
         left join SICAPAP21..Aposentadoria ap on ap.id = wfp.idAdmissao
         left join SICAPAP21..Pensionista pen on pen.id = wfp.idAdmissao
         left join SICAPAP21..Pensao p on p.cpfServidor = pen.cpfServidor
where wfp.remessa = 2 and wfp.exercicio = 2023
--and wfp.idUnidadeGestora = '02664384000172'
group by wfp.idUnidadeGestora, wfp.cpfServidor, wfp.matriculaServidor, wfp.codigoFolhaItem, wfp.FolhaItemUnidadeGestora,
         wfp.Competencia, wfp.valor, wfp.NaturezaRubrica, wfp.remessa, wfp.exercicio
order by matriculaServidor;

