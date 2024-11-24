package dev.dluks.escriba.dtos.atribuicao;

import dev.dluks.escriba.domain.entities.Atribuicao;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AtribuicaoResponse {

    private String id;
    private String nome;
    private boolean situacao;

    public AtribuicaoResponse(String id, String nome, boolean situacao) {
        this.id = id;
        this.nome = nome;
        this.situacao = situacao;
    }

    public static AtribuicaoResponse fromEntity(Atribuicao atribuicao) {
        return new AtribuicaoResponse(atribuicao.getId(), atribuicao.getNome(), atribuicao.isSituacao());
    }

}
