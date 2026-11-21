package com.backend.vetcare.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CpfUtilTest {

    @Test
    void aceitaCpfValido() {
        assertTrue(CpfUtil.isValido("52998224725"));
    }

    @Test
    void rejeitaDigitoVerificadorErrado() {
        assertFalse(CpfUtil.isValido("52998224726"));
    }

    @Test
    void rejeitaSequenciaRepetidaETamanhoErrado() {
        assertFalse(CpfUtil.isValido("11111111111"));
        assertFalse(CpfUtil.isValido("1234"));
        assertFalse(CpfUtil.isValido(null));
    }

    @Test
    void removeMascara() {
        assertEquals("52998224725", CpfUtil.somenteDigitos("529.982.247-25"));
    }
}
