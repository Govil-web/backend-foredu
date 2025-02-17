package com.foroescolar.mapper.grado;

import com.foroescolar.dtos.grado.GradoDto;
import com.foroescolar.model.Grado;
import com.foroescolar.model.Profesor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public abstract class GradoMapper {

    @Mapping(source = "profesor", target = "profesor", qualifiedByName = "profesorToLong")
    @Mapping(source = "profesor.nombre", target = "profesorNombre")
   public abstract GradoDto toResponseDto(Grado grado);

    @Mapping(source = "profesor", target = "profesor", qualifiedByName = "longToProfesor")
    public abstract Grado toEntity(GradoDto gradoDto);

    @Named("longToProfesor")
    protected Profesor longToProfesor(Long id) {
        if (id == null) {
            return null;
        }
        Profesor profesor = new Profesor();
        profesor.setId(id);
        return profesor;
    }
    @Named("profesorToLong")
    protected Long profesorToLong(Profesor profesor) {
        return profesor != null ? profesor.getId() : null;
    }
}
