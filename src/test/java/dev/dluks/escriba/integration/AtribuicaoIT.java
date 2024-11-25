package dev.dluks.escriba.integration;

import dev.dluks.escriba.domain.repositories.AtribuicaoRepository;
import dev.dluks.escriba.dtos.atribuicao.CreateAtribuicaoRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AtribuicaoIT extends IntegrationTestBase {

    @Autowired
    private AtribuicaoRepository repository;

    private CreateAtribuicaoRequest dto;

    @Override
    protected void cleanDatabase() {
        repository.deleteAll();
    }

    @Override
    protected void setupTestData() {
        dto = CreateAtribuicaoRequest.builder()
                .id("ATRIB_TESTE")
                .nome("Teste")
                .situacao(true)
                .build();
    }

    @Test
    @DisplayName("Deve criar e listar atribuição")
    void shouldCreateAndListAtribuicao() throws Exception {
        mockMvc.perform(post("/api/v1/atribuicoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(dto.getId()))
                .andExpect(jsonPath("$.nome").value(dto.getNome()))
                .andExpect(jsonPath("$.situacao").value(true));

        mockMvc.perform(get("/api/v1/atribuicoes")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    @DisplayName("Deve alterar situação da atribuição")
    void shouldChangeAtribuicaoSituacao() throws Exception {
        mockMvc.perform(post("/api/v1/atribuicoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(patch("/api/v1/atribuicoes/{id}/situacao", dto.getId())
                        .param("situacao", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacao").value(false));
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar alterar situação de atribuição inexistente")
    void shouldReturn404WhenChangingNonExistentAtribuicaoSituacao() throws Exception {
        mockMvc.perform(patch("/api/v1/atribuicoes/{id}/situacao", "ID_INEXISTENTE")
                        .param("situacao", "false"))
                .andExpect(status().isNotFound());
    }
}