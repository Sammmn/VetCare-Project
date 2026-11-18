package com.backend.vetcare.controller;

import com.backend.vetcare.dto.request.TutorDTO;
import com.backend.vetcare.dto.response.TutorResponseDTO;
import com.backend.vetcare.model.Tutor;
import com.backend.vetcare.service.TutorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tutores")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TutorController {

    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @PostMapping
    public ResponseEntity<TutorResponseDTO> cadastrar(@RequestBody TutorDTO dto) {
        Tutor tutor = tutorService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TutorResponseDTO(tutor));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TutorResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(new TutorResponseDTO(tutorService.buscarPorId(id)));
    }

    @GetMapping("/lista/todos")
    public ResponseEntity<List<TutorResponseDTO>> listarTodos() {
        return ResponseEntity.ok(tutorService.listarTodos());
    }

    @GetMapping("/busca")
    public ResponseEntity<List<TutorResponseDTO>> buscarIncremental(@RequestParam String termo) {
        return ResponseEntity.ok(tutorService.buscarIncremental(termo));
    }
}
