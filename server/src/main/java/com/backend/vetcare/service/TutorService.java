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
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome do tutor é obrigatório.");
        }
        if (dto.getCpf() == null || dto.getCpf().isBlank()) {
            throw new IllegalArgumentException("O CPF do tutor é obrigatório.");
        }

        String cpfLimpo = dto.getCpf().trim();
        String emailLimpo = (dto.getEmail() != null && !dto.getEmail().isBlank()) ? dto.getEmail().trim() : null;

        if (emailLimpo != null && tutorRepository.existsByEmail(emailLimpo)) {
            throw new IllegalArgumentException("E-mail já cadastrado para outro tutor.");
        }
        if (tutorRepository.existsByCpf(cpfLimpo)) {
            throw new IllegalArgumentException("CPF já cadastrado para outro tutor.");
        }

        Tutor tutor = new Tutor();
        tutor.setNome(dto.getNome().trim());
        tutor.setCpf(cpfLimpo);
        tutor.setEmail(emailLimpo);
        tutor.setTelefone(dto.getTelefone() != null ? dto.getTelefone().trim() : null);
        String senhaRaw = (dto.getSenha() != null && !dto.getSenha().isBlank()) ? dto.getSenha() : "123456";
        tutor.setSenha(passwordEncoder.encode(senhaRaw));
        return tutorRepository.save(tutor);
    }

    public Tutor buscarPorId(Long id) {
        return tutorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tutor não encontrado."));
    }

    public List<TutorResponseDTO> listarTodos() {
        return tutorRepository.findAll().stream().map(TutorResponseDTO::new).collect(Collectors.toList());
    }

    public List<TutorResponseDTO> buscarIncremental(String termo) {
        return tutorRepository
                .findByNomeContainingIgnoreCaseOrCpfContainingOrTelefoneContaining(termo, termo, termo)
                .stream().map(TutorResponseDTO::new).collect(Collectors.toList());
    }
}