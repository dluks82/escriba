package dev.dluks.escriba.dtos.situacao;

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
public class CreateSituacaoRequest {

    @NotBlank(message = "O id é obrigatório")
    @Size(max = 20, message = "O id deve ter no máximo 20 caracteres")
    private String id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 50, message = "O nome deve ter no máximo 50 caracteres")
    private String nome;

}
