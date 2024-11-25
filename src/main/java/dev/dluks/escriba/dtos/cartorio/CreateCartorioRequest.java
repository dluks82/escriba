package dev.dluks.escriba.dtos.cartorio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCartorioRequest {

    @NotNull(message = "ID é obrigatório")
    @Positive(message = "ID deve ser maior que zero")
    private Integer id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
    private String nome;

    @Size(max = 250, message = "Observação deve ter no máximo 250 caracteres")
    private String observacao;

    @NotNull(message = "Situação é obrigatória")
    private String situacaoId;

    @NotEmpty(message = "Pelo menos uma atribuição é obrigatória")
    private Set<String> atribuicoesIds = new HashSet<>();

}
