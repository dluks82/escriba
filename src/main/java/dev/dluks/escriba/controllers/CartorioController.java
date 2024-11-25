package dev.dluks.escriba.controllers;

import dev.dluks.escriba.dtos.atribuicao.AtribuicaoRequest;
import dev.dluks.escriba.dtos.cartorio.CartorioResponse;
import dev.dluks.escriba.dtos.cartorio.CartorioResponseMin;
import dev.dluks.escriba.dtos.cartorio.CreateCartorioRequest;
import dev.dluks.escriba.dtos.cartorio.UpdateCartorioRequest;
import dev.dluks.escriba.dtos.situacao.SituacaoRequest;
import dev.dluks.escriba.services.CartorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/cartorios")
@RequiredArgsConstructor
@Tag(name = "Cartórios", description = "APIs para gerenciamento de cartórios")
public class CartorioController {

    private final CartorioService service;


    @Operation(
            summary = "Criar cartório",
            description = "Cria um novo cartório",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Cartório criado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos ou cartório já existente"),
                    @ApiResponse(responseCode = "404", description = "Situação ou atribuição não encontrada")
            })
    @PostMapping
    public ResponseEntity<CartorioResponse> create(@Valid @RequestBody CreateCartorioRequest dto) {
        CartorioResponse response = service.create(dto);
        URI uri = URI.create("/api/v1/cartorios/" + response.getId());
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar cartórios paginados",
            description = "Retorna uma lista paginada com ID e nome dos cartórios")
    public Page<CartorioResponseMin> listAll(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "nome",
                    direction = Sort.Direction.ASC)
            @Parameter(description = "Parâmetros de paginação")
            Pageable pageable) {
        return service.listAll(pageable);
    }

    @Operation(
            summary = "Buscar cartório por ID",
            description = "Retorna um cartório pelo ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cartório encontrado"),
                    @ApiResponse(responseCode = "404", description = "Cartório não encontrado")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<CartorioResponse> findById(
            @PathVariable
            @Parameter(description = "ID do cartório")
            Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(
            summary = "Atualizar cartório",
            description = "Atualiza um cartório pelo ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cartório atualizado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                    @ApiResponse(responseCode = "404", description = "Cartório, situação ou atribuição não encontrada")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<CartorioResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateCartorioRequest dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(
            summary = "Remover cartório",
            description = "Remove um cartório pelo ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Cartório removido com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Cartório não encontrado"),
                    @ApiResponse(responseCode = "400", description = "Cartório não pode ser removido")
            }

    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Adicionar atribuição",
            description = "Adiciona uma atribuição ao cartório",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Atribuição adicionada com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Cartório ou atribuição não encontrada")
            }
    )
    @PutMapping("/{id}/atribuicoes/add")
    public ResponseEntity<CartorioResponse> addAtribuicao(
            @PathVariable Integer id,
            @Valid @RequestBody AtribuicaoRequest atribuicao
    ) {
        return ResponseEntity.ok(service.addAtribuicao(id, atribuicao.getId()));
    }

    @Operation(
            summary = "Remover atribuição",
            description = "Remove uma atribuição do cartório",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Atribuição removida com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Cartório ou atribuição não encontrada")
            }
    )
    @PutMapping("/{id}/atribuicoes/remove")
    public ResponseEntity<CartorioResponse> removeAtribuicao(
            @PathVariable Integer id,
            @Valid @RequestBody AtribuicaoRequest atribuicao
    ) {
        return ResponseEntity.ok(service.removeAtribuicao(id, atribuicao.getId()));
    }

    @Operation(
            summary = "Atualizar situação",
            description = "Atualiza a situação do cartório",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Situação atualizada com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Cartório ou situação não encontrada")
            }
    )
    @PutMapping("/{id}/situacao")
    public ResponseEntity<CartorioResponse> updateSituacao(
            @PathVariable Integer id,
            @Valid @RequestBody SituacaoRequest situacao
    ) {
        return ResponseEntity.ok(service.changeSituacao(id, situacao.getId()));
    }
}