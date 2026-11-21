package com.backend.vetcare.controller;

import com.backend.vetcare.dto.request.TutorDTO;
import com.backend.vetcare.dto.response.TutorResponseDTO;
import com.backend.vetcare.security.UsuarioAutenticado;
import com.backend.vetcare.service.TutorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tutores")
public class TutorController {

    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public ResponseEntity<TutorResponseDTO> cadastrar(@RequestBody @Valid TutorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tutorService.cadastrar(dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA','VETERINARIO')")
    public ResponseEntity<List<TutorResponseDTO>> listar() {
        return ResponseEntity.ok(tutorService.listar());
    }

    /** ADMIN, VETERINARIO ou o próprio tutor. */
    @GetMapping("/{id}")
    public ResponseEntity<TutorResponseDTO> buscarPorId(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(tutorService.buscarPorId(id, UsuarioAutenticado.from(jwt)));
    }
}
