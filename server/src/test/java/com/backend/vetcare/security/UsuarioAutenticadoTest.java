package com.backend.vetcare.security;

import com.backend.vetcare.exception.AcessoNegadoException;
import com.backend.vetcare.exception.RegraNegocioException;
import com.backend.vetcare.model.Cargo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioAutenticadoTest {

    private final UsuarioAutenticado tutor = new UsuarioAutenticado(1L, Cargo.TUTOR);
    private final UsuarioAutenticado vet = new UsuarioAutenticado(5L, Cargo.VETERINARIO);
    private final UsuarioAutenticado recepcao = new UsuarioAutenticado(8L, Cargo.RECEPCIONISTA);
    private final UsuarioAutenticado admin = new UsuarioAutenticado(9L, Cargo.ADMIN);

    @Test
    void equipeAdministrativa_eAdminEOuRecepcao() {
        assertTrue(admin.isEquipeAdministrativa());
        assertTrue(recepcao.isEquipeAdministrativa());
        assertFalse(vet.isEquipeAdministrativa());
        assertFalse(tutor.isEquipeAdministrativa());
    }

    @Test
    void resolverTutorId_tutorUsaSeuProprioId() {
        assertEquals(1L, tutor.resolverTutorId(null));
        assertEquals(1L, tutor.resolverTutorId(1L));
        assertThrows(AcessoNegadoException.class, () -> tutor.resolverTutorId(2L));
    }

    @Test
    void resolverTutorId_recepcaoPrecisaInformarOTutor() {
        assertEquals(3L, recepcao.resolverTutorId(3L));
        assertThrows(RegraNegocioException.class, () -> recepcao.resolverTutorId(null));
        assertThrows(AcessoNegadoException.class, () -> vet.resolverTutorId(3L));
    }

    @Test
    void resolverVeterinarioId_recepcaoPrecisaInformarOVeterinario() {
        assertEquals(5L, recepcao.resolverVeterinarioId(5L));
        assertThrows(RegraNegocioException.class, () -> recepcao.resolverVeterinarioId(null));
        assertEquals(5L, vet.resolverVeterinarioId(null));
        assertThrows(AcessoNegadoException.class, () -> vet.resolverVeterinarioId(6L));
        assertThrows(AcessoNegadoException.class, () -> tutor.resolverVeterinarioId(5L));
    }
}
