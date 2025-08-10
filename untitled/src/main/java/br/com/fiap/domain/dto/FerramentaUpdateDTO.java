package br.com.fiap.domain.dto;

import br.com.fiap.domain.entity.Ferramenta;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class FerramentaUpdateDTO {
    @NotBlank @Size(min = 2, max = 100)
    private String nome;
    @Size(max = 50)
    private String tipo;
    @Size(max = 50)
    private String classificacao;
    @Size(max = 50)
    private String tamanho;
    @NotNull @Positive
    private Double preco;

    public Ferramenta toEntity(){
        return Ferramenta.builder()
                .nome(nome)
                .tipo(tipo)
                .classificacao(classificacao)
                .tamanho(tamanho)
                .preco(preco)
                .build();
    }
}