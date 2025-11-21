-----------------------------------------
-- PRODUTOS
-----------------------------------------
INSERT INTO produto (nome, tipo, rentabilidade, risco) VALUES
('CDB Caixa 2026', 'CDB', 0.12, 'Baixo'),
('LCI Caixa 2025', 'LCI', 0.10, 'Baixo'),
('LCA Caixa Agronegócio', 'LCA', 0.105, 'Baixo'),
('Tesouro Selic 2028', 'Tesouro', 0.115, 'Médio'),
('Fundo XPTO Multimercado', 'Fundo', 0.18, 'Alto'),
('Fundo Alpha Agressivo', 'Fundo', 0.22, 'Alto');

-----------------------------------------
-- INVESTIMENTOS (histórico dos clientes)
-----------------------------------------
INSERT INTO investimento (clienteId, tipo, valor, rentabilidade, data) VALUES
(123, 'CDB', 5000.00, 0.12, '2025-01-15'),
(123, 'Fundo Multimercado', 3000.00, 0.08, '2025-03-10'),
(123, 'Tesouro Selic', 2000.00, 0.11, '2025-04-02'),
(456, 'LCI', 10000.00, 0.10, '2025-02-01'),
(456, 'LCA', 8000.00, 0.105, '2025-03-18'),
(789, 'Fundo Agressivo', 15000.00, 0.20, '2025-05-21');

-- cliente conservador
INSERT INTO investimento (clienteId, tipo, valor, rentabilidade, data) VALUES
    (900, 'CDB', 3000.00, 0.06, '2025-01-10');

-- cliente agressivo
INSERT INTO investimento (clienteId, tipo, valor, rentabilidade, data) VALUES
(901, 'Ação - TechCorp',           25000.00, 0.25, '2025-01-05'),
(901, 'Fundo Agressivo XP',        20000.00, 0.22, '2025-02-14'),
(901, 'ETF - XTESLA11',            15000.00, 0.27, '2025-03-20'),
(901, 'Criptomoeda - BTC',         30000.00, 0.35, '2025-04-12'),
(901, 'Fundo Multimercado High',    8000.00, 0.18, '2025-05-02'),
(901, 'Ação - EnergyPlus',          7000.00, 0.21, '2025-06-10');

-- cliente moderado
INSERT INTO investimento (clienteId, tipo, valor, rentabilidade, data) VALUES
(102, 'Tesouro IPCA', 7000.00, 0.09, '2025-02-20'),
(102, 'Fundo Moderado XP', 6000.00, 0.13, '2025-03-14'),
(102, 'Previdência Moderada', 4000.00, 0.10, '2025-04-22');

-----------------------------------------
-- SIMULAÇÕES
-----------------------------------------
INSERT INTO simulacao (clienteId, produto, valorInvestido, valorFinal, prazoMeses, dataSimulacao) VALUES
(123, 'CDB Caixa 2026', 10000.00, 11200.00, 12, '2025-10-31T14:00:00'),
(123, 'Fundo XPTO Multimercado', 5000.00, 5800.00, 6, '2025-09-15T10:30:00'),
(456, 'Tesouro Selic 2028', 7000.00, 7850.00, 10, '2025-08-20T11:10:00');


INSERT INTO PERFIL_FAIXA_VOLUME (VALOR_MIN, VALOR_MAX, PONTOS, DESCRICAO) VALUES
(0,       4999.99, 10, 'Volume menor que 5.000'),
(5000,    19999.99, 20, 'Volume entre 5.000 e 20.000'),
(20000,   NULL,     30, 'Volume acima de 20.000');

INSERT INTO PERFIL_FAIXA_FREQUENCIA (QTD_MIN, QTD_MAX, PONTOS, DESCRICAO) VALUES
(0, 1, 10, 'Menos de 2 investimentos'),
(2, 4, 20, 'Entre 2 e 4 investimentos'),
(5, NULL, 30, '5 ou mais investimentos');

INSERT INTO PERFIL_PREFERENCIA_LIQUIDEZ (PORCENTAGEM_MIN, PORCENTAGEM_MAX, PONTOS, DESCRICAO) VALUES
(0,     49.99, 20, 'Preferência baixa por alta liquidez'),
(50.00, NULL,  10, 'Preferência alta por alta liquidez');

INSERT INTO PERFIL_INVESTIDOR_FAIXA_PONTUACAO (PONTOS_MIN, PONTOS_MAX, PERFIL, DESCRICAO) VALUES
(0,   40, 'CONSERVADOR', 'Busca segurança e baixa variação, priorizando liquidez.'),
(41,  70, 'MODERADO',    'Perfil equilibrado entre segurança e rentabilidade.'),
(71,  NULL, 'AGRESSIVO', 'Busca maior rentabilidade aceitando riscos mais elevados.');