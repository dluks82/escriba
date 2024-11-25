package dev.dluks.escriba.domain.entities;

import dev.dluks.escriba.domain.exceptions.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartorioTest {

    private Situacao situacaoAtiva;
    private Atribuicao atribuicaoNotas;

    @BeforeEach
    void setUp() {
        situacaoAtiva = Situacao.builder()
                .id("SIT_ATIVO")
                .nome("Ativo")
                .build();

        atribuicaoNotas = Atribuicao.builder()
                .id("ATRIB_NOTAS")
                .nome("Tabelionato de Notas")
                .situacao(true)
                .build();
    }

    @Test
    @DisplayName("Deve criar um cartório válido")
    void shouldCreateValidCartorio() {
        Cartorio cartorio = Cartorio.builder()
                .id(1)
                .nome("1º Cartório de Notas")
                .observacao("Cartório mais antigo da cidade")
                .situacao(situacaoAtiva)
                .build();

        cartorio.adicionarAtribuicao(atribuicaoNotas);

        assertNotNull(cartorio);
        assertEquals(1, cartorio.getId());
        assertEquals("1º Cartório de Notas", cartorio.getNome());
        assertEquals("Cartório mais antigo da cidade", cartorio.getObservacao());
        assertEquals("SIT_ATIVO", cartorio.getSituacao().getId());
        assertEquals(1, cartorio.getAtribuicoes().size());
        assertTrue(cartorio.getAtribuicoes().contains(atribuicaoNotas));
    }

    @Test
    @DisplayName("Deve criar um cartório sem observação")
    void shouldCreateCartorioWithoutObservacao() {
        Cartorio cartorio = Cartorio.builder()
                .id(1)
                .nome("1º Cartório de Notas")
                .situacao(situacaoAtiva)
                .build();

        cartorio.adicionarAtribuicao(atribuicaoNotas);

        assertNull(cartorio.getObservacao());
        assertNotNull(cartorio);
    }

    @Test
    @DisplayName("Não deve criar cartório sem atribuição")
    void shouldNotCreateCartorioWithoutAtribuicao() {
        Cartorio cartorio = Cartorio.builder()
                .id(1)
                .nome("1º Cartório de Notas")
                .situacao(situacaoAtiva)
                .build();

        assertThrows(DomainException.class, () ->
                cartorio.removerAtribuicao(atribuicaoNotas)
        );
    }

    @Test
    @DisplayName("Deve gerenciar atribuições")
    void shouldManageAtribuicoes() {
        // Criar cartório
        Cartorio cartorio = Cartorio.builder()
                .id(1)
                .nome("1º Cartório de Notas")
                .situacao(situacaoAtiva)
                .build();

        // Adicionar primeira atribuição
        cartorio.adicionarAtribuicao(atribuicaoNotas);
        assertEquals(1, cartorio.getAtribuicoes().size());

        // Adicionar segunda atribuição
        Atribuicao atribuicaoProtesto = Atribuicao.builder()
                .id("ATRIB_PROTESTO")
                .nome("Tabelionato de Protesto")
                .situacao(true)
                .build();
        cartorio.adicionarAtribuicao(atribuicaoProtesto);
        assertEquals(2, cartorio.getAtribuicoes().size());

        // Remover uma atribuição (deve permitir pois ainda resta uma)
        cartorio.removerAtribuicao(atribuicaoProtesto);
        assertEquals(1, cartorio.getAtribuicoes().size());

        // Tentar remover a última atribuição (deve falhar)
        assertThrows(DomainException.class, () ->
                cartorio.removerAtribuicao(atribuicaoNotas)
        );
    }

    @Test
    @DisplayName("Não deve aceitar atribuição com id inválido")
    void shouldNotAcceptInvalidId() {

        // Arrange
        Cartorio.CartorioBuilder baseBuilder = Cartorio.builder()
                .nome("Cartório")
                .situacao(situacaoAtiva);

        // Act & Assert
        assertAll(
                () -> shouldThrowDomainExceptionForId(null, baseBuilder),
                () -> shouldThrowDomainExceptionForId(0, baseBuilder),
                () -> shouldThrowDomainExceptionForId(-1, baseBuilder)
        );

    }

    @Test
    @DisplayName("Não deve aceitar nome inválido")
    void shouldNotAcceptInvalidNome() {

        // Arrange
        Cartorio.CartorioBuilder baseBuilder = Cartorio.builder()
                .id(1)
                .situacao(situacaoAtiva);

        // Act & Assert
        assertAll(
                () -> shouldThrowDomainExceptionForNome(null, "Nome é obrigatório", baseBuilder),
                () -> shouldThrowDomainExceptionForNome("", "Nome é obrigatório", baseBuilder),
                () -> shouldThrowDomainExceptionForNome("a".repeat(151), "Nome não pode ter mais que 150 caracteres", baseBuilder)
        );
    }

    @Test
    @DisplayName("Não deve aceitar situação nula")
    void shouldNotAcceptNullSituacao() {
        Cartorio.CartorioBuilder builder = Cartorio.builder()
                .id(1)
                .nome("Cartório");

        assertThrows(DomainException.class, builder::build);
    }

    @Test
    @DisplayName("Não deve adicionar atribuição nula")
    void shouldNotAddNullAtribuicao() {
        Cartorio cartorio = Cartorio.builder()
                .id(1)
                .nome("Cartório")
                .situacao(situacaoAtiva)
                .build();

        assertThrows(DomainException.class, () ->
                        cartorio.adicionarAtribuicao(null),
                "Não deve aceitar atribuição nula"
        );
    }

    @Test
    @DisplayName("Não deve adicionar atribuição inativa")
    void shouldNotAddInactiveAtribuicao() {
        Cartorio cartorio = Cartorio.builder()
                .id(1)
                .nome("Cartório")
                .situacao(situacaoAtiva)
                .build();

        Atribuicao atribuicaoInativa = Atribuicao.builder()
                .id("ATRIB_INATIVA")
                .nome("Atribuição Inativa")
                .situacao(false)
                .build();

        assertThrows(DomainException.class, () ->
                        cartorio.adicionarAtribuicao(atribuicaoInativa),
                "Não deve aceitar atribuição inativa"
        );
    }

    private void shouldThrowDomainExceptionForId(
            Integer id,
            Cartorio.CartorioBuilder builder) {
        DomainException exception = assertThrows(DomainException.class,
                () -> builder.id(id).build());
        assertEquals("ID é obrigatório e deve ser maior que zero", exception.getMessage());
    }

    private void shouldThrowDomainExceptionForNome(
            String nome, String expectedMessage,
            Cartorio.CartorioBuilder builder) {
        DomainException exception = assertThrows(DomainException.class,
                () -> builder.nome(nome).build());
        assertEquals(expectedMessage, exception.getMessage());
    }
}