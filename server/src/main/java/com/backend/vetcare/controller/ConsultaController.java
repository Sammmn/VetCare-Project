package com.backend.vetcare.controller;

import com.backend.vetcare.dto.request.CancelamentoDTO;
import com.backend.vetcare.dto.request.ConsultaCriacaoDTO;
import com.backend.vetcare.dto.request.RegistroAtendimentoDTO;
import com.backend.vetcare.dto.response.ConsultaResponseDTO;
import com.backend.vetcare.model.StatusConsulta;
import com.backend.vetcare.security.UsuarioAutenticado;
import com.backend.vetcare.service.ConsultaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {

    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    /** Agenda uma consulta reservando um horário (TUTOR: para si; ADMIN: informa tutorId). */
    @PostMapping
    @PreAuthorize("hasAnyRole('TUTOR','ADMIN','RECEPCIONISTA')")
    public ResponseEntity<ConsultaResponseDTO> agendar(@RequestBody @Valid ConsultaCriacaoDTO dto,
                                                       @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED).body(consultaService.agendar(dto, UsuarioAutenticado.from(jwt)));
    }

    /** Lista as consultas do usuário logado (ADMIN: todas). Filtro opcional ?status=AGENDADA */
    @GetMapping
    public ResponseEntity<List<ConsultaResponseDTO>> listar(@RequestParam(required = false) StatusConsulta status,
                                                            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(consultaService.listar(UsuarioAutenticado.from(jwt), status));
    }

    /** RF10: GET /api/consultas/agenda?data=2026-12-01 (ADMIN também envia &profissionalId=1) */
    @GetMapping("/agenda")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA','VETERINARIO')")
    public ResponseEntity<List<ConsultaResponseDTO>> obterAgenda(
            @RequestParam(required = false) Long profissionalId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(consultaService.obterAgenda(UsuarioAutenticado.from(jwt), profissionalId, data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaResponseDTO> buscarPorId(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(consultaService.buscarPorId(id, UsuarioAutenticado.from(jwt)));
    }

    /** RF11: o veterinário responsável registra o atendimento. */
    @PatchMapping("/{id}/realizar")
    @PreAuthorize("hasRole('VETERINARIO')")
    public ResponseEntity<ConsultaResponseDTO> realizar(@PathVariable Long id,
                                                        @RequestBody @Valid RegistroAtendimentoDTO dados,
                                                        @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(consultaService.realizar(id, UsuarioAutenticado.from(jwt), dados));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ConsultaResponseDTO> cancelar(@PathVariable Long id,
                                                        @RequestBody(required = false) @Valid CancelamentoDTO dto,
                                                        @AuthenticationPrincipal Jwt jwt) {
        String motivo = (dto != null) ? dto.motivo() : null;
        return ResponseEntity.ok(consultaService.cancelar(id, UsuarioAutenticado.from(jwt), motivo));
    }
}
