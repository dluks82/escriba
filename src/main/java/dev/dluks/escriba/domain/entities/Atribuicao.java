package dev.dluks.escriba.domain.entities;

import dev.dluks.escriba.domain.exceptions.DomainException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "atribuicoes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Atribuicao {

    @Id
    @NotBlank(message = "O id da atribuição é obrigatório")
    @Size(max = 20, message = "O id da atribuição deve ter no máximo 20 caracteres")
    @Column(length = 20)
    private String id;

    @NotBlank(message = "O nome da atribuição é obrigatório")
    @Size(max = 50, message = "O nome da atribuição deve ter no máximo 50 caracteres")
    @Column(length = 50, nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private boolean situacao;

    @Builder
    public Atribuicao(String id, String nome, Boolean situacao) {
        validateId(id);
        validateNome(nome);
        this.id = id;
        this.nome = nome;
        this.situacao = situacao != null ? situacao : true;
    }

    private void validateId(String id) {
        if (id == null || id.trim().isBlank()) {
            throw new DomainException("O id da atribuição é obrigatório");
        }
        if (id.length() > 20) {
            throw new DomainException("O id da atribuição deve ter no máximo 20 caracteres");
        }
    }

    private void validateNome(String nome) {
        if (nome == null || nome.trim().isBlank()) {
            throw new DomainException("O nome da atribuição é obrigatório");
        }
        if (nome.length() > 50) {
            throw new DomainException("O nome da atribuição deve ter no máximo 50 caracteres");
        }
    }

    public boolean isAtribuicaoAtiva() {
        return situacao;
    }

    public void activate() {
        this.situacao = true;
    }

    public void deactivate() {
        this.situacao = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Atribuicao that = (Atribuicao) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Atribuicao{" +
               "id='" + id + '\'' +
               ", nome='" + nome + '\'' +
               ", situacao=" + situacao +
               '}';
    }
}
