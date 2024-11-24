package dev.dluks.escriba.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dluks.escriba.domain.entities.Atribuicao;
import dev.dluks.escriba.dtos.atribuicao.AtribuicaoResponse;
import dev.dluks.escriba.dtos.atribuicao.CreateAtribuicaoRequest;
import dev.dluks.escriba.dtos.atribuicao.UpdateAtribuicaoRequest;
import dev.dluks.escriba.services.AtribuicaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AtribuicaoController.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AtribuicaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AtribuicaoService service;

    private CreateAtribuicaoRequest dto;

    private UpdateAtribuicaoRequest updateDto;

    private Atribuicao atribuicao;

    private Atribuicao updatedEntity;

    private AtribuicaoResponse response;

    private AtribuicaoResponse updatedResponse;

    @BeforeEach
    void setUp() {

        dto = CreateAtribuicaoRequest.builder()
                .id("ATRIB_TESTE")
                .nome("Teste")
                .situacao(true)
                .build();

        atribuicao = Atribuicao.builder()
                .id("ATRIB_TESTE")
                .nome("Teste")
                .situacao(true)
                .build();

        updateDto = UpdateAtribuicaoRequest.builder()
                .nome("Teste Updated")
                .build();

        updatedEntity = Atribuicao.builder()
                .id("ATRIB_TESTE")
                .nome("Teste Updated")
                .situacao(true)
                .build();

        response = AtribuicaoResponse.fromEntity(atribuicao);

        updatedResponse = AtribuicaoResponse.fromEntity(updatedEntity);
    }

    @Test
    @DisplayName("Deve criar uma atribuição")
    void shouldCreateAtribuicao() throws Exception {
        when(service.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/atribuicoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(dto.getId()))
                .andExpect(jsonPath("$.nome").value(dto.getNome()))
                .andExpect(jsonPath("$.situacao").value(dto.getSituacao()));
    }

    @Test
    @DisplayName("Deve obter uma atribuição por ID")
    void shouldFindAtribuicaoById() throws Exception {
        when(service.findById(dto.getId())).thenReturn(response);

        mockMvc.perform(get("/api/v1/atribuicoes/{id}", dto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto.getId()))
                .andExpect(jsonPath("$.nome").value(dto.getNome()))
                .andExpect(jsonPath("$.situacao").value(dto.getSituacao()));
    }

    @Test
    @DisplayName("Deve atualizar uma atribuição")
    void shouldUpdateAtribuicao() throws Exception {
        String id = "ATRIB_TESTE";

        // Use ArgumentMatchers para corresponder aos argumentos exatos
        when(service.update(eq(id), argThat(request ->
                request.getNome().equals(updateDto.getNome())
        ))).thenReturn(updatedResponse);

        // Act & Assert
        mockMvc.perform(put("/api/v1/atribuicoes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value("Teste Updated"));

        // Verifica que o método foi chamado com os argumentos corretos
        verify(service).update(eq(id), argThat(request ->
                request.getNome().equals(updateDto.getNome())
        ));
    }

    @Test
    @DisplayName("Não deve criar atribuição com dados inválidos")
    void shouldNotCreateAtribuicaoWithInvalidData() throws Exception {
        CreateAtribuicaoRequest dtoInvalido = CreateAtribuicaoRequest.builder()
                .id("ATRIB_TESTE")
                .nome("")
                .situacao(true)
                .build();

        mockMvc.perform(post("/api/v1/atribuicoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Um ou mais campos estão inválidos"));
    }

    @Test
    @DisplayName("Deve excluir uma atribuição")
    void shouldDeleteAtribuicao() throws Exception {
        String id = "ATRIB_TESTE";

        mockMvc.perform(delete("/api/v1/atribuicoes/{id}", id))
                .andExpect(status().isNoContent());

        verify(service).delete(id);
    }

}