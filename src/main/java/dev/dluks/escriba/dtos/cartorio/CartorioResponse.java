package dev.dluks.escriba.dtos.cartorio;

import dev.dluks.escriba.domain.entities.Atribuicao;
import dev.dluks.escriba.domain.entities.Cartorio;
import dev.dluks.escriba.domain.entities.Situacao;
import dev.dluks.escriba.dtos.atribuicao.AtribuicaoResponse;
import dev.dluks.escriba.dtos.situacao.SituacaoResponse;
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
    private SituacaoResponse situacao;
    private Set<AtribuicaoResponse> atribuicoes = new HashSet<>();

    public CartorioResponse(Integer id, String nome, String observacao, Situacao situacao, Set<Atribuicao> atribuicoes) {
        this.id = id;
        this.nome = nome;
        this.observacao = observacao;
        this.situacao = SituacaoResponse.fromEntity(situacao);
        this.atribuicoes = atribuicoes.stream().map(AtribuicaoResponse::fromEntity).collect(java.util.stream.Collectors.toSet());
    }

    public static CartorioResponse fromEntity(Cartorio cartorio) {
        return new CartorioResponse(cartorio.getId(), cartorio.getNome(), cartorio.getObservacao(), cartorio.getSituacao(), cartorio.getAtribuicoes());
    }

}
