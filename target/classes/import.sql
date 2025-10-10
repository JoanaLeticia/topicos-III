INSERT INTO usuario (nome, email, senha, perfil) VALUES 
('Admin Sistema', 'admin@email.com', 'SYu34Plo5KZGE9fMtUK9LRPnWC3WvVpogVg35bf5tPYMM6dxXNV6AWmPEQzOLc110uIwcv8TOigbaCB43f8KHQ==', 1), -- admin123
('João Cliente', 'joao@email.com', 'SYu34Plo5KZGE9fMtUK9LRPnWC3WvVpogVg35bf5tPYMM6dxXNV6AWmPEQzOLc110uIwcv8TOigbaCB43f8KHQ==', 2), --admin123
('Maria Cliente', 'maria@email.com', 'SYu34Plo5KZGE9fMtUK9LRPnWC3WvVpogVg35bf5tPYMM6dxXNV6AWmPEQzOLc110uIwcv8TOigbaCB43f8KHQ==', 2); -- 321

INSERT INTO cliente (id) VALUES 
(2),
(3);

INSERT INTO administrador (id, cpf, data_nascimento) VALUES 
(1, '111.222.333-44', '1980-01-10');

INSERT INTO estado (nome, sigla) VALUES 
('Tocantins', 'TO'),
('São Paulo', 'SP'),
('Goiás', 'GO'),
('Rio Grande do Sul', 'RS'),
('Rio de Janeiro', 'RJ');

INSERT INTO municipio (nome, estado_id) VALUES 
('Porto Nacional', 1),
('Goiânia', 3),
('São Paulo', 2),
('Palmas', 1),
('Porto Alegre', 4);

INSERT INTO endereco (logradouro, numero, complemento, bairro, cep, municipio_id, id_cliente) VALUES 
('Rua Costa Silva Pereira', '1231', null, 'Setor Aeroporto', '77500-000', 1, 2),
('Avenida Bandeirantes', '432', null, 'Vila Mariana', '74000-000', 2, 2),
('Rua Anhenguera', '78312', null, 'Vila Nova', '49313-641', 3, 3),
('Quadra 103 Sul', '15', 'Bloco A', 'Plano Diretor Sul', '77015-020', 4, 3);

INSERT INTO telefone (codArea, numero, id_cliente) VALUES 
('63', '984323854', 2),
('99', '981453843', 3),
('11', '992349812', 2),
('61', '33445566', 3);

-- ============================================
-- 4. ITENS DO CARDÁPIO - ALMOÇO (20 itens)
-- ============================================
INSERT INTO itens_cardapio (nome, descricao, preco_base, periodo, nome_imagem) VALUES
-- Pratos principais
('Filé à Parmegiana', 'Filé bovino empanado com molho de tomate e queijo gratinado', 45.90, 'ALMOCO', 'file-a-parmegiana.jpg'),
('Frango Grelhado', 'Peito de frango grelhado com ervas finas', 32.90, 'ALMOCO', 'frango-grelhado.jpg'),
('Picanha na Chapa', 'Picanha argentina grelhada com acompanhamentos', 58.90, 'ALMOCO', 'picanha-na-chapa.jpg'),
('Peixe à Delícia', 'Filé de peixe ao molho de camarão', 42.90, 'ALMOCO', 'peixe-a-delicia.jpg'),
('Bife Acebolado', 'Bife bovino com cebolas refogadas', 38.90, 'ALMOCO', 'bife-acebolado.jpg'),
('Strogonoff de Frango', 'Strogonoff cremoso com batata palha', 36.90, 'ALMOCO', 'strogonoff-de-frango.jpg'),
('Lasanha à Bolonhesa', 'Lasanha com molho bolonhesa e queijo', 34.90, 'ALMOCO', 'lasanha-bolonhesa.jpg'),
('Feijoada Completa', 'Feijoada tradicional com todos os acompanhamentos', 42.90, 'ALMOCO', 'feijoada-completa.jpg'),
('Costela ao Barbecue', 'Costela suína ao molho barbecue', 46.90, 'ALMOCO', 'costelinha-ao-barbecue.jpg'),
('Tilápia Grelhada', 'Tilápia grelhada com legumes', 39.90, 'ALMOCO', 'tilapia-grelhada.jpg'),
-- Massas
('Espaguete à Carbonara', 'Massa ao molho carbonara com bacon', 33.90, 'ALMOCO', 'espaguete-a-carbonara.jpg'),
('Penne ao Molho Branco', 'Penne com molho branco e frango', 32.90, 'ALMOCO', 'penne-ao-molho-branco.jpg'),
('Nhoque ao Sugo', 'Nhoque de batata ao molho sugo', 29.90, 'ALMOCO', 'nhoque-ao-sugo.jpg'),
-- Saladas e opções leves
('Salada Caesar', 'Salada caesar com frango grelhado', 28.90, 'ALMOCO', 'salada-caesar.jpg'),
('Wrap de Frango', 'Wrap com frango, alface e molho especial', 26.90, 'ALMOCO', 'wrap-de-frango.jpg'),
-- Executivos
('Executivo Carne', 'Arroz, feijão, bife, salada e batata frita', 24.90, 'ALMOCO', 'executivo-carne.jpg'),
('Executivo Frango', 'Arroz, feijão, frango grelhado, salada e legumes', 22.90, 'ALMOCO', 'executivo-frango.jpg'),
('Executivo Peixe', 'Arroz, feijão, peixe grelhado, salada e purê', 26.90, 'ALMOCO', 'executivo-peixe.jpg'),
('Executivo Vegetariano', 'Arroz integral, feijão, legumes grelhados e salada', 21.90, 'ALMOCO', 'executivo-vegetariano.jpg'),
('Marmitex Tradicional', 'Arroz, feijão, carne de panela, salada e farofa', 19.90, 'ALMOCO', 'marmitex-tradicional.jpg');

