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
    private Long veterinarioId;
    private String veterinarioNome;
    private Long tutorId;
    private String tutorNome;
    private Long petId;
    private String petNome;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private StatusConsulta status;
    private String motivo;
    private String motivoCancelamento;
    private String diagnostico;
    private String prescricao;
    private String observacoes;

    /** Remove os campos clínicos (uso: perfil RECEPCIONISTA). */
    public ConsultaResponseDTO semDadosClinicos() {
        this.diagnostico = null;
        this.prescricao = null;
        this.observacoes = null;
        return this;
    }

    public ConsultaResponseDTO(Consulta consulta) {
        this.id = consulta.getId();
        this.veterinarioId = consulta.getVeterinario().getId();
        this.veterinarioNome = consulta.getVeterinario().getNome();
        this.tutorId = consulta.getTutor().getId();
        this.tutorNome = consulta.getTutor().getNome();
        this.petId = consulta.getPet().getId();
        this.petNome = consulta.getPet().getNome();
        this.inicio = consulta.getInicio();
        this.fim = consulta.getFim();
        this.status = consulta.getStatus();
        this.motivo = consulta.getMotivo();
        this.motivoCancelamento = consulta.getMotivoCancelamento();
        this.diagnostico = consulta.getDiagnostico();
        this.prescricao = consulta.getPrescricao();
        this.observacoes = consulta.getObservacoes();
    }
}
