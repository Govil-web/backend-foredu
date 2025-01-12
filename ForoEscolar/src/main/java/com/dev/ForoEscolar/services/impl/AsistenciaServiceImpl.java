package com.dev.ForoEscolar.services.impl;

import com.dev.ForoEscolar.dtos.asistencia.AsistenciaDTO;
import com.dev.ForoEscolar.dtos.asistencia.AsistenciaRequestDto;
import com.dev.ForoEscolar.exceptions.ApplicationException;
import com.dev.ForoEscolar.mapper.asistencia.AsistenciaMapper;
import com.dev.ForoEscolar.model.Asistencia;
import com.dev.ForoEscolar.model.Estudiante;
import com.dev.ForoEscolar.model.Grado;
import com.dev.ForoEscolar.repository.AsistenciaRepository;
import com.dev.ForoEscolar.repository.EstudianteRepository;
import com.dev.ForoEscolar.repository.GradoRepository;
import com.dev.ForoEscolar.services.AsistenciaService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AsistenciaServiceImpl implements AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final GradoRepository gradoRepository;
    private EstudianteRepository estudianteRepository;
    private final AsistenciaMapper asistenciaMapper;

    @Autowired
    public AsistenciaServiceImpl(AsistenciaRepository asistenciaRepository, AsistenciaMapper asistenciaMapper,
                                 GradoRepository gradoRepository, EstudianteRepository estudianteRepository) {
        this.asistenciaRepository = asistenciaRepository;
        this.asistenciaMapper = asistenciaMapper;
        this.gradoRepository = gradoRepository;
        this.estudianteRepository = estudianteRepository;
    }


    @Override
    @Transactional
    public void update(AsistenciaRequestDto requestDto) {

        Optional<Asistencia> response = asistenciaRepository.findById(requestDto.getId());
        if (response.isPresent()) {
            Asistencia asistencia= response.get();
            asistencia.setAsistio(requestDto.isAsistio());
            if(!requestDto.isAsistio()){
                asistencia.setAsistenciaAlumno(asistencia.getAsistenciaAlumno()-1);
            }else if(requestDto.isAsistio()){
                asistencia.setAsistenciaAlumno(asistencia.getAsistenciaAlumno()+1);
            }
            asistencia.setObservaciones(requestDto.getJustificativos());
            asistenciaRepository.save(asistencia);
        }

    }

    @Override
    @Transactional
    public AsistenciaDTO save(AsistenciaRequestDto requestDTO) {

        Grado grado = gradoRepository.findById(requestDTO.getGrado()).orElse(null);

        if (grado == null) {
            throw new ApplicationException("Grado no encontrado");
        }
        Asistencia newAsistencia = asistenciaMapper.toEntity(requestDTO);
        newAsistencia = verificarAsistenciaDelGrado(newAsistencia, newAsistencia.getFecha(), grado);
        asistenciaRepository.save(newAsistencia);

        return asistenciaMapper.toResponseDto(newAsistencia);

    }

    @Override
    public Optional<AsistenciaDTO> findById(Long id) {
        Optional<Asistencia> asistenciaDTO = asistenciaRepository.findById(id);

        if (asistenciaDTO.isPresent()) {
            return asistenciaDTO.map(asistenciaMapper::toResponseDto);
        } else {
            throw new ApplicationException("Asistencia no encontrada: " + id);
        }
    }

    @Override
    public Iterable<AsistenciaDTO> findAll() {
        return asistenciaRepository.findAll().stream()
                .map(asistenciaMapper::toResponseDto)
                .toList();
    }

    @Override
    public void deleteById(Long asistenciaId) {
        asistenciaRepository.deleteById(asistenciaId);
    }

    // --------------------------- CONSULTAS ---------------------------
    @Override
    public Iterable<AsistenciaDTO> getAsistenciasByEstudianteID(Long estudianteId) {
        List<Asistencia> asistencias = asistenciaRepository.findByEstudianteId(estudianteId);
        return asistencias.stream().map(asistenciaMapper::toResponseDto)
                .toList();
    }
    @Override
    public Iterable<AsistenciaDTO> getAsistenciasByGradoAndEstudiante(Long tutorId, Long gradoId) {

        Optional<Grado> grado = gradoRepository.findById(gradoId);
        if (grado.isEmpty()) {
            throw new ApplicationException("Grado no encontrado");
        }
        List<Estudiante> estudiantes = estudianteRepository.findByTutorId(tutorId);
        return estudiantes.stream().
                flatMap(estudiante -> asistenciaRepository.findByEstudianteIdAndGradoId(estudiante.getId(), gradoId).stream())
                .map(asistenciaMapper::toResponseDto)
                .toList();
    }

    @Override
    public Iterable<AsistenciaDTO> getAsistenciasByGrado(Long gradoId) {

        Optional<Grado> grado = gradoRepository.findById(gradoId);
        if (grado.isPresent()) {
            List<Asistencia> asistencias = asistenciaRepository.findByGradoId(gradoId);
            return asistencias.stream()
                    .map(asistenciaMapper::toResponseDto).toList();
        }
        throw new ApplicationException("Grado no encontrado");
    }

    @Override
    public List<AsistenciaDTO> getByFechaBeetweenAndGrado(Long gradoId, LocalDate fechaDesde, LocalDate fechaHasta) {

        Optional<Grado> grado = gradoRepository.findById(gradoId);
        if (grado.isEmpty()) {
            throw new ApplicationException("Grado no encontrado");
        }
        List<Asistencia> asistencias = asistenciaRepository.findByFechaBetweenAndGradoId(fechaDesde, fechaHasta, gradoId);

        if (asistencias.isEmpty()) {
            throw new ApplicationException("No se encontraron asistencias en el rango de fechas");
        }
        return asistencias.stream().map(asistenciaMapper::toResponseDto)
                .toList();
    }
    @Override
    public AsistenciaDTO save(AsistenciaDTO requestDTO) {
        return null;
    }

    private Asistencia verificarAsistenciaDelGrado(Asistencia asistencia, LocalDate fecha, Grado grado) {
        asistencia.setContadorClases(true);
        asistencia.setAsistenciaAlumno(asistenciaRepository.countByEstudianteIdAndAsistioTrue(asistencia.getEstudiante().getId()));
        if (!asistenciaRepository.existsByFechaAndGradoId(fecha, grado.getId())) {
            asistencia.actualizarContadorGrado();
        }
        asistencia.setContadorTotal(grado.getContador());

        return asistencia;
    }

}
