package com.backend.vetcare.repositories;

import com.backend.vetcare.model.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AgendaRepository extends JpaRepository<Agenda, Long> {

    List<Agenda> findByVeterinarioIdAndData(Long veterinarioId, LocalDate data);
}
