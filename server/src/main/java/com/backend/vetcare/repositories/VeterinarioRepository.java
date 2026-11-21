package com.backend.vetcare.repositories;

import com.backend.vetcare.model.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    List<Veterinario> findByAtivoTrue();

    boolean existsByRegistroProfissional(String registroProfissional);
}
