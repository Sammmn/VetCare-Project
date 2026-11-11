package com.backend.vetcare.service;

import com.backend.vetcare.dto.request.LoginDTO;
import com.backend.vetcare.dto.request.VeterinarioDTO;
import com.backend.vetcare.dto.response.VeterinarioListDTO;
import com.backend.vetcare.model.Veterinario;
import com.backend.vetcare.repositories.VeterinarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;
    private final PasswordEncoder passwordEncoder;

    public VeterinarioService(VeterinarioRepository veterinarioRepository, PasswordEncoder passwordEncoder) {

        this.veterinarioRepository = veterinarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Veterinario cadastrar(VeterinarioDTO dto) {

        if (veterinarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email já cadastrado.");
        }

        if (veterinarioRepository.existsByCpf(dto.getCpf())) {
            throw new RuntimeException("CPF já cadastrado.");
        }

        if (veterinarioRepository.existsByRegistroProfissional(dto.getRegistroProfissional())) {

            throw new RuntimeException("Registro profissional já cadastrado.");
        }

        Veterinario veterinario = new Veterinario();

        veterinario.setNome(dto.getNome());
        veterinario.setCpf(dto.getCpf());
        veterinario.setEmail(dto.getEmail());
        veterinario.setTelefone(dto.getTelefone());

        veterinario.setRegistroProfissional(dto.getRegistroProfissional());

        veterinario.setBiografia(dto.getBiografia());

        veterinario.setValorConsulta(dto.getValorConsulta());

        veterinario.setSenha(passwordEncoder.encode(dto.getSenha()));

        return veterinarioRepository.save(veterinario);
    }

    public Veterinario buscarPorId(Long id) {

        return veterinarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Médico não encontrado."));
    }

    public List<Veterinario> listarTodos() {
        return veterinarioRepository.findAll();
    }

    public List<Veterinario> buscarPorEspecialidade(Long id) {
        return veterinarioRepository.findByEspecialidadesId(id);
    }

    public List<VeterinarioListDTO> listarTodosFormatado() {
        return veterinarioRepository.findAll().stream()
                .map(VeterinarioListDTO::new)
                .collect(Collectors.toList());
    }

    public Veterinario login(LoginDTO dto) {
        Veterinario veterinario = veterinarioRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new RuntimeException("Médico não encontrado."));
        if (!passwordEncoder.matches(dto.getSenha(), veterinario.getSenha())) {
            throw new RuntimeException("Senha inválida.");
        }
        return veterinario;
    }
}
