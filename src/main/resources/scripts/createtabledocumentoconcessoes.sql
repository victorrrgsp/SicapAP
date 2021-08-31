create table dbo.DocumentoAposentadoria
(
    id numeric(28) identity
        constraint DocumentoAposentadoria_pk
        primary key nonclustered,
    idAposentadoria numeric(28)
        constraint DocumentoAposentadoria_Aposentadoria_id_fk
            references dbo.Aposentadoria,
    status int,
    idCastorFile varchar(100),
    inciso varchar(50)
)
    go