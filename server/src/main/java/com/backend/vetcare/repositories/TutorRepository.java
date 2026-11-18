package com.backend.vetcare.repositories;

import com.backend.vetcare.model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TutorRepository extends JpaRepository<Tutor, Long> {

    Optional<Tutor> findByEmail(String email);

    Optional<Tutor> findByCpf(String cpf);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    List<Tutor> findByNomeContainingIgnoreCaseOrCpfContainingOrTelefoneContaining(
            String nome, String cpf, String telefone
    );
}
