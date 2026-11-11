package com.backend.vetcare.dto.response;

import com.backend.vetcare.model.Consulta;
import com.backend.vetcare.model.StatusConsulta;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;

@Getter
@Setter
public class ConsultaResponseDTO {

    private Long id;
    private Long profissionalId;
    private String profissionalNome;
    private Long pacienteId;
    private String pacienteNome;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private StatusConsulta status;
    private String diagnostico;
    private String prescricao;
    private String observacoes;

    public ConsultaResponseDTO(Consulta consulta) {
        this.id = consulta.getId();
        this.profissionalId = (consulta.getVeterinario() != null) ? consulta.getVeterinario().getId() : null;
        this.profissionalNome = (consulta.getVeterinario() != null) ? consulta.getVeterinario().getNome() : null;
        this.pacienteId = (consulta.getTutor() != null) ? consulta.getTutor().getId() : null;
        this.pacienteNome = (consulta.getTutor() != null) ? consulta.getTutor().getNome() : null;
        this.inicio = consulta.getInicio();
        this.fim = consulta.getFim();
        this.status = consulta.getStatus();
        this.diagnostico = consulta.getDiagnostico();
        this.prescricao = consulta.getPrescricao();
        this.observacoes = consulta.getObservacoes();
    }
}