package dev.dluks.escriba.dtos.situacao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SituacaoRequest {

    @NotBlank(message = "O id da situação é obrigatório")
    private String id;

}
