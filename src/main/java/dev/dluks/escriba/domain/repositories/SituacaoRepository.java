package dev.dluks.escriba.domain.repositories;

import dev.dluks.escriba.domain.entities.Situacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SituacaoRepository extends JpaRepository<Situacao, String> {

    boolean existsById(String id);

    boolean existsByNomeIgnoreCase(String nome);

    Optional<Situacao> findByNomeIgnoreCase(String nome);

}
