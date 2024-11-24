package dev.dluks.escriba.domain.entities;

import dev.dluks.escriba.domain.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AtribuicaoTest {

    @Test
    @DisplayName("Deve criar atribuição válida")
    void shouldCreateValidAtribuicao() {
        Atribuicao atribuicao = Atribuicao.builder()
                .id("ATRIB_NOTAS")
                .nome("Tabelionato de Notas")
                .build();

        assertNotNull(atribuicao);
        assertEquals("ATRIB_NOTAS", atribuicao.getId());
        assertEquals("Tabelionato de Notas", atribuicao.getNome());
        assertTrue(atribuicao.isAtribuicaoAtiva());
    }

    @Test
    @DisplayName("Deve permitir criar atribuição inativa")
    void shouldAllowCreateInactiveAtribuicao() {
        Atribuicao atribuicao = Atribuicao.builder()
                .id("ATRIB_NOTAS")
                .nome("Tabelionato de Notas")
                .situacao(false)
                .build();

        assertFalse(atribuicao.isSituacao());
    }

    @Test
    @DisplayName("Não deve criar atribuição com id nulo")
    void shouldNotCreateAtribuicaoWithNullId() {
        Atribuicao.AtribuicaoBuilder builder = Atribuicao.builder()
                .id(null)
                .nome("Tabelionato de Notas");

        assertThrows(DomainException.class, builder::build);
    }

    @Test
    @DisplayName("Não deve criar atribuição com id vazio")
    void shouldNotCreateAtribuicaoWithEmptyId() {
        Atribuicao.AtribuicaoBuilder builder = Atribuicao.builder()
                .id("")
                .nome("Tabelionato de Notas");

        assertThrows(DomainException.class, builder::build);
    }

    @Test
    @DisplayName("Não deve criar atribuição com id maior que 20 caracteres")
    void shouldNotCreateAtribuicaoWithIdGreaterThan20Characters() {
        String idLongo = "A".repeat(21);
        Atribuicao.AtribuicaoBuilder builder = Atribuicao.builder()
                .id(idLongo)
                .nome("Tabelionato de Notas");

        assertThrows(DomainException.class, builder::build);
    }

    @Test
    @DisplayName("Não deve criar atribuição com nome nulo")
    void shouldNotCreateAtribuicaoWithNullName() {
        Atribuicao.AtribuicaoBuilder builder = Atribuicao.builder()
                .id("ATRIB_NOTAS")
                .nome(null);

        assertThrows(DomainException.class, builder::build);
    }

    @Test
    @DisplayName("Não deve criar atribuição com nome vazio")
    void shouldNotCreateAtribuicaoWithEmptyName() {
        Atribuicao.AtribuicaoBuilder builder = Atribuicao.builder()
                .id("ATRIB_NOTAS")
                .nome("");

        assertThrows(DomainException.class, builder::build);
    }

    @Test
    @DisplayName("Não deve criar atribuição com nome maior que 50 caracteres")
    void shouldNotCreateAtribuicaoWithNameGreaterThan50Characters() {
        String nomeLongo = "A".repeat(51);
        Atribuicao.AtribuicaoBuilder builder = Atribuicao.builder()
                .id("ATRIB_NOTAS")
                .nome(nomeLongo);

        assertThrows(DomainException.class, builder::build);
    }


}