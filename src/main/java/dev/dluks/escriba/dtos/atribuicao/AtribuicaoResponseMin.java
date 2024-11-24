package dev.dluks.escriba.dtos.atribuicao;

import dev.dluks.escriba.domain.entities.Atribuicao;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AtribuicaoResponseMin {

    private String id;
    private String nome;

    public AtribuicaoResponseMin(String id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public static AtribuicaoResponseMin fromEntity(Atribuicao atribuicao) {
        return new AtribuicaoResponseMin(atribuicao.getId(), atribuicao.getNome());
    }

}
