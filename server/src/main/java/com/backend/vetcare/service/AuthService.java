package com.backend.vetcare.service;

import com.backend.vetcare.dto.request.AlterarSenhaDTO;
import com.backend.vetcare.dto.request.LoginDTO;
import com.backend.vetcare.dto.response.LoginResponseDTO;
import com.backend.vetcare.dto.response.UsuarioResponseDTO;
import com.backend.vetcare.exception.AcessoNegadoException;
import com.backend.vetcare.exception.CredenciaisInvalidasException;
import com.backend.vetcare.exception.RegraNegocioException;
import com.backend.vetcare.model.Usuario;
import com.backend.vetcare.repositories.UsuarioRepository;
import com.backend.vetcare.security.JwtService;
import com.backend.vetcare.security.UsuarioAutenticado;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginDTO dto) {
        // Mesma mensagem para e-mail inexistente e senha errada (não revela quais e-mails existem).
        Usuario usuario = usuarioRepository.findByEmail(UsuarioService.normalizarEmail(dto.getEmail()))
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenha())) {
            throw new CredenciaisInvalidasException();
        }
        if (!usuario.isAtivo()) {
            throw new AcessoNegadoException("Usuário inativo. Procure a administração.");
        }

        JwtService.TokenGerado token = jwtService.gerar(usuario);
        return new LoginResponseDTO(token.token(), "Bearer", token.expiraEmSegundos(), new UsuarioResponseDTO(usuario));
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO me(UsuarioAutenticado logado) {
        return new UsuarioResponseDTO(usuarioService.buscarEntidade(logado.id()));
    }

    @Transactional
    public void alterarSenha(AlterarSenhaDTO dto, UsuarioAutenticado logado) {
        Usuario usuario = usuarioService.buscarEntidade(logado.id());

        if (!passwordEncoder.matches(dto.senhaAtual(), usuario.getSenha())) {
            throw new RegraNegocioException("A senha atual está incorreta.");
        }
        if (passwordEncoder.matches(dto.novaSenha(), usuario.getSenha())) {
            throw new RegraNegocioException("A nova senha deve ser diferente da atual.");
        }

        usuario.setSenha(passwordEncoder.encode(dto.novaSenha()));
        usuarioRepository.save(usuario);
    }
}
