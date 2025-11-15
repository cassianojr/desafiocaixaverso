-- This file allow to write SQL commands that will be emitted in test and dev.
-- The commands are commented as their support depends of the database
-- insert into myentity (id, field) values(1, 'field-1');
-- insert into myentity (id, field) values(2, 'field-2');
-- insert into myentity (id, field) values(3, 'field-3');
-- alter sequence myentity_seq restart with 4;


INSERT INTO produtos (nome, tipo, rentabilidade, risco)
VALUES
    ('CDB Caixa 2026', 'CDB', 0.12, 'Baixo'),
    ('LCI Caixa 2025', 'LCI', 0.10, 'Baixo'),
    ('Fundo XPTO', 'Fundo', 0.18, 'Alto');