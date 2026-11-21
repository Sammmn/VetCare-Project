package com.backend.vetcare.config;

import com.backend.vetcare.model.Administrador;
import com.backend.vetcare.model.Cargo;
import com.backend.vetcare.repositories.AdministradorRepository;
import com.backend.vetcare.repositories.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Como POST /api/administradores exige um ADMIN logado, o primeiro admin é criado aqui
 * a partir de ADMIN_EMAIL / ADMIN_SENHA (somente se ainda não existir nenhum).
 */
@Component
public class AdminInicializador implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInicializador.class);

    private final UsuarioRepository usuarioRepository;
    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String senha;

    public AdminInicializador(UsuarioRepository usuarioRepository,
                              AdministradorRepository administradorRepository,
                              PasswordEncoder passwordEncoder,
                              @Value("${app.admin.email:}") String email,
                              @Value("${app.admin.senha:}") String senha) {
        this.usuarioRepository = usuarioRepository;
        this.administradorRepository = administradorRepository;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.senha = senha;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (usuarioRepository.existsByCargo(Cargo.ADMIN)) {
            return;
        }
        if (email.isBlank() || senha.isBlank()) {
            log.warn("Nenhum administrador cadastrado. Defina ADMIN_EMAIL e ADMIN_SENHA para criar o primeiro.");
            return;
        }
        if (senha.length() < 8) {
            log.warn("ADMIN_SENHA deve ter pelo menos 8 caracteres. Administrador inicial NÃO criado.");
            return;
        }

        Administrador admin = new Administrador();
        admin.setNome("Administrador");
        admin.setEmail(email.trim().toLowerCase());
        admin.setSenha(passwordEncoder.encode(senha));
        admin.setNivelAcesso("TOTAL");
        administradorRepository.save(admin);
        log.info("Administrador inicial criado: {}", admin.getEmail());
    }
}
