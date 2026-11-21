package com.backend.vetcare.service;

import com.backend.vetcare.dto.request.VeterinarioAtualizacaoDTO;
import com.backend.vetcare.dto.request.VeterinarioDTO;
import com.backend.vetcare.dto.response.VeterinarioResponseDTO;
import com.backend.vetcare.exception.ConflitoException;
import com.backend.vetcare.exception.RecursoNaoEncontradoException;
import com.backend.vetcare.model.Veterinario;
import com.backend.vetcare.repositories.VeterinarioRepository;
import com.backend.vetcare.security.UsuarioAutenticado;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;
    private final UsuarioService usuarioService;

    public VeterinarioService(VeterinarioRepository veterinarioRepository, UsuarioService usuarioService) {
        this.veterinarioRepository = veterinarioRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public VeterinarioResponseDTO cadastrar(VeterinarioDTO dto) {
        String registro = dto.getRegistroProfissional().trim();
        if (veterinarioRepository.existsByRegistroProfissional(registro)) {
            throw new ConflitoException("Registro profissional já cadastrado.");
        }

        Veterinario vet = usuarioService.preencherNovo(new Veterinario(), dto);
        vet.setRegistroProfissional(registro);
        vet.setBiografia(dto.getBiografia());
        vet.setValorConsulta(dto.getValorConsulta());
        return new VeterinarioResponseDTO(veterinarioRepository.save(vet));
    }

    /** ADMIN vê todos; os demais perfis veem apenas veterinários ativos. */
    @Transactional(readOnly = true)
    public List<VeterinarioResponseDTO> listar(UsuarioAutenticado logado) {
        List<Veterinario> lista = logado.isAdmin()
                ? veterinarioRepository.findAll()
                : veterinarioRepository.findByAtivoTrue();
        return lista.stream().map(VeterinarioResponseDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public VeterinarioResponseDTO buscarPorId(Long id, UsuarioAutenticado logado) {
        Veterinario vet = buscarEntidade(id);
        if (!vet.isAtivo() && !logado.isAdmin() && !logado.isUsuario(id)) {
            throw new RecursoNaoEncontradoException("Veterinário não encontrado.");
        }
        return new VeterinarioResponseDTO(vet);
    }

    @Transactional
    public VeterinarioResponseDTO atualizar(Long id, VeterinarioAtualizacaoDTO dto, UsuarioAutenticado logado) {
        UsuarioService.garantirProprioOuAdmin(id, logado);
        Veterinario vet = buscarEntidade(id);
        vet.setNome(dto.nome().trim());
        vet.setTelefone(dto.telefone());
        vet.setBiografia(dto.biografia());
        vet.setValorConsulta(dto.valorConsulta());
        return new VeterinarioResponseDTO(veterinarioRepository.save(vet));
    }

    private Veterinario buscarEntidade(Long id) {
        return veterinarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veterinário não encontrado."));
    }
}
