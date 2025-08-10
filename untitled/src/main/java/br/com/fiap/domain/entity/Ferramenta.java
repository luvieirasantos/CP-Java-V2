package br.com.fiap.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TDS_TB_FERRAMENTAS")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Ferramenta {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqFerramentas")
    @SequenceGenerator(name = "seqFerramentas", sequenceName = "TDS_SEQ_FERRAMENTAS", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Column(name = "TIPO", length = 50)
    private String tipo;

    @Column(name = "CLASSIFICACAO", length = 50)
    private String classificacao;

    @Column(name = "TAMANHO", length = 50)
    private String tamanho;

    @Column(name = "PRECO", nullable = false)
    private Double preco;
}