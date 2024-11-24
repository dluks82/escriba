package dev.dluks.escriba.services;

import dev.dluks.escriba.domain.entities.Atribuicao;
import dev.dluks.escriba.domain.exceptions.DuplicateResourceException;
import dev.dluks.escriba.domain.exceptions.ResourceNotFoundException;
import dev.dluks.escriba.domain.repositories.AtribuicaoRepository;
import dev.dluks.escriba.dtos.atribuicao.AtribuicaoResponse;
import dev.dluks.escriba.dtos.atribuicao.AtribuicaoResponseMin;
import dev.dluks.escriba.dtos.atribuicao.CreateAtribuicaoRequest;
import dev.dluks.escriba.dtos.atribuicao.UpdateAtribuicaoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AtribuicaoService {

    private final AtribuicaoRepository repository;

    @Transactional
    public AtribuicaoResponse create(CreateAtribuicaoRequest dto) {
        validateDuplicatedId(dto.getId());
        validateDuplicateNome(dto.getNome());

        Atribuicao atribuicao = repository.save(
                Atribuicao.builder()
                        .id(dto.getId())
                        .nome(dto.getNome())
                        .situacao(dto.getSituacao() != null ? dto.getSituacao() : true)
                        .build());

        return AtribuicaoResponse.fromEntity(atribuicao);
    }

    @Transactional
    public AtribuicaoResponse update(String id, UpdateAtribuicaoRequest dto) {
        Atribuicao atribuicao = findOrFail(id);

        if (!atribuicao.getNome().equalsIgnoreCase(dto.getNome())) {
            validateDuplicateNome(dto.getNome());
        }

        Atribuicao atribuicaoAtualizada = repository.save(Atribuicao.builder()
                .id(id)
                .nome(dto.getNome())
                .build());

        return AtribuicaoResponse.fromEntity(atribuicaoAtualizada);
    }

    public Page<AtribuicaoResponseMin> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(AtribuicaoResponseMin::fromEntity);
    }

    public AtribuicaoResponse findById(String id) {
        return AtribuicaoResponse.fromEntity(findOrFail(id));
    }

    @Transactional
    public AtribuicaoResponse changeSituacao(String id, Boolean situacao) {
        Atribuicao atribuicao = findOrFail(id);

        if (situacao) {
            atribuicao.activate();
        } else {
            atribuicao.deactivate();
        }

        return AtribuicaoResponse.fromEntity(repository.save(atribuicao));
    }

    @Transactional
    public void delete(String id) {
        Atribuicao atribuicao = findOrFail(id);
        repository.delete(atribuicao);
    }

    private Atribuicao findOrFail(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atribuição não encontrada: " + id));
    }

    private void validateDuplicatedId(String id) {
        if (repository.existsById(id)) {
            throw new DuplicateResourceException("Registro já cadastrado");
        }
    }

    private void validateDuplicateNome(String nome) {
        Optional<Atribuicao> atribuicao = repository.findByNomeIgnoreCase(nome);

        if (atribuicao.isPresent()) {
            throw new DuplicateResourceException(
                    String.format("Nome já informado no registro com código %s", atribuicao.get().getId())
            );
        }
    }

}
