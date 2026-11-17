package com.backend.vetcare.controller;

import com.backend.vetcare.dto.request.PetDTO;
import com.backend.vetcare.dto.response.PetResponseDTO;
import com.backend.vetcare.model.Pet;
import com.backend.vetcare.service.PetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pets")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    public ResponseEntity<PetResponseDTO> cadastrar(@RequestBody PetDTO dto) {
        Pet pet = petService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new PetResponseDTO(pet));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(new PetResponseDTO(petService.buscarPorId(id)));
    }

    @GetMapping("/lista/todos")
    public ResponseEntity<List<PetResponseDTO>> listarTodos() {
        return ResponseEntity.ok(petService.listarTodosFormatado());
    }

    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<List<PetResponseDTO>> buscarPorTutor(@PathVariable Long tutorId) {
        return ResponseEntity.ok(petService.buscarPorTutor(tutorId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetResponseDTO> atualizar(@PathVariable Long id, @RequestBody PetDTO dto) {
        Pet pet = petService.atualizar(id, dto);
        return ResponseEntity.ok(new PetResponseDTO(pet));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        petService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
