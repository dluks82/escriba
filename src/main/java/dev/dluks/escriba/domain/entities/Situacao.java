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
@Table(name = "situacoes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Situacao {

    @Id
    @NotBlank(message = "O id é obrigatório")
    @Size(max = 20, message = "O id deve ter no máximo 20 caracteres")
    @Column(length = 20)
    private String id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 50, message = "O nome deve ter no máximo 50 caracteres")
    @Column(length = 50, nullable = false, unique = true)
    private String nome;

    @Builder
    public Situacao(String id, String nome) {
        validateId(id);
        validateNome(nome);
        this.id = id;
        this.nome = nome;
    }

    private void validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new DomainException("O id é obrigatório");
        }
        if (id.length() > 20) {
            throw new DomainException("O id deve ter no máximo 20 caracteres");
        }
    }

    private void validateNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new DomainException("O nome é obrigatório");
        }
        if (nome.length() > 50) {
            throw new DomainException("O nome deve ter no máximo 50 caracteres");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Situacao situacao = (Situacao) o;
        return Objects.equals(id, situacao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Situacao{" +
               "id='" + id + '\'' +
               ", nome='" + nome + '\'' +
               '}';
    }

}
