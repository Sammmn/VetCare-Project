package com.backend.vetcare.service;

import com.backend.vetcare.dto.request.PetDTO;
import com.backend.vetcare.dto.response.PetResponseDTO;
import com.backend.vetcare.exception.AcessoNegadoException;
import com.backend.vetcare.exception.ConflitoException;
import com.backend.vetcare.exception.RecursoNaoEncontradoException;
import com.backend.vetcare.exception.RegraNegocioException;
import com.backend.vetcare.model.Pet;
import com.backend.vetcare.model.Tutor;
import com.backend.vetcare.repositories.ConsultaRepository;
import com.backend.vetcare.repositories.PetRepository;
import com.backend.vetcare.repositories.TutorRepository;
import com.backend.vetcare.security.UsuarioAutenticado;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final TutorRepository tutorRepository;
    private final ConsultaRepository consultaRepository;

    public PetService(PetRepository petRepository,
                      TutorRepository tutorRepository,
                      ConsultaRepository consultaRepository) {
        this.petRepository = petRepository;
        this.tutorRepository = tutorRepository;
        this.consultaRepository = consultaRepository;
    }

    @Transactional
    public PetResponseDTO cadastrar(PetDTO dto, UsuarioAutenticado logado) {
        Long tutorId = logado.resolverTutorId(dto.getTutorId());
        Tutor tutor = buscarTutorAtivo(tutorId);

        Pet pet = new Pet();
        preencher(pet, dto);
        pet.setTutor(tutor);
        return new PetResponseDTO(petRepository.save(pet));
    }

    @Transactional(readOnly = true)
    public PetResponseDTO buscarPorId(Long id, UsuarioAutenticado logado) {
        Pet pet = buscarEntidade(id);
        garantirAcessoLeitura(pet, logado);
        return new PetResponseDTO(pet);
    }

    /** ADMIN e VETERINARIO veem todos; TUTOR vê apenas os próprios. */
    @Transactional(readOnly = true)
    public List<PetResponseDTO> listar(UsuarioAutenticado logado) {
        List<Pet> pets = logado.isTutor() ? petRepository.findByTutorId(logado.id()) : petRepository.findAll();
        return pets.stream().map(PetResponseDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public List<PetResponseDTO> buscarPorTutor(Long tutorId, UsuarioAutenticado logado) {
        if (logado.isTutor() && !logado.isUsuario(tutorId)) {
            throw new AcessoNegadoException("Você só pode consultar os seus próprios pets.");
        }
        return petRepository.findByTutorId(tutorId).stream().map(PetResponseDTO::new).toList();
    }

    @Transactional
    public PetResponseDTO atualizar(Long id, PetDTO dto, UsuarioAutenticado logado) {
        Pet pet = buscarEntidade(id);
        garantirDonoOuAdmin(pet, logado);

        preencher(pet, dto);

        // Troca de tutor: somente ADMIN
        if (dto.getTutorId() != null && !dto.getTutorId().equals(pet.getTutor().getId())) {
            if (!logado.isAdmin()) {
                throw new AcessoNegadoException("Somente um administrador pode transferir o pet para outro tutor.");
            }
            pet.setTutor(buscarTutorAtivo(dto.getTutorId()));
        }
        return new PetResponseDTO(petRepository.save(pet));
    }

    @Transactional
    public void deletar(Long id, UsuarioAutenticado logado) {
        Pet pet = buscarEntidade(id);
        garantirDonoOuAdmin(pet, logado);

        if (consultaRepository.existsByPetId(id)) {
            throw new ConflitoException("O pet possui consultas registradas e não pode ser excluído.");
        }
        petRepository.delete(pet);
    }

    // ---------- helpers ----------

    private Pet buscarEntidade(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pet não encontrado."));
    }

    private Tutor buscarTutorAtivo(Long tutorId) {
        Tutor tutor = tutorRepository.findById(tutorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tutor não encontrado."));
        if (!tutor.isAtivo()) {
            throw new RegraNegocioException("O tutor informado está inativo.");
        }
        return tutor;
    }

    private void preencher(Pet pet, PetDTO dto) {
        pet.setNome(dto.getNome().trim());
        pet.setEspecie(dto.getEspecie().trim());
        pet.setRaca(dto.getRaca());
        pet.setIdade(dto.getIdade());
        pet.setPeso(dto.getPeso());
        pet.setSexo(dto.getSexo());
        pet.setPorte(dto.getPorte());
        pet.setCor(dto.getCor());
        pet.setDataNascimento(dto.getDataNascimento());
        pet.setObservacoes(dto.getObservacoes());
    }

    private void garantirAcessoLeitura(Pet pet, UsuarioAutenticado logado) {
        if (logado.isTutor() && !logado.isUsuario(pet.getTutor().getId())) {
            throw new AcessoNegadoException("Este pet não pertence a você.");
        }
    }

    private void garantirDonoOuAdmin(Pet pet, UsuarioAutenticado logado) {
        if (!logado.isEquipeAdministrativa() && !logado.isUsuario(pet.getTutor().getId())) {
            throw new AcessoNegadoException("Este pet não pertence a você.");
        }
    }
}
