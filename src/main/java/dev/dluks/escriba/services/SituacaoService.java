package dev.dluks.escriba.services;

import dev.dluks.escriba.domain.entities.Situacao;
import dev.dluks.escriba.domain.exceptions.DuplicateResourceException;
import dev.dluks.escriba.domain.exceptions.ResourceNotFoundException;
import dev.dluks.escriba.domain.repositories.SituacaoRepository;
import dev.dluks.escriba.dtos.CreateSituacaoRequest;
import dev.dluks.escriba.dtos.SituacaoMinDTO;
import dev.dluks.escriba.dtos.UpdateSituacaoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SituacaoService {

    private final SituacaoRepository repository;

    @Transactional
    public Situacao create(CreateSituacaoRequest dto) {
        validateDuplicateId(dto.getId());
        validateDuplicateNome(dto.getNome());

        Situacao situacao = Situacao.builder()
                .id(dto.getId())
                .nome(dto.getNome())
                .build();

        return repository.save(situacao);
    }

    @Transactional
    public Situacao update(String id, UpdateSituacaoRequest dto) {
        Situacao situacao = findByIdOrFail(id);

        if (!situacao.getNome().equalsIgnoreCase(dto.getNome())) {
            validateDuplicateNome(dto.getNome());
        }

        Situacao situacaoAtualizada = Situacao.builder()
                .id(id)
                .nome(dto.getNome())
                .build();

        return repository.save(situacaoAtualizada);
    }

    @Transactional(readOnly = true)
    public Page<SituacaoMinDTO> listAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toMinDTO);
    }

    public Situacao findById(String id) {
        return findByIdOrFail(id);
    }

    @Transactional
    public void delete(String id) {
        repository.delete(findByIdOrFail(id));
    }

    private Situacao findByIdOrFail(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Situação não encontrada com o id: " + id));
    }

    private void validateDuplicateId(String id) {
        if (repository.existsById(id)) {
            throw new DuplicateResourceException("Registro já cadastrado");
        }
    }

    private void validateDuplicateNome(String nome) {
        Optional<Situacao> situacao = repository.findByNomeIgnoreCase(nome);

        if (situacao.isPresent()) {
            throw new DuplicateResourceException(
                    String.format("Nome já informado no registro com código %s", situacao.get().getId())
            );
        }

    }

    private SituacaoMinDTO toMinDTO(Situacao situacao) {
        return SituacaoMinDTO.builder()
                .id(situacao.getId())
                .nome(situacao.getNome())
                .build();
    }

}
