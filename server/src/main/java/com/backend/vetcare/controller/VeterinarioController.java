package com.backend.vetcare.controller;

import com.backend.vetcare.dto.request.VeterinarioAtualizacaoDTO;
import com.backend.vetcare.dto.request.VeterinarioDTO;
import com.backend.vetcare.dto.response.VeterinarioResponseDTO;
import com.backend.vetcare.security.UsuarioAutenticado;
import com.backend.vetcare.service.VeterinarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/veterinarios")
public class VeterinarioController {

    private final VeterinarioService veterinarioService;

    public VeterinarioController(VeterinarioService veterinarioService) {
        this.veterinarioService = veterinarioService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VeterinarioResponseDTO> cadastrar(@RequestBody @Valid VeterinarioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(veterinarioService.cadastrar(dto));
    }

    @GetMapping
    public ResponseEntity<List<VeterinarioResponseDTO>> listar(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(veterinarioService.listar(UsuarioAutenticado.from(jwt)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeterinarioResponseDTO> buscarPorId(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(veterinarioService.buscarPorId(id, UsuarioAutenticado.from(jwt)));
    }

    /** ADMIN ou o próprio veterinário. */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO')")
    public ResponseEntity<VeterinarioResponseDTO> atualizar(@PathVariable Long id,
                                                            @RequestBody @Valid VeterinarioAtualizacaoDTO dto,
                                                            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(veterinarioService.atualizar(id, dto, UsuarioAutenticado.from(jwt)));
    }
}
