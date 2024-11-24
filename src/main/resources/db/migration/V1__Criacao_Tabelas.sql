-- Tabela de situações
CREATE TABLE situacoes
(
    id   VARCHAR(20) PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    CONSTRAINT uk_situacoes_nome UNIQUE (nome)
);

-- Tabela de atribuições
CREATE TABLE atribuicoes
(
    id       VARCHAR(20) PRIMARY KEY,
    nome     VARCHAR(50) NOT NULL,
    situacao BOOLEAN     NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_atribuicoes_nome UNIQUE (nome)
);

-- Tabela de cartórios
CREATE TABLE cartorios
(
    id          INT PRIMARY KEY,
    nome        VARCHAR(150) NOT NULL,
    observacao  VARCHAR(250),
    situacao_id VARCHAR(20)  NOT NULL,
    CONSTRAINT fk_cartorios_situacao FOREIGN KEY (situacao_id)
        REFERENCES situacoes (id)
);

-- Tabela de relacionamento entre cartórios e atribuições
CREATE TABLE cartorios_atribuicoes
(
    cartorio_id   INT         NOT NULL,
    atribuicao_id VARCHAR(20) NOT NULL,
    PRIMARY KEY (cartorio_id, atribuicao_id),
    CONSTRAINT fk_cartorios_atribuicoes_cartorio
        FOREIGN KEY (cartorio_id) REFERENCES cartorios (id),
    CONSTRAINT fk_cartorios_atribuicoes_atribuicao
        FOREIGN KEY (atribuicao_id) REFERENCES atribuicoes (id)
);