-- ============================================
-- 5. ITENS DO CARDÁPIO - JANTAR (20 itens)
-- ============================================
INSERT INTO itens_cardapio (nome, descricao, preco_base, periodo, nome_imagem) VALUES
-- Pratos sofisticados
('Risoto de Camarão', 'Risoto cremoso com camarões grandes', 62.90, 'JANTAR', 'risoto-camarao.jpg'),
('Salmão ao Molho de Maracujá', 'Salmão grelhado com molho de maracujá', 68.90, 'JANTAR', 'salmao-ao-molho-de-maracuja.jpg'),
('Medalhão ao Molho Madeira', 'Medalhão bovino ao molho madeira', 72.90, 'JANTAR', 'medalhao-ao-molho-madeira.jpg'),
('Bacalhau à Portuguesa', 'Bacalhau desfiado com batatas e azeitonas', 78.90, 'JANTAR', 'bacalhau-a-portuguesa.jpg'),
('Filé Mignon ao Molho Gorgonzola', 'Filé mignon com molho gorgonzola', 69.90, 'JANTAR', 'file-mignon-ao-molho-gorgonzola.jpg'),
('Camarão à Grega', 'Camarões ao molho de tomate com queijo', 64.90, 'JANTAR', 'camarao-a-grega.jpg'),
('Polvo ao Vinagrete', 'Polvo grelhado com vinagrete especial', 82.90, 'JANTAR', 'polvo-ao-vinagrete.jpg'),
('Risoto de Cogumelos', 'Risoto com mix de cogumelos nobres', 58.90, 'JANTAR', 'risoto-de-cogumelos.jpg'),
-- Massas Premium
('Tagliatelle ao Funghi', 'Massa fresca com molho de funghi', 54.90, 'JANTAR', 'tagliatelle-ao-funghi.jpg'),
('Ravióli de Queijos', 'Ravióli recheado com quatro queijos', 52.90, 'JANTAR', 'ravioli-de-queijos.jpg'),
('Fettuccine Alfredo', 'Fettuccine ao molho alfredo com frango', 49.90, 'JANTAR', 'fettuccine-alfredo.jpg'),
-- Carnes Premium
('Picanha Premium', 'Picanha argentina 400g com acompanhamentos', 89.90, 'JANTAR', 'picanha-premium.jpg'),
('Cordeiro Assado', 'Carré de cordeiro com ervas', 95.90, 'JANTAR', 'cordeiro-assado.jpg'),
('Tournedor ao Molho de Vinho', 'Tournedor com molho de vinho tinto', 76.90, 'JANTAR', 'tournedor-ao-molho-de-vinho.jpg'),
-- Frutos do Mar
('Moqueca de Peixe', 'Moqueca capixaba tradicional', 72.90, 'JANTAR', 'moqueca-de-peixe.jpg'),
('Lagosta Thermidor', 'Lagosta ao molho thermidor gratinado', 145.90, 'JANTAR', 'lagosta-thermidor.jpg'),
-- Opções Leves
('Carpaccio de Salmão', 'Salmão em fatias finas com alcaparras', 48.90, 'JANTAR', 'carpaccio-de-salmao.jpg'),
('Salada Caprese Premium', 'Tomate, mussarela de búfala e manjericão', 38.90, 'JANTAR', 'salada-carprese-premium.jpg'),
-- Pratos Especiais
('Paella Valenciana', 'Paella tradicional com frutos do mar', 86.90, 'JANTAR', 'paella-valenciana.jpg'),
('Duck Confit', 'Pato confitado com molho de laranja', 92.90, 'JANTAR', 'duck-confit.jpg');

-- ============================================
-- 6. SUGESTÕES DO CHEFE
-- ============================================
INSERT INTO sugestoes_chefe (data, item_almoco_id, item_jantar_id) VALUES
(CURRENT_DATE, 6, 21);

-- ============================================
-- 7. APPs PARCEIROS
-- ============================================
INSERT INTO parceiros_app (nome, percentual_comissao, taxa_fixa) VALUES
('iFood', 15.00, 3.99),
('Uber Eats', 18.00, 2.99),
('Rappi', 12.00, 4.50),
('Delivery Direto', 10.00, 5.00);

