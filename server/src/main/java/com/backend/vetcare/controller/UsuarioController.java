package com.backend.vetcare.controller;

import com.backend.vetcare.dto.request.AtualizacaoPerfilDTO;
import com.backend.vetcare.dto.request.StatusUsuarioDTO;
import com.backend.vetcare.dto.response.UsuarioResponseDTO;
import com.backend.vetcare.security.UsuarioAutenticado;
import com.backend.vetcare.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    /** ADMIN ou o próprio usuário. */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscar(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id, UsuarioAutenticado.from(jwt)));
    }

    /** ADMIN ou o próprio usuário (nome e telefone). */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id,
                                                        @RequestBody @Valid AtualizacaoPerfilDTO dto,
                                                        @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(usuarioService.atualizarPerfil(id, dto, UsuarioAutenticado.from(jwt)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> alterarStatus(@PathVariable Long id,
                                                            @RequestBody @Valid StatusUsuarioDTO dto,
                                                            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(usuarioService.alterarStatus(id, dto.ativo(), UsuarioAutenticado.from(jwt)));
    }
}
