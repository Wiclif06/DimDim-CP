-- ============================================================================
-- DimDim - DDL das tabelas (Azure SQL Database / SQL Server)
-- Executado pelo passo 02 do README (scripts/02-criar-tabelas.sh).
-- Pode ser executado mais de uma vez: so cria o que ainda nao existe.
-- Tambem pode ser colado no "Query editor" do portal Azure.
--
-- Relacionamento: TB_DD_CLIENTE (1) ----< (N) TB_DD_PAGAMENTO
-- ============================================================================

IF OBJECT_ID(N'dbo.TB_DD_CLIENTE', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.TB_DD_CLIENTE (
        id_cliente   BIGINT        IDENTITY(1,1) NOT NULL,
        nome         NVARCHAR(100) NOT NULL,
        email        NVARCHAR(120) NOT NULL,
        cpf          CHAR(11)      NOT NULL,
        telefone     NVARCHAR(20)  NULL,
        dt_cadastro  DATETIME2(3)  NOT NULL CONSTRAINT DF_TB_DD_CLIENTE_DT DEFAULT SYSUTCDATETIME(),
        CONSTRAINT PK_TB_DD_CLIENTE       PRIMARY KEY (id_cliente),
        CONSTRAINT UQ_TB_DD_CLIENTE_EMAIL UNIQUE (email),
        CONSTRAINT UQ_TB_DD_CLIENTE_CPF   UNIQUE (cpf)
    );
END

IF OBJECT_ID(N'dbo.TB_DD_PAGAMENTO', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.TB_DD_PAGAMENTO (
        id_pagamento  BIGINT         IDENTITY(1,1) NOT NULL,
        id_cliente    BIGINT         NOT NULL,
        descricao     NVARCHAR(200)  NOT NULL,
        valor         DECIMAL(12,2)  NOT NULL,
        metodo        VARCHAR(20)    NOT NULL,
        status        VARCHAR(20)    NOT NULL CONSTRAINT DF_TB_DD_PAGAMENTO_STATUS DEFAULT 'PENDENTE',
        dt_pagamento  DATETIME2(3)   NOT NULL CONSTRAINT DF_TB_DD_PAGAMENTO_DT DEFAULT SYSUTCDATETIME(),
        CONSTRAINT PK_TB_DD_PAGAMENTO         PRIMARY KEY (id_pagamento),
        CONSTRAINT FK_TB_DD_PAGAMENTO_CLIENTE FOREIGN KEY (id_cliente) REFERENCES dbo.TB_DD_CLIENTE (id_cliente),
        CONSTRAINT CK_TB_DD_PAGAMENTO_VALOR   CHECK (valor > 0),
        CONSTRAINT CK_TB_DD_PAGAMENTO_METODO  CHECK (metodo IN ('PIX', 'CARTAO', 'BOLETO')),
        CONSTRAINT CK_TB_DD_PAGAMENTO_STATUS  CHECK (status IN ('PENDENTE', 'APROVADO', 'CANCELADO')),
        INDEX IX_TB_DD_PAGAMENTO_CLIENTE NONCLUSTERED (id_cliente)
    );
END
