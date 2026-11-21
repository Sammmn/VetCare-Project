package com.backend.vetcare.repositories;

import com.backend.vetcare.model.Consulta;
import com.backend.vetcare.model.StatusConsulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    @Query("""
        SELECT c FROM Consulta c
        WHERE c.veterinario.id = :veterinarioId
          AND c.inicio >= :inicio
          AND c.inicio <= :fim
        ORDER BY c.inicio ASC
    """)
    List<Consulta> buscarAgendaPorVeterinarioEPeriodo(
            @Param("veterinarioId") Long veterinarioId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

    /** Já existe consulta com o status informado para o pet que se sobreponha ao intervalo? */
    @Query("""
        SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
        FROM Consulta c
        WHERE c.pet.id = :petId
          AND c.status = :status
          AND c.inicio < :fim
          AND c.fim > :inicio
    """)
    boolean existeConflitoParaPet(
            @Param("petId") Long petId,
            @Param("status") StatusConsulta status,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

    /** Já existe consulta com o status informado para o veterinário que se sobreponha ao intervalo? */
    @Query("""
        SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
        FROM Consulta c
        WHERE c.veterinario.id = :veterinarioId
          AND c.status = :status
          AND c.inicio < :fim
          AND c.fim > :inicio
    """)
    boolean existeConflitoParaVeterinario(
            @Param("veterinarioId") Long veterinarioId,
            @Param("status") StatusConsulta status,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

    List<Consulta> findAllByOrderByInicioDesc();

    List<Consulta> findByTutorIdOrderByInicioDesc(Long tutorId);

    List<Consulta> findByVeterinarioIdOrderByInicioDesc(Long veterinarioId);

    boolean existsByPetId(Long petId);

    boolean existsByAgendaId(Long agendaId);

    boolean existsByVeterinarioIdAndStatus(Long veterinarioId, StatusConsulta status);

    boolean existsByTutorIdAndStatus(Long tutorId, StatusConsulta status);
}
