package com.backend.vetcare.service;

import com.backend.vetcare.dto.request.TutorDTO;
import com.backend.vetcare.dto.response.TutorResponseDTO;
import com.backend.vetcare.model.Tutor;
import com.backend.vetcare.repositories.TutorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TutorService {

    private final TutorRepository tutorRepository;
    private final PasswordEncoder passwordEncoder;

    public TutorService(TutorRepository tutorRepository, PasswordEncoder passwordEncoder) {
        this.tutorRepository = tutorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Tutor cadastrar(TutorDTO dto) {
        if (dto.getEmail() != null && !dto.getEmail().isBlank() && tutorRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email já cadastrado.");
        }

        if (dto.getCpf() != null && tutorRepository.existsByCpf(dto.getCpf())) {
            throw new RuntimeException("CPF já cadastrado.");
        }

        Tutor tutor = new Tutor();
        tutor.setNome(dto.getNome());
        tutor.setCpf(dto.getCpf());
        tutor.setEmail(dto.getEmail());
        tutor.setTelefone(dto.getTelefone());
        String senhaRaw = (dto.getSenha() != null && !dto.getSenha().isBlank()) ? dto.getSenha() : "123456";
        tutor.setSenha(passwordEncoder.encode(senhaRaw));

        return tutorRepository.save(tutor);
    }

    public Tutor buscarPorId(Long id) {
        return tutorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tutor não encontrado."));
    }

    public List<TutorResponseDTO> listarTodos() {
        return tutorRepository.findAll().stream()
                .map(TutorResponseDTO::new)
                .collect(Collectors.toList());
    }

    public List<TutorResponseDTO> buscarIncremental(String termo) {
        return tutorRepository
                .findByNomeContainingIgnoreCaseOrCpfContainingOrTelefoneContaining(termo, termo, termo)
                .stream()
                .map(TutorResponseDTO::new)
                .collect(Collectors.toList());
    }
}
