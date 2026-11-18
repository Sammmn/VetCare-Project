package com.backend.vetcare.service;

import com.backend.vetcare.dto.request.AdministradorDTO;
import com.backend.vetcare.model.Administrador;
import com.backend.vetcare.repositories.AdministradorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdministradorService {

    private final AdministradorRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AdministradorService(AdministradorRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Administrador cadastrar(AdministradorDTO dto) {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        Administrador administrador = new Administrador();
        administrador.setNome(dto.getNome());
        administrador.setCpf(dto.getCpf());
        administrador.setEmail(dto.getEmail());
        administrador.setTelefone(dto.getTelefone());
        administrador.setNivelAcesso(dto.getNivelAcesso());
        administrador.setSenha(passwordEncoder.encode(dto.getSenha()));

        return repository.save(administrador);
    }
}
