package com.ymware.engine.util;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * BeanMapper - replaced Orika with Spring BeanUtils for JDK 17 compatibility
 */
public class OrikaBeanMapper {

    public static <S, D> D map(S sourceObject, Class<D> destinationClass) {
        if (sourceObject == null) {
            return null;
        }
        try {
            D destination = destinationClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(sourceObject, destination);
            return destination;
        } catch (Exception e) {
            throw new RuntimeException("Failed to map " + sourceObject.getClass() + " to " + destinationClass, e);
        }
    }

    public static <S, D> List<D> mapList(Collection<S> sourceList, Class<D> destinationClass) {
        List<D> destinationList = new ArrayList<>();
        if (sourceList == null) {
            return destinationList;
        }
        for (S source : sourceList) {
            destinationList.add(map(source, destinationClass));
        }
        return destinationList;
    }

    public static <S, D> void mapCollection(Collection<S> sourceCollection, Collection<D> destinationCollection, Class<D> destinationClass) {
        if (sourceCollection == null) {
            return;
        }
        for (S source : sourceCollection) {
            destinationCollection.add(map(source, destinationClass));
        }
    }
}
