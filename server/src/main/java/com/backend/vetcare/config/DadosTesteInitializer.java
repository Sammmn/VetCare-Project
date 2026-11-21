package com.backend.vetcare.config;

import com.backend.vetcare.model.*;
import com.backend.vetcare.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Dados de demonstração. SÓ roda com SEED_ENABLED=true (desenvolvimento) e exige SEED_SENHA no .env:
 * nenhuma senha conhecida fica no código. Tudo é gravado numa única transação.
 */
@Component
@Order(2)
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DadosTesteInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DadosTesteInitializer.class);

    private final VeterinarioRepository vetRepo;
    private final TutorRepository tutorRepo;
    private final RecepcionistaRepository recepcionistaRepo;
    private final PetRepository petRepo;
    private final AgendaRepository agendaRepo;
    private final ConsultaRepository consultaRepo;
    private final PasswordEncoder encoder;
    private final Clock clock;
    private final String senha;
    private final long duracaoMinutos;

    public DadosTesteInitializer(VeterinarioRepository vetRepo, TutorRepository tutorRepo,
                                 RecepcionistaRepository recepcionistaRepo, PetRepository petRepo, AgendaRepository agendaRepo,
                                 ConsultaRepository consultaRepo, PasswordEncoder encoder, Clock clock,
                                 @Value("${app.seed.senha:}") String senha,
                                 @Value("${app.consulta.duracao-minutos:30}") long duracaoMinutos) {
        this.vetRepo = vetRepo;
        this.tutorRepo = tutorRepo;
        this.recepcionistaRepo = recepcionistaRepo;
        this.petRepo = petRepo;
        this.agendaRepo = agendaRepo;
        this.consultaRepo = consultaRepo;
        this.encoder = encoder;
        this.clock = clock;
        this.senha = senha;
        this.duracaoMinutos = duracaoMinutos;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (vetRepo.count() > 0 || tutorRepo.count() > 0) {
            return;
        }
        if (senha == null || senha.length() < 8) {
            log.warn("SEED_ENABLED=true, mas SEED_SENHA não foi definida (mínimo 8 caracteres). Seed ignorado.");
            return;
        }

        // CPFs válidos (apenas dígitos, como o restante do sistema)
        Veterinario v1 = criarVet("Dra. Camila", "52998224725", "camila@vetcare.com", "(11) 99999-0001", "CRMV-SP 41250", "Clínica geral", "180.00");
        Veterinario v2 = criarVet("Dr. Rafael", "11144477735", "rafael@vetcare.com", "(11) 99999-0002", "CRMV-SP 38910", "Dermatologia", "220.00");

        Recepcionista recepcao = new Recepcionista();
        recepcao.setNome("Mariana Costa");
        recepcao.setCpf("15350946056");
        recepcao.setEmail("recepcao@vetcare.com");
        recepcao.setTelefone("(11) 99999-0003");
        recepcao.setSenha(encoder.encode(senha));
        recepcionistaRepo.save(recepcao);

        Tutor t1 = criarTutor("Marina Lima", "39053344705", "marina.lima@email.com", "(11) 98765-4321");
        Tutor t2 = criarTutor("Pedro Alves", "12345678909", "pedro.alves@email.com", "(11) 97654-3210");
        Tutor t3 = criarTutor("Carla Dias", "98765432100", "carla.dias@email.com", "(11) 96543-2109");
        Tutor t4 = criarTutor("Lucas Mendes", "11122233396", "lucas.mendes@email.com", "(11) 95432-1098");

        Pet p1 = criarPet("Mel", "Felino", "SRD", 4, 4.2, "Fêmea", LocalDate.of(2021, 5, 10), "Sem alergias.", t1);
        Pet p2 = criarPet("Thor", "Canino", "Golden Retriever", 3, 28.5, "Macho", LocalDate.of(2022, 2, 14), "Vacinas em dia.", t2);
        Pet p3 = criarPet("Luna", "Felino", "Siamês", 2, 3.6, "Fêmea", LocalDate.of(2023, 8, 20), "Retorno.", t3);
        Pet p4 = criarPet("Simba", "Canino", "Bulldog Francês", 5, 12.0, "Macho", LocalDate.of(2020, 11, 3), "Dermatite alérgica.", t4);

        LocalDate hoje = LocalDate.now(clock);
        Agenda[] agendaV1 = new Agenda[7];
        Agenda[] agendaV2 = new Agenda[7];
        for (int h = 8; h <= 14; h++) {
            agendaV1[h - 8] = criarSlot(v1, hoje, LocalTime.of(h, 0));
            agendaV2[h - 8] = criarSlot(v2, hoje, LocalTime.of(h, 0));
        }

        // Consultas de exemplo: ficam vinculadas ao horário (que passa a ficar indisponível)
        criarConsulta(agendaV1[0], p1, t1, "Consulta de rotina");
        criarConsulta(agendaV2[1], p2, t2, "Vacinação");
        criarConsulta(agendaV1[3], p3, t3, "Retorno");
        criarConsulta(agendaV2[6], p4, t4, "Dermatite");

        log.info("Dados de demonstração criados (2 veterinários, 1 recepcionista, 4 tutores, 4 pets).");
    }

    private Veterinario criarVet(String nome, String cpf, String email, String tel, String crmv, String bio, String valor) {
        Veterinario v = new Veterinario();
        v.setNome(nome);
        v.setCpf(cpf);
        v.setEmail(email);
        v.setTelefone(tel);
        v.setSenha(encoder.encode(senha));
        v.setRegistroProfissional(crmv);
        v.setBiografia(bio);
        v.setValorConsulta(new BigDecimal(valor));
        return vetRepo.save(v);
    }

    private Tutor criarTutor(String nome, String cpf, String email, String tel) {
        Tutor t = new Tutor();
        t.setNome(nome);
        t.setCpf(cpf);
        t.setEmail(email);
        t.setTelefone(tel);
        t.setSenha(encoder.encode(senha));
        return tutorRepo.save(t);
    }

    private Pet criarPet(String nome, String especie, String raca, int idade, double peso, String sexo,
                         LocalDate nascimento, String obs, Tutor tutor) {
        Pet p = new Pet();
        p.setNome(nome);
        p.setEspecie(especie);
        p.setRaca(raca);
        p.setIdade(idade);
        p.setPeso(peso);
        p.setSexo(sexo);
        p.setDataNascimento(nascimento);
        p.setObservacoes(obs);
        p.setTutor(tutor);
        return petRepo.save(p);
    }

    private Agenda criarSlot(Veterinario vet, LocalDate data, LocalTime hora) {
        Agenda a = new Agenda();
        a.setVeterinario(vet);
        a.setData(data);
        a.setHorario(hora);
        a.setDisponivel(true);
        return agendaRepo.save(a);
    }

    private void criarConsulta(Agenda slot, Pet pet, Tutor tutor, String motivo) {
        LocalDateTime inicio = LocalDateTime.of(slot.getData(), slot.getHorario());

        Consulta c = new Consulta();
        c.setAgenda(slot);
        c.setVeterinario(slot.getVeterinario());
        c.setTutor(tutor);
        c.setPet(pet);
        c.setMotivo(motivo);
        c.setInicio(inicio);
        c.setFim(inicio.plusMinutes(duracaoMinutos));
        if (inicio.isBefore(LocalDateTime.now(clock))) {
            // horário que já passou: já nasce finalizada, para não ficar "agendada" no passado
            c.setStatus(StatusConsulta.REALIZADA);
            c.setDiagnostico("Atendimento de exemplo (dados de demonstração).");
        } else {
            c.setStatus(StatusConsulta.AGENDADA);
        }
        consultaRepo.save(c);

        slot.setDisponivel(false);
        agendaRepo.save(slot);
    }
}
