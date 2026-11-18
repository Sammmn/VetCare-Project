package com.backend.vetcare.controller;

import com.backend.vetcare.dto.request.LoginDTO;
import com.backend.vetcare.dto.request.VeterinarioDTO;
import com.backend.vetcare.dto.response.VeterinarioListDTO;
import com.backend.vetcare.dto.response.VeterinarioResponseDTO;
import com.backend.vetcare.model.Veterinario;
import com.backend.vetcare.service.VeterinarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicos")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class VeterinarioController {

    private final VeterinarioService veterinarioService;

    public VeterinarioController(VeterinarioService veterinarioService) {
        this.veterinarioService = veterinarioService;
    }

    @PostMapping
    public ResponseEntity<Veterinario> cadastrar(@RequestBody VeterinarioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(veterinarioService.cadastrar(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Veterinario> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(veterinarioService.buscarPorId(id));
    }

    @GetMapping("/lista/todos")
    public ResponseEntity<List<VeterinarioListDTO>> listarTodos() {
        return ResponseEntity.ok(veterinarioService.listarTodosFormatado());
    }

    @GetMapping("/lista/especialidade/{especialidade}")
    public ResponseEntity<List<VeterinarioListDTO>> listarPorEspecialidade(@PathVariable String especialidade) {
        return ResponseEntity.ok(veterinarioService.listarPorEspecialidade(especialidade));
    }

    @GetMapping("/especialidade/{especialidade}")
    public ResponseEntity<List<Veterinario>> buscarPorEspecialidade(@PathVariable String especialidade) {
        return ResponseEntity.ok(veterinarioService.buscarPorEspecialidade(especialidade));
    }

    @PostMapping("/login")
    public ResponseEntity<VeterinarioResponseDTO> login(@RequestBody LoginDTO dto) {
        Veterinario veterinario = veterinarioService.login(dto);
        return ResponseEntity.ok(new VeterinarioResponseDTO(veterinario));
    }
}
