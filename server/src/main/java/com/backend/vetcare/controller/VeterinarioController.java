package com.backend.vetcare.controller;

import com.backend.vetcare.dto.request.VeterinarioDTO;
import com.backend.vetcare.dto.response.VeterinarioListDTO;
import com.backend.vetcare.dto.response.VeterinarioResponseDTO;
import com.backend.vetcare.model.Veterinario;
import com.backend.vetcare.service.VeterinarioService;
import org.example.vitalink.dto.request.LoginDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicos")
public class VeterinarioController {

    private final VeterinarioService veterinarioService;

    public VeterinarioController(VeterinarioService veterinarioServiceService) {
        this.veterinarioService = veterinarioService;
    }

    @PostMapping
    public ResponseEntity<Veterinario> cadastrar(@RequestBody VeterinarioDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(veterinarioService.cadastrar(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Veterinario> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(veterinarioService.buscarPorId(id)
        );
    }

    @GetMapping("/lista/todos")
    public ResponseEntity<List<VeterinarioListDTO>> listarTodos() {
        return ResponseEntity.ok(veterinarioService.listarTodosFormatado());
		  
    }

    @GetMapping("/lista/especialidade/{id}")
    public ResponseEntity<List<VeterinarioListDTO>> listarPorEspecialidade(@PathVariable Long id) {
        return ResponseEntity.ok(veterinarioService.listarPorEspecialidade(id));
    }

    @GetMapping("/especialidade/{id}")
    public ResponseEntity<List<Veterinario>> buscarPorEspecialidade(@PathVariable Long id) {
        return ResponseEntity.ok(veterinarioService.buscarPorEspecialidade(id)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<VeterinarioResponseDTO> login(
            @RequestBody LoginDTO dto) {Veterinario veterinario = veterinarioService.login(dto);
        return ResponseEntity.ok(new VeterinarioResponseDTO(veterinario));
    }
}
