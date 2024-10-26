package com.dev.ForoEscolar.services.impl;

import com.dev.ForoEscolar.dtos.asistencia.AsistenciaDTO;
import com.dev.ForoEscolar.exceptions.ApplicationException;
import com.dev.ForoEscolar.mapper.asistencia.AsistenciaMapper;
import com.dev.ForoEscolar.model.Asistencia;
import com.dev.ForoEscolar.repository.AsistenciaRepository;
import com.dev.ForoEscolar.services.AsistenciaService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AsistenciaServiceImpl implements AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;

    private final AsistenciaMapper asistenciaMapper;

    @Autowired
    public AsistenciaServiceImpl(AsistenciaRepository asistenciaRepository, AsistenciaMapper asistenciaMapper) {
        this.asistenciaRepository = asistenciaRepository;
        this.asistenciaMapper = asistenciaMapper;
    }


    @Override
    public AsistenciaDTO update(AsistenciaDTO asistenciaDTO) {
        return null;
    }

    @Override
    @Transactional
    public AsistenciaDTO save(AsistenciaDTO requestDTO) {
        Asistencia newAsistencia = asistenciaMapper.toEntity(requestDTO);

        newAsistencia.setContadorTotal(asistenciaRepository.countByContadorClases(true));
        newAsistencia.setAsistenciaAlumno(asistenciaRepository.countByEstudianteIdAndAsistioTrue(requestDTO.getEstudiante()));

        asistenciaRepository.save(newAsistencia);

        double porcentajeAsistencia= porcentajeAsistencia(newAsistencia.getEstudiante().getId());
        AsistenciaDTO responseDTO = asistenciaMapper.toResponseDto(newAsistencia);

        responseDTO.setPorcentajeAsistencia(porcentajeAsistencia);
        return responseDTO;
    }

    @Override
    public Optional<AsistenciaDTO> findById(Long id) {
        Optional<Asistencia> asistenciaDTO = asistenciaRepository.findById(id);

        if(asistenciaDTO.isPresent()){
            return asistenciaDTO.map(asistenciaMapper::toResponseDto);
        }else{
            throw  new ApplicationException("Asistencia no encontrada: " + id);
        }
    }

    @Override
    public Iterable<AsistenciaDTO> findAll() {
        System.out.println("Estoy en el listar service");
        return asistenciaRepository.findAll().stream()
                .map(asistenciaMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long aLong) {

    }


    private double porcentajeAsistencia(long idEstudiante) {

        long diasAsistidos= asistenciaRepository.countByEstudianteIdAndAsistioTrue(idEstudiante);
        long diasDeClases= asistenciaRepository.countByContadorClases(true);
        return (diasAsistidos*100) / diasDeClases;
    }

}