-- ============================================
-- 8. MESAS
-- ============================================
INSERT INTO mesas (numero, capacidade, disponivel) VALUES
(1, 2, true),
(2, 2, true),
(3, 4, true),
(4, 4, true),
(5, 4, true),
(6, 6, true),
(7, 6, true),
(8, 8, true),
(9, 8, true),
(10, 10, true);

-- ============================================
-- 9.1. ATENDIMENTO - PRESENCIAL
-- ============================================
INSERT INTO atendimentos (tipo_atendimento, numero_mesa) VALUES
('PRESENCIAL', 3),
('PRESENCIAL', 5),
('PRESENCIAL', 7);

-- ============================================
-- 9.2. ATENDIMENTO - DELIVERY PRÓPRIO
-- ============================================
INSERT INTO atendimentos (tipo_atendimento, endereco_entrega_id, taxa_entrega) VALUES
('DELIVERY_PROPRIO', 1, 5.00),
('DELIVERY_PROPRIO', 2, 5.00),
('DELIVERY_PROPRIO', 3, 5.00);

-- ============================================
-- 9.3. ATENDIMENTO - DELIVERY APP
-- ============================================
INSERT INTO atendimentos (tipo_atendimento, endereco_entrega_id, parceiro_id) VALUES
('DELIVERY_APLICATIVO', 1, 1),
('DELIVERY_APLICATIVO', 2, 2),
('DELIVERY_APLICATIVO', 4, 3);

-- ============================================
-- 10. PEDIDOS
-- ============================================
INSERT INTO pedidos (cliente_id, atendimento_id, status, data_pedido, periodo) VALUES
-- Pedidos de Almoço
(2, 1, 'CONCLUIDO', CURRENT_TIMESTAMP - INTERVAL '5 hours', 'ALMOCO'), -- Presencial
(2, 4, 'CONCLUIDO', CURRENT_TIMESTAMP - INTERVAL '1 hours', 'ALMOCO'), -- Delivery Próprio com desconto (Sugestão do Chef)
(3, 2, 'EM_PREPARO', CURRENT_TIMESTAMP - INTERVAL '1 hour', 'ALMOCO'),
(3, 5, 'CONFIRMADO', CURRENT_TIMESTAMP - INTERVAL '30 minutes', 'ALMOCO'),
-- Pedidos de Jantar
(2, 7, 'PENDENTE', CURRENT_TIMESTAMP, 'JANTAR'), -- Delivery App
(3, 3, 'CONCLUIDO', CURRENT_TIMESTAMP - INTERVAL '2 days', 'JANTAR'),
(2, 8, 'CONCLUIDO', CURRENT_TIMESTAMP - INTERVAL '3 days', 'JANTAR'),
(3, 6, 'CONCLUIDO', CURRENT_TIMESTAMP - INTERVAL '1 day', 'JANTAR');

-- ============================================
-- 11. RELACIONAMENTO PEDIDO <-> ITEM (N-N)
-- ============================================
-- Pedido 1 (Presencial - Almoço)
INSERT INTO pedido_item (pedido_id, item_id) VALUES (1, 1), (1, 10); -- Filé Parmegiana + Tilápia

-- Pedido 2 (Delivery Próprio - Almoço com desconto de Sugestão do Chef)
INSERT INTO pedido_item (pedido_id, item_id) VALUES (2, 6); -- Strogonoff (Sugestão do Chef)

-- Pedido 3 (Presencial - Almoço)
INSERT INTO pedido_item (pedido_id, item_id) VALUES (3, 16), (3, 17); -- Executivo Carne + Executivo Frango

-- Pedido 4 (Delivery Próprio - Almoço)
INSERT INTO pedido_item (pedido_id, item_id) VALUES (4, 3), (4, 5); -- Picanha + Bife Acebolado

-- Pedido 5 (Delivery App - Jantar)
INSERT INTO pedido_item (pedido_id, item_id) VALUES (5, 21), (5, 22); -- Risoto de Camarão + Salmão

-- Pedido 6 (Presencial - Jantar)
INSERT INTO pedido_item (pedido_id, item_id) VALUES (6, 23), (6, 25); -- Medalhão + Filé ao Gorgonzola

-- Pedido 7 (Delivery App - Jantar)
INSERT INTO pedido_item (pedido_id, item_id) VALUES (7, 24), (7, 26); -- Bacalhau + Camarão à Grega

-- Pedido 8 (Delivery Próprio - Jantar)
INSERT INTO pedido_item (pedido_id, item_id) VALUES (8, 32), (8, 35); -- Picanha Premium + Moqueca

-- ============================================
-- 12. RESERVAS
-- ============================================
INSERT INTO reservas (usuario_id, mesa_id, data_hora, numero_pessoas, codigo_confirmacao) VALUES
(2, 6, CURRENT_DATE + INTERVAL '1 day' + TIME '19:30:00', 4, 'ABCD1234'),
(3, 8, CURRENT_DATE + INTERVAL '1 day' + TIME '20:00:00', 6, 'EFGH5678'),
(3, 10, CURRENT_DATE + INTERVAL '2 days' + TIME '19:00:00', 8, 'IJKL9012'),
(2, 3, CURRENT_DATE + INTERVAL '3 days' + TIME '20:30:00', 2, 'MNOP3456');

