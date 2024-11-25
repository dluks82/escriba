package dev.dluks.escriba.services;

import dev.dluks.escriba.domain.entities.Situacao;
import dev.dluks.escriba.domain.exceptions.DuplicateResourceException;
import dev.dluks.escriba.domain.exceptions.ResourceNotFoundException;
import dev.dluks.escriba.domain.repositories.SituacaoRepository;
import dev.dluks.escriba.dtos.situacao.CreateSituacaoRequest;
import dev.dluks.escriba.dtos.situacao.SituacaoResponse;
import dev.dluks.escriba.dtos.situacao.SituacaoResponseMin;
import dev.dluks.escriba.dtos.situacao.UpdateSituacaoRequest;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SituacaoServiceTest {

    @Mock
    private SituacaoRepository repository;

    @InjectMocks
    private SituacaoService service;

    private CreateSituacaoRequest dtoCreate;
    private UpdateSituacaoRequest dtoUpdate;
    private UpdateSituacaoRequest dtoUpdateSame;
    private Situacao situacao;

    @BeforeEach
    void setup() {
        dtoCreate = CreateSituacaoRequest.builder()
                .id("SIT_TEST")
                .nome("Test")
                .build();

        dtoUpdate = UpdateSituacaoRequest.builder()
                .nome("Teste")
                .build();

        dtoUpdateSame = UpdateSituacaoRequest.builder()
                .nome("Test")
                .build();

        situacao = Situacao.builder()
                .id("SIT_TEST")
                .nome("Test")
                .build();
    }

    @Test
    @DisplayName("Deve criar uma situação")
    void shouldCreateSituacao() {
        when(repository.existsById(dtoCreate.getId())).thenReturn(false);
        when(repository.findByNomeIgnoreCase(dtoCreate.getNome())).thenReturn(Optional.empty());
        when(repository.save(any(Situacao.class))).thenReturn(situacao);

        SituacaoResponse result = service.create(dtoCreate);

        assertNotNull(result);
        assertEquals(dtoCreate.getId(), result.getId());
        assertEquals(dtoCreate.getNome(), result.getNome());
        verify(repository).save(any(Situacao.class));
    }

    @Test
    @DisplayName("Deve buscar situação por id")
    void shouldFindSituacaoById() {
        when(repository.findById(dtoCreate.getId())).thenReturn(Optional.of(situacao));

        SituacaoResponse result = service.findById(dtoCreate.getId());

        assertNotNull(result);
        assertEquals(dtoCreate.getId(), result.getId());
        verify(repository).findById(dtoCreate.getId());
    }

    @Test
    @DisplayName("Deve falhar ao buscar situação por id")
    void shouldFailToFindSituacaoById() {
        when(repository.findById(dtoCreate.getId())).thenReturn(Optional.empty());

        String id = dtoCreate.getId();
        assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
        verify(repository).findById(dtoCreate.getId());
    }

    @Test
    @DisplayName("Não deve criar situação com id duplicado")
    void shouldNotCreateSituacaoWithDuplicateId() {
        when(repository.existsById(dtoCreate.getId())).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () ->
                service.create(dtoCreate));

        assertEquals("Registro já cadastrado", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve criar situação com nome duplicado")
    void shouldNotCreateSituacaoWithDuplicateNome() {
        when(repository.existsById(dtoCreate.getId())).thenReturn(false);
        when(repository.findByNomeIgnoreCase(dtoCreate.getNome())).thenReturn(Optional.of(situacao));

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () ->
                service.create(dtoCreate));

        assertEquals("Nome já informado no registro com código " + situacao.getId(), exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar situação")
    void shouldUpdateSituacao() {
        when(repository.findById(dtoCreate.getId())).thenReturn(Optional.of(situacao));
        when(repository.findByNomeIgnoreCase(dtoCreate.getNome())).thenReturn(Optional.empty());
        when(repository.save(any(Situacao.class))).thenReturn(situacao);

        SituacaoResponse result = service.update(dtoCreate.getId(), dtoUpdate);

        assertNotNull(result);
        assertEquals(dtoCreate.getNome(), result.getNome());
        verify(repository).save(any(Situacao.class));
    }

    @Test
    @DisplayName("Deve atualizar situação com nome igual ao original")
    void shouldUpdateSituacaoWithSameNome() {
        when(repository.findById(dtoCreate.getId())).thenReturn(Optional.of(situacao));
        when(repository.save(any(Situacao.class))).thenReturn(situacao);

        SituacaoResponse result = service.update(dtoCreate.getId(), dtoUpdateSame);

        assertNotNull(result);
        assertEquals(dtoCreate.getNome(), result.getNome());
        verify(repository).save(any(Situacao.class));
    }

    @Test
    @DisplayName("Deve listar paginado")
    void shouldListAllPaginated() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Situacao> page = new PageImpl<>(List.of(situacao));

        when(repository.findAll(pageable)).thenReturn(page);

        Page<SituacaoResponseMin> result = service.listAll(pageable);

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertEquals(1, result.getContent().size());
        verify(repository).findAll(pageable);
    }

    @Test
    @DisplayName("Deve apagar situação")
    void shouldDeleteSituacao() {
        when(repository.findById(dtoCreate.getId())).thenReturn(Optional.of(situacao));
        doNothing().when(repository).delete(situacao);

        assertDoesNotThrow(() -> service.delete(dtoCreate.getId()));
        verify(repository).delete(situacao);
    }

}