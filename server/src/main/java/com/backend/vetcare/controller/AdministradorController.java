package com.backend.vetcare.controller;

import com.backend.vetcare.dto.request.AdministradorDTO;
import com.backend.vetcare.model.Administrador;
import com.backend.vetcare.service.AdministradorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/administradores")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AdministradorController {

    private final AdministradorService administradorService;

    public AdministradorController(AdministradorService administradorService) {
        this.administradorService = administradorService;
    }

    @PostMapping
    public ResponseEntity<Administrador> cadastrar(@RequestBody AdministradorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(administradorService.cadastrar(dto));
    }
}
