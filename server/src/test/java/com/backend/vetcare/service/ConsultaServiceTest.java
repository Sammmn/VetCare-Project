package com.backend.vetcare.service;

import com.backend.vetcare.dto.request.ConsultaCriacaoDTO;
import com.backend.vetcare.dto.request.RegistroAtendimentoDTO;
import com.backend.vetcare.dto.response.ConsultaResponseDTO;
import com.backend.vetcare.exception.AcessoNegadoException;
import com.backend.vetcare.exception.ConflitoException;
import com.backend.vetcare.exception.RegraNegocioException;
import com.backend.vetcare.model.Agenda;
import com.backend.vetcare.model.Cargo;
import com.backend.vetcare.model.Consulta;
import com.backend.vetcare.model.Pet;
import com.backend.vetcare.model.StatusConsulta;
import com.backend.vetcare.model.Tutor;
import com.backend.vetcare.model.Veterinario;
import com.backend.vetcare.repositories.AgendaRepository;
import com.backend.vetcare.repositories.ConsultaRepository;
import com.backend.vetcare.repositories.PetRepository;
import com.backend.vetcare.repositories.TutorRepository;
import com.backend.vetcare.security.UsuarioAutenticado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ConsultaServiceTest {

    // "agora" fixo: 2026-11-17 12:00 (UTC)
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-11-17T12:00:00Z"), ZoneId.of("UTC"));

    @Mock ConsultaRepository consultaRepository;
    @Mock AgendaRepository agendaRepository;
    @Mock PetRepository petRepository;
    @Mock TutorRepository tutorRepository;

    ConsultaService service;

    Tutor tutor;
    Veterinario vet;
    Pet pet;

    private final UsuarioAutenticado logadoTutor = new UsuarioAutenticado(1L, Cargo.TUTOR);
    private final UsuarioAutenticado logadoVet = new UsuarioAutenticado(5L, Cargo.VETERINARIO);
    private final UsuarioAutenticado logadoRecepcao = new UsuarioAutenticado(8L, Cargo.RECEPCIONISTA);

    @BeforeEach
    void setUp() {
        service = new ConsultaService(consultaRepository, agendaRepository, petRepository, tutorRepository, CLOCK, 30, 2);

        tutor = new Tutor();
        tutor.setId(1L);
        tutor.setNome("Ana");

        vet = new Veterinario();
        vet.setId(5L);
        vet.setNome("Dr. Bruno");

        pet = new Pet();
        pet.setId(10L);
        pet.setNome("Rex");
        pet.setTutor(tutor);

        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutor));
        when(petRepository.findById(10L)).thenReturn(Optional.of(pet));
        when(consultaRepository.save(any(Consulta.class))).thenAnswer(i -> i.getArgument(0));
        when(agendaRepository.save(any(Agenda.class))).thenAnswer(i -> i.getArgument(0));
    }

    private Agenda agenda(Long id, LocalDate data, LocalTime hora, boolean disponivel) {
        Agenda a = new Agenda();
        a.setId(id);
        a.setData(data);
        a.setHorario(hora);
        a.setDisponivel(disponivel);
        a.setVeterinario(vet);
        return a;
    }

    private Consulta consultaAgendada(LocalDateTime inicio, Agenda agenda) {
        Consulta c = new Consulta();
        c.setId(100L);
        c.setTutor(tutor);
        c.setPet(pet);
        c.setVeterinario(vet);
        c.setAgenda(agenda);
        c.setInicio(inicio);
        c.setFim(inicio.plusMinutes(30));
        c.setStatus(StatusConsulta.AGENDADA);
        return c;
    }

    // ---------------------------------------------------------------- agendar

    @Test
    void agendar_ok_reservaHorarioECalculaFim() {
        Agenda slot = agenda(7L, LocalDate.of(2026, 11, 18), LocalTime.of(9, 0), true);
        when(agendaRepository.buscarParaReserva(7L)).thenReturn(Optional.of(slot));

        ConsultaResponseDTO resp = service.agendar(new ConsultaCriacaoDTO(7L, 10L, "Vacina", null), logadoTutor);

        assertEquals(StatusConsulta.AGENDADA, resp.getStatus());
        assertEquals(LocalDateTime.of(2026, 11, 18, 9, 0), resp.getInicio());
        assertEquals(LocalDateTime.of(2026, 11, 18, 9, 30), resp.getFim());
        assertFalse(slot.isDisponivel());
    }

    @Test
    void agendar_horarioIndisponivel_lancaConflito() {
        Agenda slot = agenda(7L, LocalDate.of(2026, 11, 18), LocalTime.of(9, 0), false);
        when(agendaRepository.buscarParaReserva(7L)).thenReturn(Optional.of(slot));

        assertThrows(ConflitoException.class,
                () -> service.agendar(new ConsultaCriacaoDTO(7L, 10L, null, null), logadoTutor));
    }

    @Test
    void agendar_horarioNoPassado_lancaRegraNegocio() {
        Agenda slot = agenda(7L, LocalDate.of(2026, 11, 17), LocalTime.of(8, 0), true);
        when(agendaRepository.buscarParaReserva(7L)).thenReturn(Optional.of(slot));

        assertThrows(RegraNegocioException.class,
                () -> service.agendar(new ConsultaCriacaoDTO(7L, 10L, null, null), logadoTutor));
    }

    @Test
    void agendar_petDeOutroTutor_lancaRegraNegocio() {
        Tutor outro = new Tutor();
        outro.setId(2L);
        pet.setTutor(outro);

        assertThrows(RegraNegocioException.class,
                () -> service.agendar(new ConsultaCriacaoDTO(7L, 10L, null, null), logadoTutor));
    }

    @Test
    void agendar_petJaTemConsultaNoPeriodo_lancaConflito() {
        Agenda slot = agenda(7L, LocalDate.of(2026, 11, 18), LocalTime.of(9, 0), true);
        when(agendaRepository.buscarParaReserva(7L)).thenReturn(Optional.of(slot));
        when(consultaRepository.existeConflitoParaPet(anyLong(), any(), any(), any())).thenReturn(true);

        assertThrows(ConflitoException.class,
                () -> service.agendar(new ConsultaCriacaoDTO(7L, 10L, null, null), logadoTutor));
    }

    @Test
    void agendar_veterinarioJaOcupadoNoPeriodo_lancaConflito() {
        Agenda slot = agenda(7L, LocalDate.of(2026, 11, 18), LocalTime.of(9, 0), true);
        when(agendaRepository.buscarParaReserva(7L)).thenReturn(Optional.of(slot));
        when(consultaRepository.existeConflitoParaVeterinario(anyLong(), any(), any(), any())).thenReturn(true);

        assertThrows(ConflitoException.class,
                () -> service.agendar(new ConsultaCriacaoDTO(7L, 10L, null, null), logadoTutor));
    }

    // ---------------------------------------------------------------- cancelar

    @Test
    void cancelar_tutorComMenosDeDuasHoras_lancaRegraNegocio() {
        Agenda slot = agenda(7L, LocalDate.of(2026, 11, 17), LocalTime.of(13, 0), false);
        Consulta c = consultaAgendada(LocalDateTime.of(2026, 11, 17, 13, 0), slot);
        when(consultaRepository.findById(100L)).thenReturn(Optional.of(c));

        assertThrows(RegraNegocioException.class, () -> service.cancelar(100L, logadoTutor, "Imprevisto"));
        assertEquals(StatusConsulta.AGENDADA, c.getStatus());
    }

    @Test
    void cancelar_tutorComAntecedencia_liberaHorario() {
        Agenda slot = agenda(7L, LocalDate.of(2026, 11, 17), LocalTime.of(15, 0), false);
        Consulta c = consultaAgendada(LocalDateTime.of(2026, 11, 17, 15, 0), slot);
        when(consultaRepository.findById(100L)).thenReturn(Optional.of(c));

        ConsultaResponseDTO resp = service.cancelar(100L, logadoTutor, "Imprevisto");

        assertEquals(StatusConsulta.CANCELADA, resp.getStatus());
        assertTrue(slot.isDisponivel());
    }

    @Test
    void cancelar_peloVeterinario_mantemHorarioBloqueado() {
        Agenda slot = agenda(7L, LocalDate.of(2026, 11, 17), LocalTime.of(13, 0), false);
        Consulta c = consultaAgendada(LocalDateTime.of(2026, 11, 17, 13, 0), slot);
        when(consultaRepository.findById(100L)).thenReturn(Optional.of(c));

        ConsultaResponseDTO resp = service.cancelar(100L, logadoVet, "Emergência");

        assertEquals(StatusConsulta.CANCELADA, resp.getStatus());
        assertFalse(slot.isDisponivel());
    }

    // ---------------------------------------------------------------- realizar

    @Test
    void realizar_antesDoInicio_lancaRegraNegocio() {
        Consulta c = consultaAgendada(LocalDateTime.of(2026, 11, 17, 15, 0), null);
        when(consultaRepository.findById(100L)).thenReturn(Optional.of(c));

        RegistroAtendimentoDTO dados = new RegistroAtendimentoDTO();
        dados.setDiagnostico("Saudável");

        assertThrows(RegraNegocioException.class, () -> service.realizar(100L, logadoVet, dados));
    }

    @Test
    void realizar_aposInicio_registraAtendimento() {
        Consulta c = consultaAgendada(LocalDateTime.of(2026, 11, 17, 11, 30), null);
        when(consultaRepository.findById(100L)).thenReturn(Optional.of(c));

        RegistroAtendimentoDTO dados = new RegistroAtendimentoDTO();
        dados.setDiagnostico("Saudável");
        dados.setPrescricao("Vitaminas");

        ConsultaResponseDTO resp = service.realizar(100L, logadoVet, dados);

        assertEquals(StatusConsulta.REALIZADA, resp.getStatus());
        assertEquals("Saudável", resp.getDiagnostico());
    }

    // ---------------------------------------------------------------- recepcionista

    @Test
    void agendar_recepcionistaSemTutorId_lancaRegraNegocio() {
        assertThrows(RegraNegocioException.class,
                () -> service.agendar(new ConsultaCriacaoDTO(7L, 10L, null, null), logadoRecepcao));
    }

    @Test
    void agendar_recepcionistaInformandoTutor_agendaParaOTutor() {
        Agenda slot = agenda(7L, LocalDate.of(2026, 11, 18), LocalTime.of(9, 0), true);
        when(agendaRepository.buscarParaReserva(7L)).thenReturn(Optional.of(slot));

        ConsultaResponseDTO resp = service.agendar(new ConsultaCriacaoDTO(7L, 10L, "Vacina", 1L), logadoRecepcao);

        assertEquals(StatusConsulta.AGENDADA, resp.getStatus());
        assertEquals(1L, resp.getTutorId());
        assertFalse(slot.isDisponivel());
    }

    @Test
    void cancelar_recepcionistaNaoTemRegraDeAntecedenciaDoTutor_eLiberaHorario() {
        Agenda slot = agenda(7L, LocalDate.of(2026, 11, 17), LocalTime.of(13, 0), false);
        Consulta c = consultaAgendada(LocalDateTime.of(2026, 11, 17, 13, 0), slot);
        when(consultaRepository.findById(100L)).thenReturn(Optional.of(c));

        ConsultaResponseDTO resp = service.cancelar(100L, logadoRecepcao, "Pedido do tutor");

        assertEquals(StatusConsulta.CANCELADA, resp.getStatus());
        assertTrue(slot.isDisponivel());
    }

    @Test
    void listar_recepcionistaVeTodasMasSemDadosClinicos() {
        Consulta c = consultaAgendada(LocalDateTime.of(2026, 11, 17, 11, 0), null);
        c.setStatus(StatusConsulta.REALIZADA);
        c.setDiagnostico("Gastrite");
        c.setPrescricao("Omeprazol");
        c.setObservacoes("Queixa: vômito");
        when(consultaRepository.findAllByOrderByInicioDesc()).thenReturn(List.of(c));

        List<ConsultaResponseDTO> lista = service.listar(logadoRecepcao, null);

        assertEquals(1, lista.size());
        assertEquals(StatusConsulta.REALIZADA, lista.get(0).getStatus());
        assertEquals(null, lista.get(0).getDiagnostico());
        assertEquals(null, lista.get(0).getPrescricao());
        assertEquals(null, lista.get(0).getObservacoes());
    }

    @Test
    void realizar_recepcionista_eNegado() {
        Consulta c = consultaAgendada(LocalDateTime.of(2026, 11, 17, 11, 30), null);
        when(consultaRepository.findById(100L)).thenReturn(Optional.of(c));

        RegistroAtendimentoDTO dados = new RegistroAtendimentoDTO();
        dados.setDiagnostico("Qualquer");

        assertThrows(AcessoNegadoException.class, () -> service.realizar(100L, logadoRecepcao, dados));
    }
}
