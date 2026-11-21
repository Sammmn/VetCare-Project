package com.backend.vetcare.controller;

import com.backend.vetcare.dto.request.AgendaCriacaoDTO;
import com.backend.vetcare.dto.response.HorariosDisponiveisPorDataDTO;
import com.backend.vetcare.security.UsuarioAutenticado;
import com.backend.vetcare.service.AgendaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/agendas")
public class AgendaController {

    private final AgendaService agendaService;

    public AgendaController(AgendaService agendaService) {
        this.agendaService = agendaService;
    }

    /** Abre horários. VETERINARIO: na própria agenda. ADMIN: informa o veterinarioId. */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA','VETERINARIO')")
    public ResponseEntity<HorariosDisponiveisPorDataDTO> criar(@RequestBody @Valid AgendaCriacaoDTO dto,
                                                               @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agendaService.criar(dto, UsuarioAutenticado.from(jwt)));
    }

    /** GET /api/agendas/veterinario/1?data=2026-12-01&apenasDisponiveis=true */
    @GetMapping("/veterinario/{veterinarioId}")
    public ResponseEntity<HorariosDisponiveisPorDataDTO> buscarHorarios(
            @PathVariable Long veterinarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam(defaultValue = "false") boolean apenasDisponiveis) {
        return ResponseEntity.ok(agendaService.buscarHorarios(veterinarioId, data, apenasDisponiveis));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA','VETERINARIO')")
    public ResponseEntity<Void> remover(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        agendaService.remover(id, UsuarioAutenticado.from(jwt));
        return ResponseEntity.noContent().build();
    }
}
