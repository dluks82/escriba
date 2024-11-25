package dev.dluks.escriba.integration;

import dev.dluks.escriba.domain.entities.Atribuicao;
import dev.dluks.escriba.domain.entities.Cartorio;
import dev.dluks.escriba.domain.entities.Situacao;
import dev.dluks.escriba.domain.repositories.AtribuicaoRepository;
import dev.dluks.escriba.domain.repositories.CartorioRepository;
import dev.dluks.escriba.domain.repositories.SituacaoRepository;
import dev.dluks.escriba.dtos.cartorio.CartorioResponse;
import dev.dluks.escriba.dtos.cartorio.CreateCartorioRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CartorioIT extends IntegrationTestBase {

    @Autowired
    private CartorioRepository cartorioRepository;

    @Autowired
    private SituacaoRepository situacaoRepository;

    @Autowired
    private AtribuicaoRepository atribuicaoRepository;

    private CreateCartorioRequest dto;
    private CreateCartorioRequest badDto;
    private Cartorio cartorio;
    private CartorioResponse cartorioResponse;
    private Situacao situacao;
    private Situacao situacaoInativo;
    private Atribuicao atribuicao;
    private Atribuicao atribuicao2;

    @Override
    protected void cleanDatabase() {
        cartorioRepository.deleteAll();
        situacaoRepository.deleteAll();
        atribuicaoRepository.deleteAll();
    }

    @Override
    protected void setupTestData() {
        situacao = situacaoRepository.save(Situacao.builder()
                .id("SIT_ATIVO")
                .nome("Ativo")
                .build());

        situacaoInativo = situacaoRepository.save(Situacao.builder()
                .id("SIT_INATIVO")
                .nome("Inativo")
                .build());

        atribuicao = atribuicaoRepository.save(Atribuicao.builder()
                .id("ATRIB_NOTAS")
                .nome("Notas")
                .situacao(true)
                .build());

        atribuicao2 = atribuicaoRepository.save(Atribuicao.builder()
                .id("ATRIB_ESCRITURAS")
                .nome("Escrituras")
                .situacao(true)
                .build());

        dto = CreateCartorioRequest.builder()
                .id(1)
                .nome("1º Cartório")
                .observacao("Teste de Integração")
                .situacaoId(situacao.getId())
                .atribuicoesIds(new HashSet<>(Set.of(atribuicao.getId())))
                .build();

        badDto = CreateCartorioRequest.builder()
                .id(2)
                .nome("2º Cartório")
                .observacao("Teste de Integração")
                .situacaoId(situacao.getId())
                .atribuicoesIds(new HashSet<>(Collections.emptySet()))
                .build();
    }

    @Test
    void deveCriarEBuscarCartorio() throws Exception {
        String response = mockMvc.perform(post("/api/v1/cartorios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(dto.getId()))
                .andExpect(jsonPath("$.nome").value(dto.getNome()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(get("/api/v1/cartorios/{id}", dto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value(dto.getNome()))
                .andExpect(jsonPath("$.situacao.id").value(situacao.getId()))
                .andExpect(jsonPath("$.atribuicoes", hasSize(1)))
                .andExpect(jsonPath("$.atribuicoes[0].id").value(atribuicao.getId()))
                .andExpect(jsonPath("$.atribuicoes[0].nome").value(atribuicao.getNome()));
    }

    @Test
    void naoDeveCriarCartorioSemAtribuicao() throws Exception {
        mockMvc.perform(post("/api/v1/cartorios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Um ou mais campos estão inválidos"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]").value("atribuicoesIds: Pelo menos uma atribuição é obrigatória"));
    }

    @Test
    @DisplayName("Deve incluir uma nova atribuição ao cartório")
    void shouldIncludeANewAtribuicaoToCartorio() throws Exception {
        cartorio = cartorioRepository.save(Cartorio.builder()
                .id(1)
                .nome("1º Cartório")
                .observacao("Teste de Integração")
                .situacao(situacao)
                .atribuicoes(new HashSet<>(Set.of(atribuicao)))
                .build());

        mockMvc.perform(put("/api/v1/cartorios/{id}/atribuicoes/add", cartorio.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": \"ATRIB_ESCRITURAS\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cartorio.getId()))
                .andExpect(jsonPath("$.atribuicoes", hasSize(2)));
    }

    @Test
    @DisplayName("Deve remover uma atribuição do cartório")
    void shouldRemoveAtribuicaoFromCartorio() throws Exception {
        cartorio = cartorioRepository.save(Cartorio.builder()
                .id(1)
                .nome("1º Cartório")
                .observacao("Teste de Integração")
                .situacao(situacao)
                .atribuicoes(new HashSet<>(Set.of(atribuicao, atribuicao2)))
                .build());

        mockMvc.perform(put("/api/v1/cartorios/{id}/atribuicoes/remove", cartorio.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": \"ATRIB_ESCRITURAS\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cartorio.getId()))
                .andExpect(jsonPath("$.atribuicoes", hasSize(1)));
    }

    @Test
    @DisplayName("Deve alterar a situação do cartório")
    void shouldChangeCartorioSituacao() throws Exception {
        cartorio = cartorioRepository.save(Cartorio.builder()
                .id(1)
                .nome("1º Cartório")
                .observacao("Teste de Integração")
                .situacao(situacao)
                .atribuicoes(new HashSet<>(Set.of(atribuicao)))
                .build());

        mockMvc.perform(put("/api/v1/cartorios/{id}/situacao", cartorio.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": \"SIT_INATIVO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cartorio.getId()))
                .andExpect(jsonPath("$.situacao.id").value("SIT_INATIVO"));
    }

}