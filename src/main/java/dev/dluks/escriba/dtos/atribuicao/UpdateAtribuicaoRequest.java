package dev.dluks.escriba.dtos.atribuicao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAtribuicaoRequest {

    @NotBlank(message = "O nome da atribuição é obrigatório")
    @Size(max = 50, message = "O nome da atribuição deve ter no máximo 50 caracteres")
    private String nome;

}
