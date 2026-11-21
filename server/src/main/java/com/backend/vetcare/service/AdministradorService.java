package com.backend.vetcare.service;

import com.backend.vetcare.dto.request.AdministradorDTO;
import com.backend.vetcare.dto.response.AdministradorResponseDTO;
import com.backend.vetcare.model.Administrador;
import com.backend.vetcare.repositories.AdministradorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdministradorService {

    private final AdministradorRepository repository;
    private final UsuarioService usuarioService;

    public AdministradorService(AdministradorRepository repository, UsuarioService usuarioService) {
        this.repository = repository;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public AdministradorResponseDTO cadastrar(AdministradorDTO dto) {
        Administrador admin = usuarioService.preencherNovo(new Administrador(), dto);
        admin.setNivelAcesso(dto.getNivelAcesso());
        return new AdministradorResponseDTO(repository.save(admin));
    }

    @Transactional(readOnly = true)
    public List<AdministradorResponseDTO> listar() {
        return repository.findAll().stream().map(AdministradorResponseDTO::new).toList();
    }
}
