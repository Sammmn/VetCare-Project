package com.backend.vetcare.controller;

import com.backend.vetcare.dto.request.AdministradorDTO;
import com.backend.vetcare.dto.response.AdministradorResponseDTO;
import com.backend.vetcare.service.AdministradorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/administradores")
@PreAuthorize("hasRole('ADMIN')")
public class AdministradorController {

    private final AdministradorService administradorService;

    public AdministradorController(AdministradorService administradorService) {
        this.administradorService = administradorService;
    }

    @PostMapping
    public ResponseEntity<AdministradorResponseDTO> cadastrar(@RequestBody @Valid AdministradorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(administradorService.cadastrar(dto));
    }

    @GetMapping
    public ResponseEntity<List<AdministradorResponseDTO>> listar() {
        return ResponseEntity.ok(administradorService.listar());
    }
}
