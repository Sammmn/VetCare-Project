package com.backend.vetcare.repositories;


import com.backend.vetcare.model.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {

    List<Agenda> findByProfissionalIdAndData(Long profissionalId, LocalDate data);

}
