package com.backend.vetcare.repositories;


import com.backend.vetcare.model.Administrador;
import com.backend.vetcare.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdministradorRepository extends JpaRepository<Usuario, Long> {

    Optional<Administrador> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);
}
