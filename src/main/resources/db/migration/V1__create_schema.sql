-- CP1: schema inicial da aplicação.
CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(100) NOT NULL,
    cep VARCHAR(8),
    cidade VARCHAR(100),
    uf VARCHAR(2),
    role VARCHAR(20) NOT NULL DEFAULT 'USER'
);

CREATE TABLE servico (
    id BIGSERIAL PRIMARY KEY,
    prestador_id BIGINT NOT NULL REFERENCES usuario(id),
    titulo VARCHAR(150) NOT NULL,
    descricao VARCHAR(2000),
    categoria VARCHAR(100) NOT NULL,
    preco NUMERIC(10,2) NOT NULL,
    situacao VARCHAR(20) NOT NULL DEFAULT 'ATIVO'
);

CREATE TABLE contratacao (
    id BIGSERIAL PRIMARY KEY,
    servico_id BIGINT NOT NULL REFERENCES servico(id),
    contratante_id BIGINT NOT NULL REFERENCES usuario(id),
    situacao VARCHAR(20) NOT NULL DEFAULT 'SOLICITADA',
    data_contratacao TIMESTAMP NOT NULL DEFAULT now()
);
