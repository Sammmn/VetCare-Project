package com.backend.vetcare.service;

import com.backend.vetcare.dto.request.TutorDTO;
import com.backend.vetcare.dto.response.TutorResponseDTO;
import com.backend.vetcare.exception.AcessoNegadoException;
import com.backend.vetcare.exception.RecursoNaoEncontradoException;
import com.backend.vetcare.model.Tutor;
import com.backend.vetcare.repositories.TutorRepository;
import com.backend.vetcare.security.UsuarioAutenticado;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TutorService {

    private final TutorRepository tutorRepository;
    private final UsuarioService usuarioService;

    public TutorService(TutorRepository tutorRepository, UsuarioService usuarioService) {
        this.tutorRepository = tutorRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public TutorResponseDTO cadastrar(TutorDTO dto) {
        Tutor tutor = usuarioService.preencherNovo(new Tutor(), dto);
        return new TutorResponseDTO(tutorRepository.save(tutor));
    }

    @Transactional(readOnly = true)
    public List<TutorResponseDTO> listar() {
        return tutorRepository.findAll().stream().map(TutorResponseDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public TutorResponseDTO buscarPorId(Long id, UsuarioAutenticado logado) {
        if (logado.isTutor() && !logado.isUsuario(id)) {
            throw new AcessoNegadoException("Você só pode consultar o seu próprio cadastro.");
        }
        Tutor tutor = tutorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tutor não encontrado."));
        return new TutorResponseDTO(tutor);
    }
}
