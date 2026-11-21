package com.backend.vetcare.dto.response;

import com.backend.vetcare.model.Agenda;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HorarioDisponivelDTO {

    /** Use este id em POST /api/consultas (campo agendaId). */
    private Long id;
    private String horario;
    private boolean disponivel;

    public HorarioDisponivelDTO(Agenda agenda) {
        this.id = agenda.getId();
        this.horario = agenda.getHorario().toString();
        this.disponivel = agenda.isDisponivel();
    }
}
