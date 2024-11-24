package dev.dluks.escriba.domain.entities;

import dev.dluks.escriba.domain.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SituacaoTest {

    @Test
    @DisplayName("Deve criar situação válida")
    void shouldCreateValidSituacao() {
        Situacao situacao = Situacao.builder()
                .id("SIT_ATIVO")
                .nome("Ativo")
                .build();

        assertNotNull(situacao);
        assertEquals("SIT_ATIVO", situacao.getId());
        assertEquals("Ativo", situacao.getNome());
    }

    @Test
    @DisplayName("Não deve criar situação com id nulo")
    void shouldNotCreateSituacaoWithNullId() {
        Situacao.SituacaoBuilder builder = Situacao.builder()
                .id(null)
                .nome("Ativo");

        assertThrows(DomainException.class, builder::build);
    }

    @Test
    @DisplayName("Não deve criar situação com id vazio")
    void shouldNotCreateSituacaoWithEmptyId() {
        Situacao.SituacaoBuilder builder = Situacao.builder()
                .id("")
                .nome("Ativo");

        assertThrows(DomainException.class, builder::build);
    }

    @Test
    @DisplayName("Não deve criar situação com id maior que 20 caracteres")
    void shouldNotCreateSituacaoWithIdGreaterThan20Characters() {
        String longId = "A".repeat(21);
        Situacao.SituacaoBuilder builder = Situacao.builder()
                .id(longId)
                .nome("Ativo");

        assertThrows(DomainException.class, builder::build);
    }

    @Test
    @DisplayName("Não deve criar situação com nome nulo")
    void shouldNotCreateSituacaoWithNullName() {
        Situacao.SituacaoBuilder builder = Situacao.builder()
                .id("SIT_ATIVO")
                .nome(null);

        assertThrows(DomainException.class, builder::build);
    }

    @Test
    @DisplayName("Não deve criar situação com nome vazio")
    void shouldNotCreateSituacaoWithEmptyName() {
        Situacao.SituacaoBuilder builder = Situacao.builder()
                .id("SIT_ATIVO")
                .nome("");

        assertThrows(DomainException.class, builder::build);
    }

    @Test
    @DisplayName("Não deve criar situação com nome maior que 50 caracteres")
    void shouldNotCreateSituacaoWithNameGreaterThan50Characters() {
        String longNome = "A".repeat(51);
        Situacao.SituacaoBuilder builder = Situacao.builder()
                .id("SIT_ATIVO")
                .nome(longNome);

        assertThrows(DomainException.class, builder::build);
    }

}