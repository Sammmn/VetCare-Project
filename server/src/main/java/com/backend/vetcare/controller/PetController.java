package com.backend.vetcare.controller;

import com.backend.vetcare.dto.request.PetDTO;
import com.backend.vetcare.dto.response.PetResponseDTO;
import com.backend.vetcare.security.UsuarioAutenticado;
import com.backend.vetcare.service.PetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TUTOR','ADMIN','RECEPCIONISTA')")
    public ResponseEntity<PetResponseDTO> cadastrar(@RequestBody @Valid PetDTO dto, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED).body(petService.cadastrar(dto, UsuarioAutenticado.from(jwt)));
    }

    /** TUTOR: apenas os seus pets. ADMIN/VETERINARIO: todos. */
    @GetMapping
    public ResponseEntity<List<PetResponseDTO>> listar(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(petService.listar(UsuarioAutenticado.from(jwt)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetResponseDTO> buscarPorId(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(petService.buscarPorId(id, UsuarioAutenticado.from(jwt)));
    }

    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<List<PetResponseDTO>> buscarPorTutor(@PathVariable Long tutorId,
                                                               @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(petService.buscarPorTutor(tutorId, UsuarioAutenticado.from(jwt)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TUTOR','ADMIN','RECEPCIONISTA')")
    public ResponseEntity<PetResponseDTO> atualizar(@PathVariable Long id,
                                                    @RequestBody @Valid PetDTO dto,
                                                    @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(petService.atualizar(id, dto, UsuarioAutenticado.from(jwt)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TUTOR','ADMIN','RECEPCIONISTA')")
    public ResponseEntity<Void> deletar(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        petService.deletar(id, UsuarioAutenticado.from(jwt));
        return ResponseEntity.noContent().build();
    }
}
