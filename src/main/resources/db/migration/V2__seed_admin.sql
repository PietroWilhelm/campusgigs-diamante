-- Usuário ADMIN de demonstração, necessário para testar a regra
-- "ADMIN pode encerrar qualquer serviço" (Checkpoint 4).
-- Senha: admin123 (hash BCrypt abaixo).
INSERT INTO usuario (nome, email, senha, role)
VALUES ('Administrador', 'admin@campusgigs.com', '$2b$10$tq.9jhD.aShg8lqL7Wj/reB9squdaI16FkPJFJJOrdqLUclQgOHKa', 'ADMIN');
