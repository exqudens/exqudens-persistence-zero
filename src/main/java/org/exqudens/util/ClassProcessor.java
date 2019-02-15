package org.exqudens.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ClassProcessor {

    @Deprecated
    default List<List<Class<?>>> uniDirectionGraphs(List<Entry<Class<?>, Class<?>>> relations) {
        List<List<Class<?>>> graphs = new ArrayList<>();
        List<Class<?>> newGraph = new ArrayList<>();
        newGraph.add(relations.get(0).getKey());
        newGraph.add(relations.get(0).getValue());
        graphs.add(newGraph);
        newGraph = null;
        for (int i = 1; i < relations.size(); i++) {
            Entry<Class<?>, Class<?>> relation = relations.get(i);
            Class<?> key = relation.getKey();
            Class<?> value = relation.getValue();
            for (List<Class<?>> graph : graphs) {
                int min = 0;
                int max = graph.size() - 1;
                if (graph.get(min).equals(key)) {
                    graph.add(0, value);
                    newGraph = null;
                    break;
                } else if (graph.get(min).equals(value)) {
                    graph.add(0, key);
                    newGraph = null;
                    break;
                } else if (graph.get(max).equals(key)) {
                    graph.add(value);
                    newGraph = null;
                    break;
                } else if (graph.get(max).equals(value)) {
                    graph.add(key);
                    newGraph = null;
                    break;
                } else {
                    if (newGraph == null) {
                        newGraph = new ArrayList<>();
                        newGraph.add(key);
                        newGraph.add(value);
                    }
                }
            }
            if (newGraph != null) {
                graphs.add(newGraph);
                newGraph = null;
            }
        }
        return graphs;
    }

    default List<List<Class<?>>> graphs(List<Entry<Class<?>, Class<?>>> relations) {
        List<List<Class<?>>> graphs = new ArrayList<>();
        List<Class<?>> newGraph = new ArrayList<>();
        newGraph.add(relations.get(0).getKey());
        newGraph.add(relations.get(0).getValue());
        graphs.add(newGraph);
        newGraph = null;
        for (int i = 1; i < relations.size(); i++) {
            Entry<Class<?>, Class<?>> relation = relations.get(i);
            Class<?> key = relation.getKey();
            Class<?> value = relation.getValue();
            for (List<Class<?>> graph : graphs) {
                if (graph.contains(key) && graph.contains(value)) {
                    newGraph = null;
                    break;
                } else if (graph.contains(key) && !graph.contains(value)) {
                    graph.add(value);
                    newGraph = null;
                    break;
                } else if (graph.contains(value) && !graph.contains(key)) {
                    graph.add(key);
                    newGraph = null;
                    break;
                } else {
                    if (newGraph == null) {
                        newGraph = new ArrayList<>();
                        newGraph.add(key);
                        newGraph.add(value);
                    }
                }
            }
            if (newGraph != null) {
                graphs.add(newGraph);
                newGraph = null;
            }
        }
        return graphs;
    }

    default List<Entry<Class<?>, Class<?>>> relations(
        List<Class<?>> entityClasses,
        List<Class<?>> hierarchy,
        Set<String> fieldNames,
        List<Class<? extends Annotation>> includeAnnotationClasses,
        List<Class<? extends Annotation>> excludeAnnotationClasses,
        List<Entry<Class<?>, Class<?>>> relations
    ) {
        if (relations == null) {
            relations = new ArrayList<>();
        }
        for (Class<?> c : hierarchy) {
            Set<Class<?>> associatedClasses = new LinkedHashSet<>();
            for (Field field : c.getDeclaredFields()) {
                if (fieldNames.contains(field.getName())) {
                    boolean includePresent = Arrays.stream(field.getAnnotations()).map(Annotation::annotationType).filter(ac -> includeAnnotationClasses.contains(ac)).findAny().isPresent();
                    boolean excludePresent = Arrays.stream(field.getAnnotations()).map(Annotation::annotationType).filter(ac -> excludeAnnotationClasses.contains(ac)).findAny().isPresent();
                    if (includePresent && !excludePresent) {
                        Class<?> type = field.getType();
                        if (Collection.class.isAssignableFrom(type)) {
                            ParameterizedType parameterizedType = ParameterizedType.class.cast(field.getGenericType());
                            Type genericType = parameterizedType.getActualTypeArguments()[0];
                            type = Class.class.cast(genericType);
                        }
                        if (entityClasses.contains(type)) {
                            associatedClasses.add(type);
                        }
                    }
                }
            }
            Map<String, String> methodFieldNameMap = fieldNames
            .stream()
            .map(fieldName -> new SimpleEntry<>(getterName(fieldName), fieldName))
            .distinct()
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
            for (Method method : c.getDeclaredMethods()) {
                String fieldName = methodFieldNameMap.get(method.getName());
                if (fieldName != null) {
                    boolean includePresent = Arrays.stream(method.getAnnotations()).map(Annotation::annotationType).filter(ac -> includeAnnotationClasses.contains(ac)).findAny().isPresent();
                    boolean excludePresent = Arrays.stream(method.getAnnotations()).map(Annotation::annotationType).filter(ac -> excludeAnnotationClasses.contains(ac)).findAny().isPresent();
                    if (includePresent && !excludePresent) {
                        Class<?> type = method.getReturnType();
                        if (Collection.class.isAssignableFrom(type)) {
                            ParameterizedType parameterizedType = ParameterizedType.class.cast(method.getGenericReturnType());
                            Type genericType = parameterizedType.getActualTypeArguments()[0];
                            type = Class.class.cast(genericType);
                        }
                        if (entityClasses.contains(type)) {
                            associatedClasses.add(type);
                        }
                    }
                }
            }
            for (Class<?> associatedClass : associatedClasses) {
                if (!c.equals(associatedClass)) {
                    Entry<Class<?>, Class<?>> newEntry = new SimpleEntry<>(c, associatedClass);
                    Entry<Class<?>, Class<?>> checkEntry = new SimpleEntry<>(associatedClass, c);
                    if (!relations.contains(newEntry) && !relations.contains(checkEntry)) {
                        relations.add(newEntry);
                    }
                }
            }
        }
        return relations;
    }

    default Set<String> fieldNames(
        List<Class<?>> hierarchy,
        List<Class<? extends Annotation>> includeAnnotations,
        List<Class<? extends Annotation>> excludeAnnotations,
        boolean ifIncludeAnnotationPresent,
        boolean ifAllIncludeAnnotationsPresent,
        boolean ifAllExcludeAnnotationsNotPresent,
        List<Class<?>> includeTypes,
        List<Class<?>> excludeTypes
    ) {
        Set<String> allFieldNames = new LinkedHashSet<>();
        Set<String> fieldNames = new LinkedHashSet<>();
        for (Class<?> c : hierarchy) {
            Set<String> classFieldNames = new LinkedHashSet<>();
            for (Field field : c.getDeclaredFields()) {
                String fieldName = field.getName();
                if ("this$0".equals(fieldName)) {
                    continue;
                }
                allFieldNames.add(fieldName);
                Set<Class<? extends Annotation>> fieldAnnotationClasses = Arrays.stream(field.getAnnotations()).map(Annotation::annotationType).collect(Collectors.toSet());
                Class<?> fieldType = field.getType();
                boolean includePresent = true;
                boolean excludePresent = false;
                if (includeAnnotations != null && !includeAnnotations.isEmpty() && ifAllIncludeAnnotationsPresent) {
                    includePresent = includePresent && fieldAnnotationClasses.containsAll(includeAnnotations);
                } else if (includeAnnotations != null && !includeAnnotations.isEmpty()) {
                    includePresent = includePresent && fieldAnnotationClasses.stream().filter(ac -> includeAnnotations.contains(ac)).findAny().isPresent();
                }
                if (includeTypes != null && !includeTypes.isEmpty()) {
                    includePresent = includePresent && includeTypes.contains(fieldType);
                }
                if (excludeAnnotations != null && !excludeAnnotations.isEmpty() && ifAllExcludeAnnotationsNotPresent) {
                    excludePresent = excludePresent || fieldAnnotationClasses.containsAll(excludeAnnotations);
                } else if (excludeAnnotations != null && !excludeAnnotations.isEmpty()) {
                    excludePresent = excludePresent || fieldAnnotationClasses.stream().filter(ac -> excludeAnnotations.contains(ac)).findAny().isPresent();
                }
                if (excludeTypes != null && !excludeTypes.isEmpty()) {
                    excludePresent = excludePresent || excludeTypes.contains(fieldType);
                }
                if (excludePresent) {
                    fieldNames.remove(fieldName);
                    classFieldNames.remove(fieldName);
                } else if (includePresent) {
                    classFieldNames.add(fieldName);
                }
            }
            Map<String, String> methodFieldNameMap = Stream
            .concat(allFieldNames.stream(), classFieldNames.stream())
            .map(fieldName -> new SimpleEntry<>(getterName(fieldName), fieldName))
            .distinct()
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
            for (Method method : c.getDeclaredMethods()) {
                String fieldName = methodFieldNameMap.get(method.getName());
                if (fieldName == null) {
                    continue;
                }
                Set<Class<? extends Annotation>> methodAnnotationClasses = Arrays.stream(method.getAnnotations()).map(Annotation::annotationType).collect(Collectors.toSet());
                Class<?> methodReturnType = method.getReturnType();
                boolean includePresent = true;
                boolean excludePresent = false;
                if (includeAnnotations != null && !includeAnnotations.isEmpty() && ifAllIncludeAnnotationsPresent) {
                    includePresent = includePresent && methodAnnotationClasses.containsAll(includeAnnotations);
                } else if (includeAnnotations != null && !includeAnnotations.isEmpty()) {
                    includePresent = includePresent &&methodAnnotationClasses.stream().filter(ac -> includeAnnotations.contains(ac)).findAny().isPresent();
                }
                if (includeTypes != null && !includeTypes.isEmpty()) {
                    includePresent = includePresent && includeTypes.contains(methodReturnType);
                }
                if (excludeAnnotations != null && !excludeAnnotations.isEmpty() && ifAllExcludeAnnotationsNotPresent) {
                    excludePresent = excludePresent || methodAnnotationClasses.containsAll(excludeAnnotations);
                } else if (excludeAnnotations != null && !excludeAnnotations.isEmpty()) {
                    excludePresent = excludePresent || methodAnnotationClasses.stream().filter(ac -> excludeAnnotations.contains(ac)).findAny().isPresent();
                }
                if (excludeTypes != null && !excludeTypes.isEmpty()) {
                    excludePresent = excludePresent || excludeTypes.contains(methodReturnType);
                }
                if (excludePresent) {
                    fieldNames.remove(fieldName);
                    classFieldNames.remove(fieldName);
                } else if (ifIncludeAnnotationPresent && includePresent) {
                    classFieldNames.add(fieldName);
                } else if (!ifIncludeAnnotationPresent) {
                    classFieldNames.add(fieldName);
                }
            }
            fieldNames.addAll(classFieldNames);
        }
        return fieldNames;
    }

    default List<Class<?>> hierarchy(
        Class<?> entityClass,
        List<Class<? extends Annotation>> includeAnnotations,
        List<Class<? extends Annotation>> excludeAnnotations
    ) {
        List<Class<?>> hierarchy = new ArrayList<>();
        Class<?> superClass = entityClass;
        while (superClass != null) {
            hierarchy.add(0, superClass);
            superClass = superClass.getSuperclass();
            if (superClass != null) {
                boolean includePresent = true;
                boolean excludePresent = false;
                if (includeAnnotations != null && !includeAnnotations.isEmpty()) {
                    includePresent = includePresent && Arrays.stream(superClass.getAnnotations()).map(Annotation::annotationType).filter(ac -> includeAnnotations.contains(ac)).findAny().isPresent();
                }
                if (excludeAnnotations != null && !excludeAnnotations.isEmpty()) {
                    excludePresent = excludePresent || Arrays.stream(superClass.getAnnotations()).map(Annotation::annotationType).filter(ac -> excludeAnnotations.contains(ac)).findAny().isPresent();
                }
                if (includePresent && !excludePresent) {
                    continue;
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        return hierarchy;
    }

    default String getterName(String fieldName) {
        return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

}
