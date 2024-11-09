package com.dev.ForoEscolar.services.impl;

import com.dev.ForoEscolar.dtos.asistencia.AsistenciaDTO;
import com.dev.ForoEscolar.dtos.tutorlegal.TutorLegalRequestDTO;
import com.dev.ForoEscolar.dtos.tutorlegal.TutorLegalResponseDTO;
import com.dev.ForoEscolar.enums.RoleEnum;
import com.dev.ForoEscolar.exceptions.ApplicationException;
import com.dev.ForoEscolar.mapper.tutorlegal.TutorLegalMapper;
import com.dev.ForoEscolar.model.TutorLegal;
import com.dev.ForoEscolar.model.UpdatedEntities;
import com.dev.ForoEscolar.repository.TutorLegalRepository;
import com.dev.ForoEscolar.services.AsistenciaService;
import com.dev.ForoEscolar.services.TutorLegalService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TutorLegalServiceImpl implements TutorLegalService {

    private final TutorLegalRepository tutorLegalRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TutorLegalMapper tutorLegalMapper;
    private final AsistenciaService asistenciaService;

    @Autowired
    public TutorLegalServiceImpl(TutorLegalRepository tutorLegalRepository, PasswordEncoder passwordEncoder,
                                 AsistenciaService asistenciaService, TutorLegalMapper tutorLegalMapper) {
        this.tutorLegalRepository = tutorLegalRepository;
        this.passwordEncoder = (BCryptPasswordEncoder) passwordEncoder;
        this.tutorLegalMapper = tutorLegalMapper;
        this.asistenciaService = asistenciaService;
    }

    @Transactional
    @Override
    public TutorLegalResponseDTO save(TutorLegalRequestDTO tutorLegalRequestDTO) {
        try {
            if (tutorLegalRepository.findByEmail(tutorLegalRequestDTO.email()).isPresent()) {
                throw new ApplicationException("Tutor legal con email ya existente: " + tutorLegalRequestDTO.email());
            }
            TutorLegal newTutorLegal = tutorLegalMapper.toEntity(tutorLegalRequestDTO);
            newTutorLegal.setContrasena(passwordEncoder.encode(tutorLegalRequestDTO.contrasena()));
            newTutorLegal.setRol(RoleEnum.valueOf("TUTOR"));
            newTutorLegal.setActivo(true);
            newTutorLegal = tutorLegalRepository.save(newTutorLegal);
            return tutorLegalMapper.toResponseDTO(newTutorLegal);
        } catch (ApplicationException e) {
            throw new ApplicationException("Error al guardar el usuario: " + e.getMessage());
        }
    }

    @Override
    public Optional<TutorLegalResponseDTO> findById(Long id) {
        Optional<TutorLegal> tutorLegal = tutorLegalRepository.findById(id);
        return tutorLegal.map(tutorLegalMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public TutorLegalResponseDTO update(TutorLegalRequestDTO tutorLegalRequestDTO) {

        Optional<TutorLegal> tutorLegal = tutorLegalRepository.findById(tutorLegalRequestDTO.id());

        if (tutorLegal.isPresent()) {
            TutorLegal tutorLegalUpdated = (TutorLegal) UpdatedEntities.update(tutorLegal.get(), tutorLegalRequestDTO);
            if(tutorLegalRequestDTO.contrasena() != null){
                validarPassword(tutorLegalRequestDTO.contrasena());
                tutorLegalUpdated.setContrasena(passwordEncoder.encode(tutorLegalRequestDTO.contrasena()));
            }
            return tutorLegalMapper.toResponseDTO(tutorLegalRepository.save(tutorLegalUpdated));
        } else
        {
            throw new ApplicationException("Tutor legal no encontrado");
        }
}


    @Override
    public Iterable<TutorLegalResponseDTO> findAll() {
        List<TutorLegal> tutorLegal = tutorLegalRepository.findAll();
        return tutorLegal.stream().map(tutorLegalMapper::toResponseDTO)
                .toList();
    }

    @Override
    public void deleteById(Long aLong) {
        TutorLegal tutorLegal = tutorLegalRepository.findById(aLong)
                .orElseThrow(() -> new ApplicationException("Tutor legal no encontrado"));
        tutorLegalRepository.delete(tutorLegal);
    }


    @Override
    public Iterable<AsistenciaDTO> findAsistenciasByEstudianteId(Long idTutor, Long gradoId) {
        Optional<TutorLegal> response = tutorLegalRepository.findById(idTutor);
        if (response.isEmpty()) {
            throw new ApplicationException("Tutor legal no encontrado");
        }
        return asistenciaService.getAsistenciasByGradoAndEstudiante(idTutor,gradoId);

    }

    protected void validarPassword(String contrasena) {
        if (contrasena!=null && contrasena.length() < 8) {
            throw new ApplicationException("La contraseña debe tener al menos 8 caracteres");
        }
    }


}
