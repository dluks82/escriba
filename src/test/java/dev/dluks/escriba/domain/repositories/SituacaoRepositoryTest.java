package dev.dluks.escriba.domain.repositories;

import dev.dluks.escriba.domain.entities.Situacao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class SituacaoRepositoryTest {

    @Autowired
    private SituacaoRepository repository;

    @Test
    @DisplayName("Deve verificar existência por id")
    void shouldExistsById() {
        Situacao situacao = Situacao.builder()
                .id("SIT_TEST")
                .nome("Test")
                .build();

        repository.save(situacao);

        assertTrue(repository.existsById("SIT_TEST"));
        assertFalse(repository.existsById("SIT_TEST_2"));
    }

    @Test
    @DisplayName("Deve verificar existência por nome")
    void shouldExistsByNomeIgnoreCase() {
        Situacao situacao = Situacao.builder()
                .id("SIT_TEST")
                .nome("Test")
                .build();

        repository.save(situacao);

        assertTrue(repository.existsByNomeIgnoreCase("test"));
        assertTrue(repository.existsByNomeIgnoreCase("TEST"));
        assertFalse(repository.existsByNomeIgnoreCase("Teste"));
    }

    @Test
    @DisplayName("Deve buscar por nome")
    void shouldFindByNomeIgnoreCase() {
        Situacao situacao = Situacao.builder()
                .id("SIT_TEST")
                .nome("Test")
                .build();

        repository.save(situacao);

        Optional<Situacao> optional = repository.findByNomeIgnoreCase("test");
        assertTrue(optional.isPresent());
        assertEquals("SIT_TEST", optional.get().getId());
    }
}