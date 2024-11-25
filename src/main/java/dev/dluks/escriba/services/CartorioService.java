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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartorioService {

    private final CartorioRepository repository;
    private final SituacaoService situacaoService;
    private final AtribuicaoService atribuicaoService;

    @Transactional
    public CartorioResponse create(CreateCartorioRequest dto) {
        validateDuplicateId(dto.getId());
        validateDuplicateNome(dto.getNome());

        Cartorio cartorio = toEntity(dto);

        cartorio = repository.save(cartorio);

        return CartorioResponse.fromEntity(cartorio);
    }

    @Transactional
    public CartorioResponse update(Integer id, UpdateCartorioRequest dto) {
        Cartorio cartorioExistente = findOrFail(id);

        if (!cartorioExistente.getNome().equalsIgnoreCase(dto.getNome())) {
            validateDuplicateNome(dto.getNome());
        }

        Cartorio cartorio = Cartorio.builder()
                .id(cartorioExistente.getId())
                .nome(dto.getNome())
                .observacao(dto.getObservacao())
                .situacao(cartorioExistente.getSituacao())
                .atribuicoes(cartorioExistente.getAtribuicoes())
                .build();

        return CartorioResponse.fromEntity(repository.save(cartorio));
    }

    @Transactional
    public CartorioResponse changeSituacao(Integer id, String situacaoId) {
        Cartorio cartorio = findOrFail(id);
        SituacaoResponse situacao = situacaoService.findById(situacaoId);

        cartorio.changeSituacao(Situacao.builder()
                .id(situacao.getId())
                .nome(situacao.getNome())
                .build());

        return CartorioResponse.fromEntity(repository.save(cartorio));
    }

    @Transactional
    public CartorioResponse addAtribuicao(Integer id, String atribuicaoId) {
        Cartorio cartorio = findOrFail(id);
        AtribuicaoResponse atribuicao = atribuicaoService.findById(atribuicaoId);

        cartorio.adicionarAtribuicao(Atribuicao.builder()
                .id(atribuicao.getId())
                .nome(atribuicao.getNome())
                .situacao(atribuicao.isSituacao())
                .build());

        return CartorioResponse.fromEntity(repository.save(cartorio));
    }

    @Transactional
    public CartorioResponse removeAtribuicao(Integer id, String atribuicaoId) {
        Cartorio cartorio = findOrFail(id);
        AtribuicaoResponse atribuicao = atribuicaoService.findById(atribuicaoId);

        cartorio.removerAtribuicao(Atribuicao.builder()
                .id(atribuicao.getId())
                .nome(atribuicao.getNome())
                .situacao(atribuicao.isSituacao())
                .build());

        return CartorioResponse.fromEntity(repository.save(cartorio));
    }

    @Transactional(readOnly = true)
    public CartorioResponse findById(Integer id) {
        return CartorioResponse.fromEntity(findOrFail(id));
    }

    @Transactional(readOnly = true)
    public Page<CartorioResponseMin> listAll(Pageable pageable) {
        Page<Cartorio> cartorios = repository.findAll(pageable);
        return new PageImpl<>(
                cartorios.getContent().stream()
                        .map(CartorioResponseMin::fromEntity)
                        .collect(Collectors.toList()),
                pageable,
                cartorios.getTotalElements()
        );
    }

    @Transactional
    public void delete(Integer id) {
        Cartorio cartorio = findOrFail(id);
        try {
            repository.delete(cartorio);
            repository.flush();
        } catch (Exception e) {
            throw new IntegrityConstraintException("Registro utilizado em outro cadastro.");
        }
    }

    private Cartorio findOrFail(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cartório não encontrado: " + id));
    }

    private void validateDuplicateId(Integer id) {
        if (repository.existsById(id)) {
            throw new DuplicateResourceException("Registro já cadastrado");
        }
    }

    private void validateDuplicateNome(String nome) {
        repository.findByNomeIgnoreCase(nome)
                .ifPresent(cartorio -> {
                    throw new DuplicateResourceException(
                            String.format("Nome já informado no registro com código %d", cartorio.getId())
                    );
                });
    }

    private Cartorio toEntity(CreateCartorioRequest dto) {
        SituacaoResponse situacao = situacaoService.findById(dto.getSituacaoId());

        Set<Atribuicao> atribuicoes = dto.getAtribuicoesIds().stream()
                .map(atribuicaoService::findById)
                .map(atribuicaoDTO -> Atribuicao.builder()
                        .id(atribuicaoDTO.getId())
                        .nome(atribuicaoDTO.getNome())
                        .situacao(atribuicaoDTO.isSituacao())
                        .build()
                )
                .collect(Collectors.toSet());

        return Cartorio.builder()
                .id(dto.getId())
                .nome(dto.getNome())
                .observacao(dto.getObservacao())
                .situacao(Situacao.builder()
                        .id(situacao.getId())
                        .nome(situacao.getNome())
                        .build())
                .atribuicoes(atribuicoes)
                .build();
    }

}
