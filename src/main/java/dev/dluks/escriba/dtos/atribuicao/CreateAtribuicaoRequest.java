package dev.dluks.escriba.dtos.atribuicao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAtribuicaoRequest {

    @Id
    @NotBlank(message = "O id da atribuição é obrigatório")
    @Size(max = 20, message = "O id da atribuição deve ter no máximo 20 caracteres")
    private String id;

    @NotBlank(message = "O nome da atribuição é obrigatório")
    @Size(max = 50, message = "O nome da atribuição deve ter no máximo 50 caracteres")
    private String nome;

    private Boolean situacao;

}
