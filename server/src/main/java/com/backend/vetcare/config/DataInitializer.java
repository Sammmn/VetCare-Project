package com.backend.vetcare.config;

import com.backend.vetcare.model.*;
import com.backend.vetcare.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.*;

@Component
public class DataInitializer implements CommandLineRunner {
    private final VeterinarioRepository vetRepo;
    private final TutorRepository tutorRepo;
    private final PetRepository petRepo;
    private final ConsultaRepository consultaRepo;
    private final PasswordEncoder encoder;

    public DataInitializer(VeterinarioRepository vetRepo, TutorRepository tutorRepo, PetRepository petRepo, ConsultaRepository consultaRepo, PasswordEncoder encoder) {
        this.vetRepo = vetRepo;
        this.tutorRepo = tutorRepo;
        this.petRepo = petRepo;
        this.consultaRepo = consultaRepo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (vetRepo.count() > 0 || tutorRepo.count() > 0) return;

        Veterinario v1 = criarVet("Dra. Camila", "111.222.333-44", "camila@vetcare.com", "(11) 99999-0001", "CRMV-SP 41250", "Clínica geral", "180.00");
        Veterinario v2 = criarVet("Dr. Rafael", "222.333.444-55", "rafael@vetcare.com", "(11) 99999-0002", "CRMV-SP 38910", "Dermatologia", "220.00");

        Tutor t1 = criarTutor("Marina Lima", "123.456.789-00", "marina.lima@email.com", "(11) 98765-4321");
        Tutor t2 = criarTutor("Pedro Alves", "234.567.890-11", "pedro.alves@email.com", "(11) 97654-3210");
        Tutor t3 = criarTutor("Carla Dias", "345.678.901-22", "carla.dias@email.com", "(11) 96543-2109");
        Tutor t4 = criarTutor("Lucas Mendes", "456.789.012-33", "lucas.mendes@email.com", "(11) 95432-1098");

        Pet p1 = criarPet("Mel", "Felino", "SRD", 4, 4.2, "Fêmea", "Pequeno", "Amarelo / Branco", LocalDate.of(2021, 5, 10), "Sem alergias.", t1);
        Pet p2 = criarPet("Thor", "Canino", "Golden Retriever", 3, 28.5, "Macho", "Grande", "Dourado", LocalDate.of(2022, 2, 14), "Vacinas em dia.", t2);
        Pet p3 = criarPet("Luna", "Felino", "Siamês", 2, 3.6, "Fêmea", "Pequeno", "Creme", LocalDate.of(2023, 8, 20), "Retorno.", t3);
        Pet p4 = criarPet("Simba", "Canino", "Bulldog Francês", 5, 12.0, "Macho", "Médio", "Tigrado", LocalDate.of(2020, 11, 3), "Dermatite alérgica.", t4);

        LocalDate hoje = LocalDate.now();
        criarConsulta(hoje, LocalTime.of(8, 0), p1, t1, v1, "Consulta de rotina");
        criarConsulta(hoje, LocalTime.of(9, 30), p2, t2, v2, "Vacinação");
        criarConsulta(hoje, LocalTime.of(11, 0), p3, t3, v1, "Retorno");
        criarConsulta(hoje, LocalTime.of(14, 30), p4, t4, v2, "Exame dermatológico");
    }

    private Veterinario criarVet(String nome, String cpf, String email, String tel, String crmv, String esp, String valor) {
        Veterinario v = new Veterinario();
        v.setNome(nome); v.setCpf(cpf); v.setEmail(email); v.setTelefone(tel);
        v.setRegistroProfissional(crmv); v.setEspecialidade(esp);
        v.setValorConsulta(new BigDecimal(valor)); v.setSenha(encoder.encode("123456"));
        return vetRepo.save(v);
    }

    private Tutor criarTutor(String nome, String cpf, String email, String tel) {
        Tutor t = new Tutor();
        t.setNome(nome); t.setCpf(cpf); t.setEmail(email); t.setTelefone(tel);
        t.setSenha(encoder.encode("123456"));
        return tutorRepo.save(t);
    }

    private Pet criarPet(String nome, String esp, String raca, int idade, double peso, String sexo, String porte, String cor, LocalDate nasc, String obs, Tutor tutor) {
        Pet p = new Pet();
        p.setNome(nome); p.setEspecie(esp); p.setRaca(raca); p.setIdade(idade); p.setPeso(peso);
        p.setSexo(sexo); p.setPorte(porte); p.setCor(cor); p.setDataNascimento(nasc); p.setObservacoes(obs); p.setTutor(tutor);
        return petRepo.save(p);
    }

    private void criarConsulta(LocalDate data, LocalTime hora, Pet pet, Tutor tutor, Veterinario vet, String obs) {
        Consulta c = new Consulta();
        c.setInicio(LocalDateTime.of(data, hora)); c.setFim(LocalDateTime.of(data, hora.plusHours(1)));
        c.setStatus(StatusConsulta.AGENDADA); c.setPet(pet); c.setTutor(tutor); c.setVeterinario(vet); c.setObservacoes(obs);
        consultaRepo.save(c);
    }
}