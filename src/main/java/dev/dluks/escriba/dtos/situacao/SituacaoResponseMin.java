package dev.dluks.escriba.dtos.situacao;

import dev.dluks.escriba.domain.entities.Situacao;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SituacaoResponseMin {

    private String id;
    private String nome;

    public SituacaoResponseMin(String id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public static SituacaoResponseMin fromEntity(Situacao situacao) {
        return new SituacaoResponseMin(situacao.getId(), situacao.getNome());
    }

}
