package com.backend.vetcare.repositories;

import com.backend.vetcare.model.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VeterinarioRepository extends JpaRepository <Veterinario, Long> {

    Optional<Veterinario> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    List<Veterinario> findByEspecialidadesId(Long id);

    boolean existsByRegistroProfissional(String registroProfissional);
}
