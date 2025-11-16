-----------------------------------------
-- TABELA: produto
-----------------------------------------
IF OBJECT_ID('produto', 'U') IS NOT NULL
DROP TABLE produto;
GO

CREATE TABLE produto (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nome NVARCHAR(255) NOT NULL,
    tipo NVARCHAR(100) NOT NULL,
    rentabilidade DECIMAL(18,6) NOT NULL,
    risco NVARCHAR(50) NOT NULL
);
GO


-----------------------------------------
-- TABELA: investimento
-----------------------------------------
IF OBJECT_ID('investimento', 'U') IS NOT NULL
DROP TABLE investimento;
GO

CREATE TABLE investimento (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    clienteId BIGINT NOT NULL,
    tipo NVARCHAR(100) NOT NULL,
    valor DECIMAL(18,2) NOT NULL,
    rentabilidade DECIMAL(18,6) NOT NULL,
    data DATE NOT NULL
);
GO


-----------------------------------------
-- TABELA: simulacao
-----------------------------------------
IF OBJECT_ID('simulacao', 'U') IS NOT NULL
DROP TABLE simulacao;
GO

CREATE TABLE simulacao (
   id BIGINT IDENTITY(1,1) PRIMARY KEY,
   clienteId BIGINT NOT NULL,
   produto NVARCHAR(255) NOT NULL,
   valorInvestido DECIMAL(18,2) NOT NULL,
   valorFinal DECIMAL(18,2) NOT NULL,
   prazoMeses INT NOT NULL,
   dataSimulacao DATETIME2 NOT NULL
);
GO


IF OBJECT_ID('telemetria', 'U') IS NOT NULL
DROP TABLE telemetria;
GO

CREATE TABLE telemetria (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    servico NVARCHAR(255) NOT NULL,
    quantidadeChamadas BIGINT NOT NULL,
    mediaRespostaMs DECIMAL(18,6) NOT NULL,
    data DATETIME2 NOT NULL
);
GO