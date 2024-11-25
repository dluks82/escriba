package dev.dluks.escriba.services;

import dev.dluks.escriba.domain.entities.Atribuicao;
import dev.dluks.escriba.domain.entities.Cartorio;
import dev.dluks.escriba.domain.entities.Situacao;
import dev.dluks.escriba.domain.exceptions.DuplicateResourceException;
import dev.dluks.escriba.domain.exceptions.IntegrityConstraintException;
import dev.dluks.escriba.domain.exceptions.ResourceNotFoundException;
import dev.dluks.escriba.domain.repositories.CartorioRepository;
import dev.dluks.escriba.dtos.atribuicao.AtribuicaoResponse;
import dev.dluks.escriba.dtos.cartorio.CartorioResponse;
import dev.dluks.escriba.dtos.cartorio.CartorioResponseMin;
import dev.dluks.escriba.dtos.cartorio.CreateCartorioRequest;
import dev.dluks.escriba.dtos.cartorio.UpdateCartorioRequest;
import dev.dluks.escriba.dtos.situacao.SituacaoResponse;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CartorioServiceTest {

    @Mock
    private CartorioRepository repository;

    @Mock
    private SituacaoService situacaoService;

    @Mock
    private AtribuicaoService atribuicaoService;

    @InjectMocks
    private CartorioService service;

    private CreateCartorioRequest dto;
    private UpdateCartorioRequest updateDto;
    private UpdateCartorioRequest updateDtoSame;
    private Cartorio cartorio;
    private Cartorio cartorioUpdated;
    private Situacao situacao;
    private Atribuicao atribuicao;

    private SituacaoResponse situacaoResponse;
    private AtribuicaoResponse atribuicaoResponse;

    @BeforeEach
    void setUp() {
        situacao = Situacao.builder()
                .id("SIT_ATIVO")
                .nome("Ativo")
                .build();

        situacaoResponse = SituacaoResponse.fromEntity(situacao);

        atribuicao = Atribuicao.builder()
                .id("ATRIB_NOTAS")
                .nome("Notas")
                .situacao(true)
                .build();

        atribuicaoResponse = AtribuicaoResponse.fromEntity(atribuicao);

        dto = CreateCartorioRequest.builder()
                .id(1)
                .nome("1º Cartório")
                .observacao("Observação teste")
                .situacaoId("SIT_ATIVO")
                .atribuicoesIds(Set.of("ATRIB_NOTAS"))
                .build();

        updateDto = UpdateCartorioRequest.builder()
                .nome("1º Cartório atualizado")
                .observacao("Observação teste atualizado")
                .build();

        updateDtoSame = UpdateCartorioRequest.builder()
                .nome("1º Cartório")
                .observacao("Observação teste")
                .build();

        cartorio = Cartorio.builder()
                .id(1)
                .nome("1º Cartório")
                .observacao("Observação teste")
                .situacao(situacao)
                .atribuicoes(Set.of(atribuicao))
                .build();

        cartorioUpdated = Cartorio.builder()
                .id(1)
                .nome("1º Cartório atualizado")
                .observacao("Observação teste atualizado")
                .situacao(situacao)
                .atribuicoes(Set.of(atribuicao))
                .build();

    }

    @Test
    @DisplayName("Deve criar um cartório")
    void shouldCreateCartorio() {
        when(repository.existsById(dto.getId())).thenReturn(false);
        when(repository.findByNomeIgnoreCase(dto.getNome())).thenReturn(Optional.empty());
        when(situacaoService.findById(dto.getSituacaoId())).thenReturn(situacaoResponse);
        when(atribuicaoService.findById("ATRIB_NOTAS")).thenReturn(atribuicaoResponse);
        when(repository.save(any(Cartorio.class))).thenReturn(cartorio);

        CartorioResponse resultado = service.create(dto);

        assertNotNull(resultado);
        assertEquals(dto.getId(), resultado.getId());
        assertEquals(dto.getNome(), resultado.getNome());
        assertEquals(1, resultado.getAtribuicoes().size());
    }

    @Test
    @DisplayName("Não deve criar cartório com ID duplicado")
    void shouldNotCreateCartorioWithDuplicatedId() {
        when(repository.existsById(dto.getId())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () ->
                service.create(dto)
        );
    }

    @Test
    @DisplayName("Não deve criar cartório com nome duplicado")
    void shouldNotCreateCartorioWithDuplicatedName() {
        when(repository.findByNomeIgnoreCase(cartorio.getNome()))
                .thenReturn(Optional.of(cartorio));

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () ->
                service.create(dto)
        );

        assertTrue(exception.getMessage().contains("Nome já informado no registro"));
    }

    @Test
    @DisplayName("Deve atualizar cartório")
    void shouldUpdateCartorio() {
        when(repository.findById(cartorio.getId())).thenReturn(Optional.of(cartorio));
        when(repository.findByNomeIgnoreCase(updateDto.getNome())).thenReturn(Optional.empty());
        when(repository.save(any(Cartorio.class))).thenReturn(cartorioUpdated);

        CartorioResponse resultado = service.update(cartorio.getId(), updateDto);

        assertNotNull(resultado);
        assertEquals(updateDto.getNome(), resultado.getNome());
    }

    @Test
    @DisplayName("Deve atualizar cartório com mesmo nome")
    void shouldUpdateCartorioWithSameName() {
        when(repository.findById(cartorio.getId())).thenReturn(Optional.of(cartorio));
        when(repository.findByNomeIgnoreCase(updateDtoSame.getNome())).thenReturn(Optional.empty());
        when(repository.save(any(Cartorio.class))).thenReturn(cartorio);

        CartorioResponse resultado = service.update(cartorio.getId(), updateDtoSame);

        assertNotNull(resultado);
        assertEquals(updateDtoSame.getNome(), resultado.getNome());
    }

    @Test
    @DisplayName("Não deve atualizar cartório com nome duplicado")
    void shouldNotUpdateCartorioWithDuplicatedName() {
        when(repository.findById(cartorio.getId())).thenReturn(Optional.of(cartorio));
        when(repository.findByNomeIgnoreCase(updateDto.getNome())).thenReturn(Optional.of(cartorio));

        Integer id = cartorio.getId();

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () ->
                service.update(id, updateDto)
        );

        assertTrue(exception.getMessage().contains("Nome já informado no registro"));
    }

    @Test
    @DisplayName("Deve apagar cartório")
    void shouldDeleteCartorio() {
        when(repository.findById(dto.getId())).thenReturn(Optional.of(cartorio));
        doNothing().when(repository).delete(any(Cartorio.class));

        assertDoesNotThrow(() -> service.delete(dto.getId()));
    }

    @Test
    @DisplayName("Não deve apagar cartório em uso")
    void shouldNotDeleteCartorioInUse() {
        when(repository.findById(dto.getId())).thenReturn(Optional.of(cartorio));
        doThrow(new RuntimeException()).when(repository).flush();

        Integer id = dto.getId();

        IntegrityConstraintException exception =
                assertThrows(IntegrityConstraintException.class, () ->
                        service.delete(id)
                );

        assertEquals("Registro utilizado em outro cadastro.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve listar os cartorios paginados")
    void shouldListCartoriosPaginated() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cartorio> page = new PageImpl<>(List.of(cartorio));

        when(repository.findAll(pageable)).thenReturn(page);

        Page<CartorioResponseMin> result = service.listAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Deve buscar cartório por ID")
    void shouldFindCartorioById() {
        when(repository.findById(cartorio.getId())).thenReturn(Optional.of(cartorio));

        CartorioResponse resultado = service.findById(cartorio.getId());

        assertNotNull(resultado);
        assertEquals(cartorio.getId(), resultado.getId());
        assertEquals(cartorio.getNome(), resultado.getNome());
    }

    @Test
    @DisplayName("Deve falhar ao buscar cartório por ID")
    void shouldFailToFindCartorioById() {
        when(repository.findById(cartorio.getId())).thenReturn(Optional.empty());

        Integer id = cartorio.getId();

        assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
    }
}