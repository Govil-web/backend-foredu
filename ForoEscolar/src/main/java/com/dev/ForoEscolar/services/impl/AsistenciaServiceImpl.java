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
        double anioEscolar= 198.0;
        Asistencia newAsistencia = asistenciaMapper.toEntity(requestDTO);
        newAsistencia.setDiasAnioEscolar(anioEscolar);
        asistenciaRepository.save(newAsistencia);

        //double porcentajeAsistencia= calcularPorcentajeAsistencia(anioEscolar);
        double porcentajeAsistencia= calcularPorcentajeAsistenciaTrimestral(anioEscolar);

        AsistenciaDTO responseDTO = asistenciaMapper.toResponseDto(newAsistencia);
        responseDTO = new AsistenciaDTO(
                responseDTO.id(),
                responseDTO.asistio(),
                //anioEscolar,
                responseDTO.fecha(),
                responseDTO.observaciones(),
                porcentajeAsistencia,
                responseDTO.profesor(),
                responseDTO.estudiante()
        );
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
        return asistenciaRepository.findAll().stream()
                .map(asistenciaMapper::toResponseDto)
                .toList();
    }

    @Override
    public void deleteById(Long aLong) {

    }

    private double calcularPorcentajeAsistenciaTrimestral(double diasAnio) {
        double var= diasAnio/3;
        long totalAsistencias = asistenciaRepository.countByAsistio(true);
        return  (totalAsistencias*100) / var;
    }
}
