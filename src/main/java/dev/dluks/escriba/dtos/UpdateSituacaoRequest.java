package dev.dluks.escriba.dtos;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class UpdateSituacaoRequest {

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 50, message = "O nome deve ter no máximo 50 caracteres")
    private final String nome;

    public UpdateSituacaoRequest(String nome) {
        this.nome = nome;
    }

}
