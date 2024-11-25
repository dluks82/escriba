package dev.dluks.escriba.dtos.atribuicao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AtribuicaoRequest {

    @NotBlank(message = "O id da atribuição é obrigatório")
    private String id;

}
