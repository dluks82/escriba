package dev.dluks.escriba.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dluks.escriba.config.WebMvcTestConfig;
import dev.dluks.escriba.domain.entities.Atribuicao;
import dev.dluks.escriba.domain.entities.Cartorio;
import dev.dluks.escriba.domain.entities.Situacao;
import dev.dluks.escriba.dtos.cartorio.CartorioResponse;
import dev.dluks.escriba.dtos.cartorio.CartorioResponseMin;
import dev.dluks.escriba.dtos.cartorio.CreateCartorioRequest;
import dev.dluks.escriba.dtos.cartorio.UpdateCartorioRequest;
import dev.dluks.escriba.services.CartorioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasItems;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartorioController.class)
@Import(WebMvcTestConfig.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CartorioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartorioService service;

    private Situacao situacao;

    private Atribuicao atribuicao;

    private Cartorio cartorio;

    private CreateCartorioRequest dto;

    private CartorioResponse response;

    private CartorioResponseMin responseMin;

    @BeforeEach
    void setUp() {

        situacao = Situacao.builder()
                .id("SIT_ATIVO")
                .nome("Ativo")
                .build();

        atribuicao = Atribuicao.builder()
                .id("ATRIB_NOTAS")
                .nome("Notas")
                .build();

        dto = CreateCartorioRequest.builder()
                .id(1)
                .nome("1º Cartório")
                .observacao("Observação teste")
                .situacaoId("SIT_ATIVO")
                .atribuicoesIds(new HashSet<>(Set.of("ATRIB_NOTAS")))
                .build();

        cartorio = Cartorio.builder()
                .id(1)
                .nome("1º Cartório")
                .observacao("Observação teste")
                .situacao(situacao)
                .atribuicoes(new HashSet<>(Set.of(atribuicao)))
                .build();

        response = CartorioResponse.fromEntity(cartorio);

        responseMin = CartorioResponseMin.fromEntity(cartorio);
    }

    @Test
    void deveCreateCartorio() throws Exception {
        when(service.create(any(CreateCartorioRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/cartorios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(dto.getId()))
                .andExpect(jsonPath("$.nome").value(dto.getNome()));
    }

    @Test
    void deveListarCartorios() throws Exception {
        Page<CartorioResponseMin> page = new PageImpl<>(List.of(responseMin));
        when(service.listAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/cartorios")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(dto.getId()))
                .andExpect(jsonPath("$.content[0].nome").value(dto.getNome()));
    }

    @Test
    void deveBuscarCartorio() throws Exception {
        when(service.findById(dto.getId())).thenReturn(response);

        mockMvc.perform(get("/api/v1/cartorios/{id}", dto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto.getId()))
                .andExpect(jsonPath("$.nome").value(dto.getNome()));
    }

    @Test
    void deveAtualizarCartorio() throws Exception {
        when(service.update(eq(dto.getId()), any(UpdateCartorioRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/cartorios/{id}", dto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto.getId()));
    }

    @Test
    void deveRemoverCartorio() throws Exception {
        doNothing().when(service).delete(dto.getId());

        mockMvc.perform(delete("/api/v1/cartorios/{id}", dto.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar400QuandoDadosInvalidos() throws Exception {
        CreateCartorioRequest dtoInvalido = CreateCartorioRequest.builder()
                .id(-1) // ID inválido
                .nome("") // Nome vazio
                .situacaoId("SIT_ATIVO")
                .atribuicoesIds(new HashSet<>()) // Sem atribuições
                .build();

        mockMvc.perform(post("/api/v1/cartorios")
                        .characterEncoding("UTF-8")  // Define encoding na requisição
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(content().encoding("UTF-8"))  // Verifica encoding
                .andExpect(jsonPath("$.message").value("Um ou mais campos estão inválidos"))
                .andExpect(jsonPath("$.errors", hasItems(
                        "atribuicoesIds: Pelo menos uma atribuição é obrigatória",
                        "id: ID deve ser maior que zero",
                        "nome: Nome é obrigatório"
                )));
    }
}