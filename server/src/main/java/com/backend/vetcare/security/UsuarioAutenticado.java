package com.backend.vetcare.security;

import com.backend.vetcare.exception.AcessoNegadoException;
import com.backend.vetcare.exception.RegraNegocioException;
import com.backend.vetcare.model.Cargo;
import org.springframework.security.oauth2.jwt.Jwt;

/** Dados do usuário logado, extraídos do JWT (nunca do corpo/parâmetros da requisição). */
public record UsuarioAutenticado(Long id, Cargo cargo) {

    public static UsuarioAutenticado from(Jwt jwt) {
        return new UsuarioAutenticado(
                Long.valueOf(jwt.getSubject()),
                Cargo.valueOf(jwt.getClaimAsString("cargo")));
    }

    public boolean isAdmin() {
        return cargo == Cargo.ADMIN;
    }

    public boolean isRecepcionista() {
        return cargo == Cargo.RECEPCIONISTA;
    }

    /** Equipe de balcão/gestão: ADMIN ou RECEPCIONISTA (operam em nome de tutores e veterinários). */
    public boolean isEquipeAdministrativa() {
        return isAdmin() || isRecepcionista();
    }

    public boolean isTutor() {
        return cargo == Cargo.TUTOR;
    }

    public boolean isVeterinario() {
        return cargo == Cargo.VETERINARIO;
    }

    public boolean isUsuario(Long outroId) {
        return id.equals(outroId);
    }

    /**
     * TUTOR sempre age em nome próprio; ADMIN e RECEPCIONISTA precisam informar o tutor.
     * Qualquer outro perfil não pode agir como tutor.
     */
    public Long resolverTutorId(Long informado) {
        if (isTutor()) {
            if (informado != null && !informado.equals(id)) {
                throw new AcessoNegadoException("Você só pode agir em nome do seu próprio cadastro.");
            }
            return id;
        }
        if (isEquipeAdministrativa()) {
            if (informado == null) {
                throw new RegraNegocioException("Informe o tutorId.");
            }
            return informado;
        }
        throw new AcessoNegadoException("Seu perfil não pode realizar esta operação.");
    }

    /** VETERINARIO sempre age em nome próprio; ADMIN e RECEPCIONISTA precisam informar o veterinário. */
    public Long resolverVeterinarioId(Long informado) {
        if (isVeterinario()) {
            if (informado != null && !informado.equals(id)) {
                throw new AcessoNegadoException("Você só pode gerenciar a sua própria agenda.");
            }
            return id;
        }
        if (isEquipeAdministrativa()) {
            if (informado == null) {
                throw new RegraNegocioException("Informe o veterinarioId.");
            }
            return informado;
        }
        throw new AcessoNegadoException("Seu perfil não pode realizar esta operação.");
    }
}
