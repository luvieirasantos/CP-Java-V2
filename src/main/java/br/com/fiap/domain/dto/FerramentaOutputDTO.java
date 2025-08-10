package br.com.fiap.domain.dto;

import br.com.fiap.domain.entity.Ferramenta;
import lombok.*;

@Data @Builder
public class FerramentaOutputDTO {
    private Long id;
    private String nome;
    private String tipo;
    private String classificacao;
    private String tamanho;
    private Double preco;

    public static FerramentaOutputDTO from(Ferramenta f){
        return FerramentaOutputDTO.builder()
                .id(f.getId())
                .nome(f.getNome())
                .tipo(f.getTipo())
                .classificacao(f.getClassificacao())
                .tamanho(f.getTamanho())
                .preco(f.getPreco())
                .build();
    }
}