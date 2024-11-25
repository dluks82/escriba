package dev.dluks.escriba.controllers;

import dev.dluks.escriba.dtos.atribuicao.AtribuicaoResponse;
import dev.dluks.escriba.dtos.atribuicao.AtribuicaoResponseMin;
import dev.dluks.escriba.dtos.atribuicao.CreateAtribuicaoRequest;
import dev.dluks.escriba.dtos.atribuicao.UpdateAtribuicaoRequest;
import dev.dluks.escriba.services.AtribuicaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/atribuicoes")
@RequiredArgsConstructor
@Tag(name = "Atribuições", description = "APIs para gerenciamento de atribuições de cartório")
public class AtribuicaoController {

    private final AtribuicaoService service;

    @PostMapping
    @Operation(summary = "Criar atribuição",
            description = "Cria uma nova atribuição",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Atribuição criada com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos ou atribuição já existente")
            })
    public ResponseEntity<AtribuicaoResponse> create(@Valid @RequestBody CreateAtribuicaoRequest dto) {
        AtribuicaoResponse response = service.create(dto);

        URI uri = URI.create("/api/v1/atribuicoes/" + response.getId());

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar atribuições",
            description = "Retorna uma lista paginada de todas as atribuições")
    public Page<AtribuicaoResponseMin> listAll(
            @ParameterObject
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "nome",
                    direction = Sort.Direction.ASC)

            @Parameter(description = "Parâmetros de paginação")
            Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar atribuição",
            description = "Retorna uma atribuição pelo ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Atribuição encontrada"),
                    @ApiResponse(responseCode = "404", description = "Atribuição não encontrada")
            })
    public AtribuicaoResponse findById(
            @PathVariable
            @Parameter(description = "ID da atribuição")
            String id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar atribuição",
            description = "Atualiza uma atribuição",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Atribuição atualizada"),
                    @ApiResponse(responseCode = "404", description = "Atribuição não encontrada"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos")
            })
    public AtribuicaoResponse update(
            @PathVariable String id,
            @Valid @RequestBody UpdateAtribuicaoRequest dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}/situacao")
    @Operation(summary = "Alterar situação da atribuição",
            description = "Ativa ou desativa uma atribuição",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Situação alterada com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Atribuição não encontrada")
            })
    public AtribuicaoResponse changeSituacao(
            @PathVariable String id,
            @RequestParam
            @Parameter(description = "Nova situação (true=ativo, false=inativo)")
            boolean situacao) {
        return service.changeSituacao(id, situacao);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remover atribuição",
            description = "Remove uma atribuição",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Atribuição removida"),
                    @ApiResponse(responseCode = "404", description = "Atribuição não encontrada"),
                    @ApiResponse(responseCode = "400", description = "Atribuição não pode ser removida")
            })
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}