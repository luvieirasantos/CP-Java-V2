-- Tabela do CP
CREATE TABLE TDS_TB_FERRAMENTAS (
                                    ID            NUMBER(10)      PRIMARY KEY,
                                    NOME          VARCHAR2(100)   NOT NULL,
                                    TIPO          VARCHAR2(50),
                                    CLASSIFICACAO VARCHAR2(50),
                                    TAMANHO       VARCHAR2(50),
                                    PRECO         NUMBER(10,2)    NOT NULL
);

-- Sequência para o ID
CREATE SEQUENCE TDS_SEQ_FERRAMENTAS START WITH 1 INCREMENT BY 1 NOCACHE;

INSERT INTO TDS_TB_FERRAMENTAS (ID, NOME, TIPO, CLASSIFICACAO, TAMANHO, PRECO) VALUES
    (TDS_SEQ_FERRAMENTAS.NEXTVAL, 'Martelo', 'Manual', 'Uso geral', 'Médio', 29.90);