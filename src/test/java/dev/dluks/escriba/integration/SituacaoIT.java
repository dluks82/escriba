package dev.dluks.escriba.integration;

import dev.dluks.escriba.domain.repositories.SituacaoRepository;
import dev.dluks.escriba.dtos.situacao.CreateSituacaoRequest;
import dev.dluks.escriba.dtos.situacao.SituacaoResponseMin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SituacaoIT extends IntegrationTestBase {

    @Autowired
    private SituacaoRepository repository;

    private SituacaoResponseMin dto;

    private CreateSituacaoRequest badDto;

    @Override
    protected void cleanDatabase() {
        repository.deleteAll();
    }

    @Override
    protected void setupTestData() {
        dto = new SituacaoResponseMin(
                "SIT_TESTE",
                "Situacao Teste"
        );

        badDto = new CreateSituacaoRequest(
                "",
                "Situacao Teste"
        );
    }

    @Test
    @DisplayName("Deve criar e listar situação")
    void shouldCreateAndListSituacao() throws Exception {
        mockMvc.perform(post("/api/v1/situacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(dto.getId()))
                .andExpect(jsonPath("$.nome").value(dto.getNome()));

        mockMvc.perform(get("/api/v1/situacoes")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(dto.getId()));
    }

    @Test
    @DisplayName("Não deve criar situação com nome duplicado")
    void shouldNotCreateDuplicatedSituacao() throws Exception {
        mockMvc.perform(post("/api/v1/situacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/situacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Deve falhar ao criar situação com nome vazio")
    void shouldFailToCreateSituacaoWithEmptyName() throws Exception {

        mockMvc.perform(post("/api/v1/situacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badDto)))
                .andExpect(status().isBadRequest());
    }
}