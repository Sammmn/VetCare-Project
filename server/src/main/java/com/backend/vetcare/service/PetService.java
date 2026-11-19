package com.backend.vetcare.service;

import com.backend.vetcare.dto.request.PetDTO;
import com.backend.vetcare.dto.response.PetResponseDTO;
import com.backend.vetcare.model.*;
import com.backend.vetcare.repositories.PetRepository;
import com.backend.vetcare.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetService {
    private final PetRepository petRepository;
    private final UsuarioRepository usuarioRepository;

    public PetService(PetRepository petRepository, UsuarioRepository usuarioRepository) {
        this.petRepository = petRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Pet cadastrar(PetDTO dto) {
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome do pet é obrigatório.");
        }
        if (dto.getTutorId() == null) {
            throw new IllegalArgumentException("É obrigatório vincular um tutor responsável ao pet.");
        }

        Usuario usuario = usuarioRepository.findById(dto.getTutorId())
                .orElseThrow(() -> new RuntimeException("Tutor não encontrado com o ID: " + dto.getTutorId()));
        if (!(usuario instanceof Tutor tutor)) {
            throw new IllegalArgumentException("O usuário informado não é um tutor válido.");
        }

        Pet pet = new Pet();
        pet.setNome(dto.getNome().trim());
        pet.setEspecie(dto.getEspecie());
        pet.setRaca(dto.getRaca());
        pet.setIdade(dto.getIdade());
        pet.setPeso(dto.getPeso());
        pet.setSexo(dto.getSexo());
        pet.setPorte(dto.getPorte());
        pet.setCor(dto.getCor());
        pet.setDataNascimento(dto.getDataNascimento());
        pet.setObservacoes(dto.getObservacoes());
        pet.setTutor(tutor);
        return petRepository.save(pet);
    }

    public Pet buscarPorId(Long id) {
        return petRepository.findById(id).orElseThrow(() -> new RuntimeException("Pet não encontrado."));
    }

    public List<Pet> listarTodos() { return petRepository.findAll(); }

    public List<PetResponseDTO> listarTodosFormatado() {
        return petRepository.findAll().stream().map(PetResponseDTO::new).collect(Collectors.toList());
    }

    public List<PetResponseDTO> buscarPorTutor(Long tutorId) {
        return petRepository.findByTutorId(tutorId).stream().map(PetResponseDTO::new).collect(Collectors.toList());
    }

    public Pet atualizar(Long id, PetDTO dto) {
        Pet pet = buscarPorId(id);
        pet.setNome(dto.getNome());
        pet.setEspecie(dto.getEspecie());
        pet.setRaca(dto.getRaca());
        pet.setIdade(dto.getIdade());
        pet.setPeso(dto.getPeso());
        pet.setSexo(dto.getSexo());
        pet.setPorte(dto.getPorte());
        pet.setCor(dto.getCor());
        pet.setDataNascimento(dto.getDataNascimento());
        pet.setObservacoes(dto.getObservacoes());

        if (dto.getTutorId() != null) {
            Usuario usuario = usuarioRepository.findById(dto.getTutorId())
                    .orElseThrow(() -> new RuntimeException("Tutor não encontrado."));
            if (!(usuario instanceof Tutor tutor)) {
                throw new IllegalArgumentException("O usuário informado não é um tutor.");
            }
            pet.setTutor(tutor);
        }
        return petRepository.save(pet);
    }

    public void deletar(Long id) { petRepository.delete(buscarPorId(id)); }
}