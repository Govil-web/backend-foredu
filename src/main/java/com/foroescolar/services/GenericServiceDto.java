package com.foroescolar.services;

import java.util.Optional;

public interface GenericServiceDto<T, EntityDto> {

    EntityDto save(EntityDto requestDTO);

    Optional<EntityDto> findById(Long id);

    Iterable<EntityDto> findAll();

    void deleteById(Long id);




}
