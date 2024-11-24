package dev.dluks.escriba.services;

import dev.dluks.escriba.domain.entities.Atribuicao;
import dev.dluks.escriba.domain.exceptions.DuplicateResourceException;
import dev.dluks.escriba.domain.exceptions.ResourceNotFoundException;
import dev.dluks.escriba.domain.repositories.AtribuicaoRepository;
import dev.dluks.escriba.dtos.atribuicao.AtribuicaoResponse;
import dev.dluks.escriba.dtos.atribuicao.AtribuicaoResponseMin;
import dev.dluks.escriba.dtos.atribuicao.CreateAtribuicaoRequest;
import dev.dluks.escriba.dtos.atribuicao.UpdateAtribuicaoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AtribuicaoServiceTest {

    @Mock
    private AtribuicaoRepository repository;

    @InjectMocks
    private AtribuicaoService service;

    private CreateAtribuicaoRequest dtoCreate;
    private CreateAtribuicaoRequest dtoCreateWithSituacaoFalse;
    private UpdateAtribuicaoRequest dtoUpdate;
    private UpdateAtribuicaoRequest dtoUpdateSame;
    private Atribuicao atribuicao;
    private Atribuicao atribuicaoWithSituacaoFalse;

    private AtribuicaoResponse atribuicaoResponse;

    @BeforeEach
    void setup() {
        dtoCreate = CreateAtribuicaoRequest.builder()
                .id("ATRIB_TEST")
                .nome("Test")
                .build();

        dtoCreateWithSituacaoFalse = CreateAtribuicaoRequest.builder()
                .id("ATRIB_TEST")
                .nome("Test")
                .situacao(false)
                .build();

        dtoUpdate = UpdateAtribuicaoRequest.builder()
                .nome("Teste")
                .build();

        dtoUpdateSame = UpdateAtribuicaoRequest.builder()
                .nome("Test")
                .build();

        atribuicao = Atribuicao.builder()
                .id("ATRIB_TEST")
                .nome("Test")
                .situacao(true)
                .build();

        atribuicaoWithSituacaoFalse = Atribuicao.builder()
                .id("ATRIB_TEST")
                .nome("Test")
                .situacao(false)
                .build();

        atribuicaoResponse = AtribuicaoResponse.fromEntity(atribuicao);

    }

    @Test
    @DisplayName("Deve criar uma atribuição")
    void shouldCreateAtribuicao() {
        when(repository.existsById(dtoCreate.getId())).thenReturn(false);
        when(repository.findByNomeIgnoreCase(dtoCreate.getNome())).thenReturn(Optional.empty());
        when(repository.save(any(Atribuicao.class))).thenReturn(atribuicao);

        AtribuicaoResponse result = service.create(dtoCreate);

        assertNotNull(result);
        assertEquals(dtoCreate.getId(), result.getId());
        assertEquals(dtoCreate.getNome(), result.getNome());
        verify(repository).save(any(Atribuicao.class));
    }

    @Test
    @DisplayName("Deve criar uma atribuição com situação false")
    void shouldCreateAtribuicaoWithSituacaoFalse() {
        when(repository.existsById(dtoCreateWithSituacaoFalse.getId())).thenReturn(false);
        when(repository.findByNomeIgnoreCase(dtoCreateWithSituacaoFalse.getNome())).thenReturn(Optional.empty());
        when(repository.save(any(Atribuicao.class))).thenReturn(atribuicaoWithSituacaoFalse);

        AtribuicaoResponse result = service.create(dtoCreateWithSituacaoFalse);

        assertNotNull(result);
        assertEquals(dtoCreateWithSituacaoFalse.getId(), result.getId());
        assertEquals(dtoCreateWithSituacaoFalse.getNome(), result.getNome());
        assertFalse(result.isSituacao());
        verify(repository).save(any(Atribuicao.class));
    }

    @Test
    @DisplayName("Não deve criar uma atribuição com id duplicado")
    void shouldNotCreateAtribuicaoWithDuplicatedId() {
        when(repository.existsById(dtoCreate.getId())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.create(dtoCreate));
    }

    @Test
    @DisplayName("Não deve criar uma atribuição com nome duplicado")
    void shouldNotCreateAtribuicaoWithDuplicatedName() {
        when(repository.existsById(dtoCreate.getId())).thenReturn(false);
        when(repository.findByNomeIgnoreCase(dtoCreate.getNome())).thenReturn(Optional.of(atribuicao));
        when(repository.existsById(dtoCreate.getId())).thenReturn(false);

        when(repository.findByNomeIgnoreCase(dtoCreate.getNome()))
                .thenReturn(Optional.of(atribuicao));

        assertThrows(DuplicateResourceException.class, () -> service
                .create(dtoCreate));
    }

    @Test
    @DisplayName("Deve falhar ao buscar situação por id")
    void shouldFailToFindAtribuicaoById() {
        String id = "ATRIB_TEST";
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(id, dtoUpdate));
    }

    @Test
    @DisplayName("Deve listar todas as atribuições paginado")
    void shouldListAllAtribuicoesPaginated() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Atribuicao> page = new PageImpl<>(List.of(atribuicao));

        when(repository.findAll(pageable)).thenReturn(page);

        Page<AtribuicaoResponseMin> result = service.findAll(pageable);

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertEquals(1, result.getContent().size());
        verify(repository).findAll(pageable);
    }

    @Test
    @DisplayName("Deve ativar uma atribuição")
    void shouldActivateAtribuicao() {
        String id = "ATRIB_TESTE";

        when(repository.findById(id)).thenReturn(Optional.of(atribuicao));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        AtribuicaoResponse resultado = service.changeSituacao(id, true);

        assertTrue(resultado.isSituacao());
    }

    @Test
    @DisplayName("Deve desativar uma atribuição")
    void shouldDeactivateAtribuicao() {
        String id = "ATRIB_TESTE";

        when(repository.findById(id)).thenReturn(Optional.of(atribuicao));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        AtribuicaoResponse resultado = service.changeSituacao(id, false);

        assertFalse(resultado.isSituacao());
    }

    @Test
    @DisplayName("Deve atualizar atribuição")
    void shouldUpdateAtribuicao() {
        when(repository.findById(dtoCreate.getId())).thenReturn(Optional.of(atribuicao));
        when(repository.findByNomeIgnoreCase(dtoCreate.getNome())).thenReturn(Optional.empty());
        when(repository.save(any(Atribuicao.class))).thenReturn(atribuicao);

        AtribuicaoResponse result = service.update(dtoCreate.getId(), dtoUpdate);

        assertNotNull(result);
        assertEquals(dtoCreate.getNome(), result.getNome());
        verify(repository).save(any(Atribuicao.class));
    }

    @Test
    @DisplayName("Deve atualizar atribuição com nome igual ao original")
    void shouldUpdateAtribuicaoWithSameName() {
        when(repository.findById(dtoCreate.getId())).thenReturn(Optional.of(atribuicao));
        when(repository.save(any(Atribuicao.class))).thenReturn(atribuicao);

        AtribuicaoResponse result = service.update(dtoCreate.getId(), dtoUpdateSame);

        assertNotNull(result);
        assertEquals(dtoCreate.getNome(), result.getNome());
        verify(repository).save(any(Atribuicao.class));
    }

    @Test
    @DisplayName("Deve apagar uma atribuição")
    void shouldDeleteAtribuicao() {
        String id = "ATRIB_TESTE";

        when(repository.findById(id)).thenReturn(Optional.of(atribuicao));

        service.delete(id);

        verify(repository).delete(atribuicao);
    }

}