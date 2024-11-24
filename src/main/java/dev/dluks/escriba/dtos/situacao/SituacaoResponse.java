package dev.dluks.escriba.dtos.situacao;

import dev.dluks.escriba.domain.entities.Situacao;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SituacaoResponse {
    private String id;
    private String nome;

    public SituacaoResponse(String id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public static SituacaoResponse fromEntity(Situacao situacao) {
        return new SituacaoResponse(situacao.getId(), situacao.getNome());
    }

}
