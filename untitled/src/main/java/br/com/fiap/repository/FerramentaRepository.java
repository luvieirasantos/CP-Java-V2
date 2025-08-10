package br.com.fiap.repository;

import br.com.fiap.domain.entity.Ferramenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FerramentaRepository extends JpaRepository<Ferramenta, Long> {}