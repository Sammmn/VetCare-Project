package com.backend.vetcare.service;

import com.backend.vetcare.dto.request.AtualizacaoPerfilDTO;
import com.backend.vetcare.dto.request.UsuarioDTO;
import com.backend.vetcare.dto.response.UsuarioResponseDTO;
import com.backend.vetcare.exception.AcessoNegadoException;
import com.backend.vetcare.exception.ConflitoException;
import com.backend.vetcare.exception.RecursoNaoEncontradoException;
import com.backend.vetcare.exception.RegraNegocioException;
import com.backend.vetcare.model.Cargo;
import com.backend.vetcare.model.StatusConsulta;
import com.backend.vetcare.model.Usuario;
import com.backend.vetcare.repositories.ConsultaRepository;
import com.backend.vetcare.repositories.UsuarioRepository;
import com.backend.vetcare.security.UsuarioAutenticado;
import com.backend.vetcare.util.CpfUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ConsultaRepository consultaRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          ConsultaRepository consultaRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.consultaRepository = consultaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listar() {
        return usuarioRepository.findAll().stream().map(UsuarioResponseDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id, UsuarioAutenticado logado) {
        garantirProprioOuAdmin(id, logado);
        return new UsuarioResponseDTO(buscarEntidade(id));
    }

    @Transactional
    public UsuarioResponseDTO atualizarPerfil(Long id, AtualizacaoPerfilDTO dto, UsuarioAutenticado logado) {
        garantirProprioOuAdmin(id, logado);
        Usuario usuario = buscarEntidade(id);
        usuario.setNome(dto.nome().trim());
        usuario.setTelefone(dto.telefone());
        return new UsuarioResponseDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO alterarStatus(Long id, boolean ativo, UsuarioAutenticado logado) {
        Usuario usuario = buscarEntidade(id);

        if (!ativo) {
            if (logado.isUsuario(id)) {
                throw new RegraNegocioException("Você não pode desativar a própria conta.");
            }
            if (usuario.getCargo() == Cargo.VETERINARIO
                    && consultaRepository.existsByVeterinarioIdAndStatus(id, StatusConsulta.AGENDADA)) {
                throw new ConflitoException(
                        "O veterinário possui consultas agendadas. Cancele-as ou reatribua antes de desativar.");
            }
            if (usuario.getCargo() == Cargo.TUTOR
                    && consultaRepository.existsByTutorIdAndStatus(id, StatusConsulta.AGENDADA)) {
                throw new ConflitoException("O tutor possui consultas agendadas. Cancele-as antes de desativar.");
            }
        }

        usuario.setAtivo(ativo);
        return new UsuarioResponseDTO(usuarioRepository.save(usuario));
    }

    /**
     * Valida unicidade de e-mail/CPF, valida o CPF e preenche os dados comuns de um novo usuário
     * (e-mail em minúsculas, CPF só com dígitos e senha criptografada).
     */
    public <T extends Usuario> T preencherNovo(T usuario, UsuarioDTO dto) {
        String email = normalizarEmail(dto.getEmail());
        String cpf = CpfUtil.somenteDigitos(dto.getCpf());

        if (!CpfUtil.isValido(cpf)) {
            throw new RegraNegocioException("CPF inválido.");
        }
        if (usuarioRepository.existsByEmail(email)) {
            throw new ConflitoException("E-mail já cadastrado.");
        }
        if (usuarioRepository.existsByCpf(cpf)) {
            throw new ConflitoException("CPF já cadastrado.");
        }

        usuario.setNome(dto.getNome().trim());
        usuario.setCpf(cpf);
        usuario.setEmail(email);
        usuario.setTelefone(dto.getTelefone());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setAtivo(true);
        return usuario;
    }

    public static String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    /** Regra de acesso: o próprio usuário ou um ADMIN. */
    public static void garantirProprioOuAdmin(Long idAlvo, UsuarioAutenticado logado) {
        if (!logado.isAdmin() && !logado.isUsuario(idAlvo)) {
            throw new AcessoNegadoException("Você não tem permissão para acessar este usuário.");
        }
    }
}
