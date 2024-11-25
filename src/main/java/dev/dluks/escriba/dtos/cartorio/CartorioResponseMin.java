package dev.dluks.escriba.dtos.cartorio;

import dev.dluks.escriba.domain.entities.Cartorio;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartorioResponseMin {

    private Integer id;
    private String nome;

    public CartorioResponseMin(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public static CartorioResponseMin fromEntity(Cartorio cartorio) {
        return new CartorioResponseMin(cartorio.getId(), cartorio.getNome());
    }

}
