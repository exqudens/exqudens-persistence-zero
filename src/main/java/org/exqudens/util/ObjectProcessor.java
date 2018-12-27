package org.exqudens.util;

import java.lang.annotation.Annotation;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public interface ObjectProcessor extends ClassProcessor {

    default <T> List<T> getNodes(
        Function<T, Integer> idFunction,
        Class<T> objectClass,
        T entity,
        List<Class<?>> entityClasses,
        List<Class<? extends Annotation>> hierarchyIncludeAnnotations,
        List<Class<? extends Annotation>> hierarchyExcludeAnnotations,
        List<Class<? extends Annotation>> fieldIncludeAnnotations,
        List<Class<? extends Annotation>> fieldManyToManyAnnotations,
        List<Class<? extends Annotation>> fieldExcludeAnnotations,
        boolean fieldIfIncludeAnnotationPresent,
        boolean fieldIfAllIncludeAnnotationsPresent,
        boolean fieldIfAllExcludeAnnotationsNotPresent,
        List<Class<?>> fieldIncludeTypes,
        List<Class<?>> fieldExcludeTypes
    ) {
        try {
            List<T> result = new ArrayList<>();
            result.add(entity);
            Queue<T> queue = new LinkedList<>();
            queue.add(entity);
            List<Integer> identityHashCodes = new ArrayList<>();
            identityHashCodes.add(System.identityHashCode(entity));
            while (!queue.isEmpty()) {
                T object = queue.remove();
                List<T> children = Collections.emptyList();
                do {
                    children = getUnvisitedNodes(
                        idFunction,
                        objectClass,
                        object,
                        entityClasses,
                        identityHashCodes,
                        hierarchyIncludeAnnotations,
                        hierarchyExcludeAnnotations,
                        fieldIncludeAnnotations,
                        fieldManyToManyAnnotations,
                        fieldExcludeAnnotations,
                        fieldIfIncludeAnnotationPresent,
                        fieldIfAllIncludeAnnotationsPresent,
                        fieldIfAllExcludeAnnotationsNotPresent,
                        fieldIncludeTypes,
                        fieldExcludeTypes
                    );
                    if (children.isEmpty()) {
                        break;
                    }
                    for (Object child : children) {
                        identityHashCodes.add(System.identityHashCode(child));
                        result.add(objectClass.cast(child));
                        queue.add(objectClass.cast(child));
                    }
                } while (!children.isEmpty());
            }
            return result;
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    default <T> List<T> getUnvisitedNodes(
        Function<T, Integer> idFunction,
        Class<T> objectClass,
        T object,
        List<Class<?>> entityClasses,
        List<Integer> ids,
        List<Class<? extends Annotation>> hierarchyIncludeAnnotations,
        List<Class<? extends Annotation>> hierarchyExcludeAnnotations,
        List<Class<? extends Annotation>> fieldIncludeAnnotations,
        List<Class<? extends Annotation>> fieldManyToManyAnnotations,
        List<Class<? extends Annotation>> fieldExcludeAnnotations,
        boolean fieldIfIncludeAnnotationPresent,
        boolean fieldIfAllIncludeAnnotationsPresent,
        boolean fieldIfAllExcludeAnnotationsNotPresent,
        List<Class<?>> fieldIncludeTypes,
        List<Class<?>> fieldExcludeTypes
    ) {
        try {
            List<Class<?>> hierarchy = hierarchy(
                object.getClass(),
                hierarchyIncludeAnnotations,
                hierarchyExcludeAnnotations
            );
            List<T> result = new LinkedList<>();

            Set<String> fieldNames;
            Set<String> methodNames;

            fieldNames = fieldNames(
                hierarchy,
                fieldIncludeAnnotations,
                fieldExcludeAnnotations,
                fieldIfIncludeAnnotationPresent,
                fieldIfAllIncludeAnnotationsPresent,
                fieldIfAllExcludeAnnotationsNotPresent,
                fieldIncludeTypes,
                fieldExcludeTypes
            );
            methodNames = fieldNames.stream().map(this::getterName).collect(Collectors.toSet());
            for (String methodName : methodNames) {
                Object value = object.getClass().getMethod(methodName).invoke(object);
                if (value == null) {
                    continue;
                }
                Class<?> type = value.getClass();
                if (!Collection.class.isAssignableFrom(type)) {
                    if (!entityClasses.contains(value.getClass())) {
                        continue;
                    }
                    Integer identityHashCode = idFunction.apply(objectClass.cast(value));
                    if (!ids.contains(identityHashCode)) {
                        result.add(objectClass.cast(value));
                    }
                } else {
                    Collection<?> collection = Collection.class.cast(value);
                    for (Object o : collection) {
                        if (!entityClasses.contains(o.getClass())) {
                            break;
                        }
                        Integer identityHashCode = idFunction.apply(objectClass.cast(o));
                        if (!ids.contains(identityHashCode)) {
                            result.add(objectClass.cast(o));
                        }
                    }
                }
            }

            if (fieldManyToManyAnnotations != null && !fieldManyToManyAnnotations.isEmpty()) {
                fieldNames = fieldNames(
                    hierarchy,
                    fieldManyToManyAnnotations,
                    fieldExcludeAnnotations,
                    fieldIfIncludeAnnotationPresent,
                    fieldIfAllIncludeAnnotationsPresent,
                    fieldIfAllExcludeAnnotationsNotPresent,
                    fieldIncludeTypes,
                    fieldExcludeTypes
                );
                methodNames = fieldNames.stream().map(this::getterName).collect(Collectors.toSet());
                for (String methodName : methodNames) {
                    Object value = object.getClass().getMethod(methodName).invoke(object);
                    if (value == null) {
                        continue;
                    }
                    Class<?> type = value.getClass();
                    if (!Collection.class.isAssignableFrom(type)) {
                        continue;
                    }
                    Collection<?> collection = Collection.class.cast(value);
                    for (Object o : collection) {
                        if (!entityClasses.contains(o.getClass())) {
                            break;
                        }
                        Integer identityHashCode = idFunction.apply(objectClass.cast(o));
                        if (!ids.contains(identityHashCode)) {
                            result.add(objectClass.cast(o));
                            result.add(objectClass.cast(new SimpleEntry<>(object, o)));
                        }
                    }
                }
            }

            return result;
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    default Map<String, Object> toMap(
        Object object,
        List<Class<? extends Annotation>> hierarchyIncludeAnnotations,
        List<Class<? extends Annotation>> hierarchyExcludeAnnotations,
        List<Class<? extends Annotation>> fieldIncludeAnnotations,
        List<Class<? extends Annotation>> fieldExcludeAnnotations,
        boolean fieldIfIncludeAnnotationPresent,
        boolean fieldIfAllIncludeAnnotationsPresent,
        boolean fieldIfAllExcludeAnnotationsNotPresent,
        List<Class<?>> fieldIncludeTypes,
        List<Class<?>> fieldExcludeTypes
    ) {
        try {
            List<Class<?>> hierarchy = hierarchy(
                object.getClass(),
                hierarchyIncludeAnnotations,
                hierarchyExcludeAnnotations
            );
            Set<String> fieldNames = fieldNames(
                hierarchy,
                fieldIncludeAnnotations,
                fieldExcludeAnnotations,
                fieldIfIncludeAnnotationPresent,
                fieldIfAllIncludeAnnotationsPresent,
                fieldIfAllExcludeAnnotationsNotPresent,
                fieldIncludeTypes,
                fieldExcludeTypes
            );
            Map<String, String> methodFieldNameMap = fieldNames.stream().map(
                fieldName -> new SimpleEntry<>(getterName(fieldName), fieldName)
            ).distinct().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
            Map<String, Object> map = new HashMap<>();
            for (Entry<String, String> entry : methodFieldNameMap.entrySet()) {
                String methodName = entry.getKey();
                String fieldName = entry.getValue();
                Object value = object.getClass().getMethod(methodName).invoke(object);
                if (value != null) {
                    map.put(fieldName, value);
                }
            }
            return map;
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
