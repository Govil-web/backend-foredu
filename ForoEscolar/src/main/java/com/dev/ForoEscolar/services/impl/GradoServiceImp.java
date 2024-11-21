package com.dev.ForoEscolar.services.impl;

import com.dev.ForoEscolar.dtos.grado.GradoDto;
import com.dev.ForoEscolar.exceptions.ApplicationException;

import com.dev.ForoEscolar.mapper.grado.GradoMapper;
import com.dev.ForoEscolar.model.Estudiante;
import com.dev.ForoEscolar.model.Grado;
import com.dev.ForoEscolar.model.Profesor;
import com.dev.ForoEscolar.repository.GradoRepository;
import com.dev.ForoEscolar.services.GradoService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GradoServiceImp implements GradoService {

    private final GradoRepository gradoRepository;
    private final GradoMapper gradoMapper;

    public GradoServiceImp(GradoRepository gradoRepository, GradoMapper gradoMapper) {
        this.gradoRepository = gradoRepository;
        this.gradoMapper = gradoMapper;
    }
    @Override
    public GradoDto save(GradoDto gradoDto) {
        if(gradoDto==null){
            throw new ApplicationException("El grado no puede estar vacio");
        }
        Profesor profesor= new Profesor();
        profesor.setId(gradoDto.getProfesor());
        Grado grado=Grado.builder()
                .curso(gradoDto.getCurso())
                .aula(gradoDto.getAula())
                .turno(gradoDto.getTurno())
                .materia(gradoDto.getMateria())
                .profesor(profesor)
                .build();
        gradoRepository.save(grado);
        return gradoMapper.toResponseDto(grado);
   }

    @Override
    public Optional<GradoDto> findById(Long id) {
        Optional<Grado> response= gradoRepository.findById(id);
        if(response.isEmpty()){
            throw new ApplicationException("No se encontró el grado");
        }
        Grado grado= response.get();
        return Optional.ofNullable(gradoMapper.toResponseDto(grado));
    }

    @Override
    public Iterable<GradoDto> findAll() {
       List<Grado> grados= gradoRepository.findAll();
        return grados.stream().map(gradoMapper::toResponseDto).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        Optional<Grado> response= gradoRepository.findById(id);
        if(response.isEmpty()){
            throw new ApplicationException("No se encontró el grado");
        }
        gradoRepository.deleteById(id);
    }

    @Override
    public Iterable<GradoDto> findGradosByProfesorId(Long id) {
        List<Grado> grados = gradoRepository.findByProfesorId(id);
        return grados.stream()
                .map(gradoMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<GradoDto> findGradosByTutorId(Long id) {
        List<Grado> grados = gradoRepository.findByEstudiantesTutorId(id);
        return grados.stream()
                .map(gradoMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasActiveAssociations(Long id) {
        Optional<Grado> grado = gradoRepository.findById(id);
        return grado.filter(value -> !value.getEstudiantes().isEmpty() ||
                value.getProfesor() != null).isPresent();
        // Verifica si hay estudiantes o un profesor asignado
    }

    @Override
    public boolean existsById(Long id) {
        return gradoRepository.existsById(id);
    }

    @Override
    public GradoDto update(GradoDto gradoDto) {
        // Verificar que el grado existe
        Grado existingGrado = gradoRepository.findById(gradoDto.getId())
                .orElseThrow(() -> new ApplicationException("Grado no encontrado con ID: " + gradoDto.getId()));

        // Preservar las relaciones existentes
        List<Estudiante> estudiantesActuales = existingGrado.getEstudiantes();

        // Mapear el DTO a la entidad
        Grado gradoToUpdate = gradoMapper.toEntity(gradoDto);

        // Mantener las relaciones existentes
        gradoToUpdate.setEstudiantes(estudiantesActuales);

        // Validar los datos del grado
        validateGradoData(gradoToUpdate);

        // Verificar si el profesor asignado ha cambiado
        if (gradoToUpdate.getProfesor() != null &&
                !gradoToUpdate.getProfesor().getId().equals(existingGrado.getProfesor().getId())) {
            // Aquí podrías agregar lógica adicional para la reasignación de profesor
            validateProfesorAssignment(gradoToUpdate.getProfesor().getId());
        }

        // Actualizar solo los campos permitidos
        existingGrado.setAula(gradoToUpdate.getAula());
        existingGrado.setCurso(gradoToUpdate.getCurso());
        existingGrado.setTurno(gradoToUpdate.getTurno());
        existingGrado.setMateria(gradoToUpdate.getMateria());

        if (gradoToUpdate.getProfesor() != null) {
            existingGrado.setProfesor(gradoToUpdate.getProfesor());
        }

        // Guardar y retornar
        Grado updatedGrado = gradoRepository.save(existingGrado);
        return gradoMapper.toResponseDto(updatedGrado);
    }

    private void validateGradoData(Grado grado) {
        List<String> errors = new ArrayList<>();

        if (grado.getAula() == null) {
            errors.add("El aula es requerida");
        }
        if (grado.getCurso() == null) {
            errors.add("El curso es requerido");
        }
        if (grado.getTurno() == null) {
            errors.add("El turno es requerido");
        }
        if (grado.getMateria() == null) {
            errors.add("La materia es requerida");
        }

        // Validar que no exista otro grado con la misma combinación de aula y turno
        if (gradoRepository.existsByAulaAndTurnoAndIdNot(grado.getAula(), grado.getTurno(), grado.getId())) {
            errors.add("Ya existe un grado asignado para esta aula y turno");
        }

        if (!errors.isEmpty()) {
            throw new ApplicationException("Errores de validación: " + String.join(", ", errors));
        }
    }

    private void validateProfesorAssignment(Long profesorId) {
        // Aquí puedes agregar validaciones específicas para la asignación de profesores
        // Por ejemplo, verificar la carga horaria del profesor, materias que puede dictar, etc.
        // Si algo no es válido, lanzar una excepción

        // Ejemplo de validación básica
        int gradosAsignados = gradoRepository.countByProfesorId(profesorId);
        if (gradosAsignados >= 5) { // Límite ejemplo de 5 grados por profesor
            throw new ApplicationException("El profesor ya tiene el máximo de grados permitidos asignados");
        }
    }
}
