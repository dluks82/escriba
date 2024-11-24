package dev.dluks.escriba.controllers;

import dev.dluks.escriba.dtos.situacao.CreateSituacaoRequest;
import dev.dluks.escriba.dtos.situacao.SituacaoResponse;
import dev.dluks.escriba.dtos.situacao.SituacaoResponseMin;
import dev.dluks.escriba.dtos.situacao.UpdateSituacaoRequest;
import dev.dluks.escriba.services.SituacaoService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/situacoes")
@RequiredArgsConstructor
@Tag(name = "Situações", description = "API para gerenciamento de situações de cartórios")
public class SituacaoController {

    private final SituacaoService service;

    @Operation(
            summary = "Criar nova situação",
            description = "Cria uma nova situação de cartório no sistema",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Situação criada com sucesso"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Dados inválidos"
                    )
            }
    )
    @PostMapping
    public ResponseEntity<SituacaoResponse> create(
            @RequestBody @Valid CreateSituacaoRequest dto) {
        SituacaoResponse createdSituacao = service.create(dto);

        URI uri = URI.create("/api/v1/situacoes/" + createdSituacao.getId());
        return ResponseEntity.created(uri).body(createdSituacao);
    }

    @Operation(
            summary = "Listar situações",
            description = "Lista todas as situações de cartório cadastradas no sistema",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Situações listadas com sucesso"
                    )
            }
    )
    @GetMapping
    public Page<SituacaoResponseMin> listAll(
            @ParameterObject
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "nome",
                    direction = Sort.Direction.ASC)

            @Parameter(description = "Parâmetros de paginação")
            Pageable pageable) {
        return service.listAll(pageable);
    }

    @Operation(summary = "Buscar situação por ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Situação encontrada"),
                    @ApiResponse(responseCode = "404", description = "Situação não encontrada")
            })
    @GetMapping("/{id}")
    public ResponseEntity<SituacaoResponse> findById(
            @PathVariable
            @Parameter(description = "ID da situação")
            String id) {
        SituacaoResponse situacao = service.findById(id);

        return ResponseEntity.ok(situacao);
    }

    @Operation(summary = "Atualizar situação",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Situação atualizada com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Situação não encontrada"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos")
            })
    @PutMapping("/{id}")
    public ResponseEntity<SituacaoResponse> update(
            @PathVariable String id,
            @RequestBody @Valid UpdateSituacaoRequest dto) {

        SituacaoResponse updatedSituacao = service.update(id, dto);

        return ResponseEntity.ok(updatedSituacao);
    }

    @Operation(summary = "Excluir situação",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Situação excluída com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Situação não encontrada")
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
