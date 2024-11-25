package dev.dluks.escriba.dtos.cartorio;

import dev.dluks.escriba.domain.entities.Atribuicao;
import dev.dluks.escriba.domain.entities.Cartorio;
import dev.dluks.escriba.domain.entities.Situacao;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
public class CartorioResponse {

    private Integer id;
    private String nome;
    private String observacao;
    private Situacao situacao;
    private Set<Atribuicao> atribuicoes = new HashSet<>();

    public CartorioResponse(Integer id, String nome, String observacao, Situacao situacao, Set<Atribuicao> atribuicoes) {
        this.id = id;
        this.nome = nome;
        this.observacao = observacao;
        this.situacao = situacao;
        this.atribuicoes = atribuicoes;
    }

    public static CartorioResponse fromEntity(Cartorio cartorio) {
        return new CartorioResponse(cartorio.getId(), cartorio.getNome(), cartorio.getObservacao(), cartorio.getSituacao(), cartorio.getAtribuicoes());
    }

}
