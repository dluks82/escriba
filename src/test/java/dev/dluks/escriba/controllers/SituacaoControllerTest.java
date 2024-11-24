package dev.dluks.escriba.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dluks.escriba.domain.entities.Situacao;
import dev.dluks.escriba.dtos.situacao.CreateSituacaoRequest;
import dev.dluks.escriba.dtos.situacao.SituacaoResponse;
import dev.dluks.escriba.dtos.situacao.SituacaoResponseMin;
import dev.dluks.escriba.dtos.situacao.UpdateSituacaoRequest;
import dev.dluks.escriba.services.SituacaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SituacaoController.class)
class SituacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SituacaoService service;

    @Test
    @DisplayName("Deve criar situação")
    void shouldCreateSituacao() throws Exception {
        CreateSituacaoRequest dto = CreateSituacaoRequest.builder()
                .id("SIT_TESTE")
                .nome("Teste")
                .build();

        Situacao entity = Situacao.builder()
                .id("SIT_TESTE")
                .nome("Teste")
                .build();

        SituacaoResponse responseEntity = SituacaoResponse.fromEntity(entity);

        when(service.create(any())).thenReturn(responseEntity);

        mockMvc.perform(post("/api/v1/situacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(entity.getId()))
                .andExpect(jsonPath("$.nome").value(entity.getNome()));
    }

    @Test
    @DisplayName("Não deve criar situação com dados inválidos")
    void shouldNotCreateSituacaoWithInvalidData() throws Exception {
        CreateSituacaoRequest dto = CreateSituacaoRequest
                .builder()
                .build();

        mockMvc.perform(post("/api/v1/situacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Um ou mais campos estão inválidos"));
    }

    @Test
    @DisplayName("Deve buscar situação por ID")
    void shouldFindSituacaoById() throws Exception {
        Situacao entity = Situacao.builder()
                .id("SIT_TESTE")
                .nome("Teste")
                .build();

        SituacaoResponse responseEntity = SituacaoResponse.fromEntity(entity);

        when(service.findById(entity.getId())).thenReturn(responseEntity);

        mockMvc.perform(get("/api/v1/situacoes/{id}", entity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(entity.getId()))
                .andExpect(jsonPath("$.nome").value(entity.getNome()));
    }

    @Test
    @DisplayName("Deve listar todas as situações")
    void shouldListAllSituacoes() throws Exception {
        SituacaoResponseMin dto = new SituacaoResponseMin(
                "SIT_TESTE",
                "Teste"
        );

        Page<SituacaoResponseMin> page = new PageImpl<>(List.of(dto));
        when(service.listAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/situacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(dto.getId()))
                .andExpect(jsonPath("$.content[0].nome").value(dto.getNome()));
    }

    @Test
    @DisplayName("Deve alterar situação")
    void shouldUpdateSituacao() throws Exception {
        String id = "SIT_TESTE";

        UpdateSituacaoRequest dto = UpdateSituacaoRequest.builder()
                .nome("Teste Updated")
                .build();

        SituacaoResponse updatedEntity = SituacaoResponse.fromEntity(
                Situacao.builder()
                        .id(id)
                        .nome("Teste Updated")
                        .build());

        // Use ArgumentMatchers para corresponder aos argumentos exatos
        when(service.update(eq(id), argThat(request ->
                request.getNome().equals(dto.getNome())
        ))).thenReturn(updatedEntity);

        // Act & Assert
        mockMvc.perform(put("/api/v1/situacoes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value("Teste Updated"));

        // Verifica que o método foi chamado com os argumentos corretos
        verify(service).update(eq(id), argThat(request ->
                request.getNome().equals(dto.getNome())
        ));
    }

}