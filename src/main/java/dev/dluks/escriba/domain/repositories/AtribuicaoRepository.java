package dev.dluks.escriba.domain.repositories;

import dev.dluks.escriba.domain.entities.Atribuicao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AtribuicaoRepository extends JpaRepository<Atribuicao, String> {

    boolean existsById(String id);

    boolean existsByNomeIgnoreCase(String nome);

    Optional<Atribuicao> findByNomeIgnoreCase(String nome);

    List<Atribuicao> findBySituacaoTrue();

}
