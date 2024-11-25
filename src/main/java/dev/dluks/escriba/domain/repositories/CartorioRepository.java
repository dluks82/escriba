package dev.dluks.escriba.domain.repositories;

import dev.dluks.escriba.domain.entities.Cartorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartorioRepository extends JpaRepository<Cartorio, Integer> {

    Optional<Cartorio> findByNomeIgnoreCase(String nome);

}