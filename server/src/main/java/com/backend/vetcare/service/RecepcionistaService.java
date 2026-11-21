package com.backend.vetcare.service;

import com.backend.vetcare.dto.request.RecepcionistaDTO;
import com.backend.vetcare.dto.response.UsuarioResponseDTO;
import com.backend.vetcare.model.Recepcionista;
import com.backend.vetcare.repositories.RecepcionistaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecepcionistaService {

    private final RecepcionistaRepository recepcionistaRepository;
    private final UsuarioService usuarioService;

    public RecepcionistaService(RecepcionistaRepository recepcionistaRepository, UsuarioService usuarioService) {
        this.recepcionistaRepository = recepcionistaRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public UsuarioResponseDTO cadastrar(RecepcionistaDTO dto) {
        Recepcionista recepcionista = usuarioService.preencherNovo(new Recepcionista(), dto);
        return new UsuarioResponseDTO(recepcionistaRepository.save(recepcionista));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listar() {
        return recepcionistaRepository.findAll().stream().map(UsuarioResponseDTO::new).toList();
    }
}
