package dev.dluks.escriba.domain.entities;

import dev.dluks.escriba.domain.exceptions.DomainException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cartorios")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cartorio {

    @Id
    @Column(name = "id")
    private Integer id;

    @NotBlank
    @Size(max = 150)
    @Column(name = "nome", length = 150, nullable = false)
    private String nome;

    @Size(max = 250)
    @Column(name = "observacao", length = 250)
    private String observacao;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "situacao_id", nullable = false)
    private Situacao situacao;

    @NotEmpty(message = "O cartório deve ter pelo menos uma atribuição")
    @ManyToMany
    @JoinTable(
            name = "cartorios_atribuicoes",
            joinColumns = @JoinColumn(name = "cartorio_id"),
            inverseJoinColumns = @JoinColumn(name = "atribuicao_id")
    )
    private Set<Atribuicao> atribuicoes = new HashSet<>();

    @Builder
    public Cartorio(Integer id, String nome, String observacao, Situacao situacao, Set<Atribuicao> atribuicoes) {
        validarCamposObrigatorios(id, nome, situacao, atribuicoes);
        this.id = id;
        this.nome = nome;
        this.observacao = observacao;
        this.situacao = situacao;
        this.atribuicoes = new HashSet<>(atribuicoes);
    }

    private void validarCamposObrigatorios(Integer id, String nome, Situacao situacao, Set<Atribuicao> atribuicoes) {
        if (id == null || id <= 0) {
            throw new DomainException("ID é obrigatório e deve ser maior que zero");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new DomainException("Nome é obrigatório");
        }
        if (situacao == null) {
            throw new DomainException("Situação é obrigatória");
        }
        if (atribuicoes == null || atribuicoes.isEmpty()) {
            throw new DomainException("O cartório deve ter pelo menos uma atribuição");
        }

        validarTamanhoNome(nome);
    }

    private void validarTamanhoNome(String nome) {
        if (nome.length() > 150) {
            throw new DomainException("Nome não pode ter mais que 150 caracteres");
        }
    }

    public void changeSituacao(Situacao situacao) {
        if (situacao == null) {
            throw new DomainException("Situação é obrigatória");
        }
        this.situacao = situacao;
    }

    public void adicionarAtribuicao(Atribuicao atribuicao) {
        if (atribuicao == null) {
            throw new DomainException("Atribuição não pode ser nula");
        }
        if (!atribuicao.isSituacao()) {
            throw new DomainException("Não é possível adicionar uma atribuição inativa");
        }
        atribuicoes.add(atribuicao);
    }

    public void removerAtribuicao(Atribuicao atribuicao) {
        if (atribuicoes.size() <= 1) {
            throw new DomainException("O cartório deve ter pelo menos uma atribuição");
        }
        atribuicoes.remove(atribuicao);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cartorio cartorio = (Cartorio) o;
        return id.equals(cartorio.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}