package com.backend.vetcare.controller;

import com.backend.vetcare.dto.request.AlterarSenhaDTO;
import com.backend.vetcare.dto.request.LoginDTO;
import com.backend.vetcare.dto.request.TutorDTO;
import com.backend.vetcare.dto.response.LoginResponseDTO;
import com.backend.vetcare.dto.response.TutorResponseDTO;
import com.backend.vetcare.dto.response.UsuarioResponseDTO;
import com.backend.vetcare.security.UsuarioAutenticado;
import com.backend.vetcare.service.AuthService;
import com.backend.vetcare.service.TutorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final TutorService tutorService;

    public AuthController(AuthService authService, TutorService tutorService) {
        this.authService = authService;
        this.tutorService = tutorService;
    }

    /** Público. */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    /** Público: autocadastro de tutor. */
    @PostMapping("/registro")
    public ResponseEntity<TutorResponseDTO> registrar(@RequestBody @Valid TutorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tutorService.cadastrar(dto));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> me(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(authService.me(UsuarioAutenticado.from(jwt)));
    }

    @PatchMapping("/senha")
    public ResponseEntity<Void> alterarSenha(@RequestBody @Valid AlterarSenhaDTO dto,
                                             @AuthenticationPrincipal Jwt jwt) {
        authService.alterarSenha(dto, UsuarioAutenticado.from(jwt));
        return ResponseEntity.noContent().build();
    }
}
