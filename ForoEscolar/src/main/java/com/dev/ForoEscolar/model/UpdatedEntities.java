package com.dev.ForoEscolar.model;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.repository.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Locale.filter;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

public class UpdatedEntities {

    public static <T> T update(T entity, T updatedEntity) {
        BeanUtils.copyProperties(updatedEntity, entity, getNullPropertiesNames(updatedEntity));
        return (T) entity;
    }

    private static String[] getNullPropertiesNames(Object updateEntity) {
        final BeanWrapper src = new BeanWrapperImpl(updateEntity);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }



}
