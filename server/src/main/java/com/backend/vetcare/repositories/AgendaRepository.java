package com.backend.vetcare.repositories;

import com.backend.vetcare.model.Agenda;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgendaRepository extends JpaRepository<Agenda, Long> {

    List<Agenda> findByVeterinarioIdAndDataOrderByHorarioAsc(Long veterinarioId, LocalDate data);

    boolean existsByVeterinarioIdAndDataAndHorario(Long veterinarioId, LocalDate data, LocalTime horario);

    /** Trava a linha para evitar que dois tutores reservem o mesmo horário ao mesmo tempo. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Agenda a WHERE a.id = :id")
    Optional<Agenda> buscarParaReserva(@Param("id") Long id);
}
