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

-----------------------------------------
-- SIMULAÇÕES
-----------------------------------------
INSERT INTO simulacao (clienteId, produto, valorInvestido, valorFinal, prazoMeses, dataSimulacao) VALUES
(123, 'CDB Caixa 2026', 10000.00, 11200.00, 12, '2025-10-31T14:00:00'),
(123, 'Fundo XPTO Multimercado', 5000.00, 5800.00, 6, '2025-09-15T10:30:00'),
(456, 'Tesouro Selic 2028', 7000.00, 7850.00, 10, '2025-08-20T11:10:00');