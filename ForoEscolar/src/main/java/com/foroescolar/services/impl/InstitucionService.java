package com.foroescolar.services.impl;

import com.foroescolar.dtos.institucion.InstitucionRequestDto;
import com.foroescolar.dtos.institucion.InstitucionResponseDto;
import com.foroescolar.enums.NivelEducativo;
import com.foroescolar.mapper.InstitucionMapper;
import com.foroescolar.model.Institucion;
import com.foroescolar.repository.InstitucionRepository;
import com.foroescolar.services.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class InstitucionService {


    private final InstitucionRepository institucionRepository;
    private final InstitucionMapper institucionMapper;

    public InstitucionService(InstitucionRepository institucionRepository, InstitucionMapper institucionMapper) {
        this.institucionRepository = institucionRepository;
        this.institucionMapper = institucionMapper;
    }

    public InstitucionResponseDto save(InstitucionRequestDto institucionRequestDto) {
        Institucion institucion = institucionRepository.save(institucionMapper.toEntity(institucionRequestDto));
        institucion.setNivelEducativo(NivelEducativo.valueOf(institucionRequestDto.nivelEducativo()));
        return institucionMapper.toResponseDto(institucion);
    }

    public InstitucionResponseDto findById(Long id) {

        Institucion institucion = institucionRepository.findById(id).orElse(null);
        return institucionMapper.toResponseDto(institucion);
    }

    public void deleteById(Long id) {
        institucionRepository.deleteById(id);
    }

    public List<InstitucionResponseDto> findAll() {
        return institucionRepository.findAll().stream().map(institucionMapper::toResponseDto).toList();
    }
}
