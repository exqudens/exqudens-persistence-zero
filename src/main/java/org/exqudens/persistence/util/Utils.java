package org.exqudens.persistence.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.Transient;

import org.exqudens.util.ClassProcessor;
import org.exqudens.util.EntryProcessor;
import org.exqudens.util.GraphProcessor;

public class Utils implements EntryProcessor, GraphProcessor, ClassProcessor {

    public static final Utils INSTANCE;

    static {
        INSTANCE = new Utils();
    }

    private Utils() {
        super();
    }

    public List<List<Class<?>>> graphs(boolean fieldNamesOnlyIfIncludePresent, List<Class<?>> entityClasses) {
        List<Class<? extends Annotation>> hierarchyIncludeAnnotationClasses = Arrays.asList(MappedSuperclass.class);
        List<Class<? extends Annotation>> hierarchyExcludeAnnotationClasses = Arrays.asList();
        List<Class<? extends Annotation>> fieldNamesIncludeAnnotationClasses = Arrays.asList(Column.class, JoinColumns.class, JoinColumn.class);
        List<Class<? extends Annotation>> fieldNamesExcludeAnnotationClasses = Arrays.asList(Transient.class);
        List<Class<? extends Annotation>> relationIncludeAnnotationClasses = Arrays.asList(OneToMany.class, ManyToOne.class, ManyToMany.class, OneToOne.class);
        List<Class<? extends Annotation>> relationExcludeAnnotationClasses = Arrays.asList();
        List<Entry<Class<?>, Class<?>>> relations = new ArrayList<>();

        for (Class<?> entityClass : entityClasses) {
            List<Class<?>> hierarchy = hierarchy(entityClass, hierarchyIncludeAnnotationClasses, hierarchyExcludeAnnotationClasses);
            Set<String> fieldNames = fieldNames(hierarchy, fieldNamesIncludeAnnotationClasses, fieldNamesExcludeAnnotationClasses, fieldNamesOnlyIfIncludePresent, false, false);
            relations(entityClasses, hierarchy, fieldNames, relationIncludeAnnotationClasses, relationExcludeAnnotationClasses, relations);
        }

        return graphs(relations);
    }

    public List<Class<?>> rootNodes(List<Class<?>> entityClasses, boolean fieldNamesOnlyIfIncludePresent, Class<?> entityClass, List<List<Class<?>>> graphs) {
        List<Class<? extends Annotation>> hierarchyIncludeAnnotationClasses = Arrays.asList(MappedSuperclass.class);
        List<Class<? extends Annotation>> hierarchyExcludeAnnotationClasses = Arrays.asList();
        List<Class<? extends Annotation>> fieldNamesIncludeAnnotationClasses = Arrays.asList(Column.class, JoinColumns.class, JoinColumn.class);
        List<Class<? extends Annotation>> fieldNamesExcludeAnnotationClasses = Arrays.asList(Transient.class);

        List<Class<?>> graph = graphs.stream().filter(g -> g.contains(entityClass)).findFirst().get();
        List<Class<?>> hierarchy = hierarchy(entityClass, hierarchyIncludeAnnotationClasses, hierarchyExcludeAnnotationClasses);
        Set<String> fieldNames = fieldNames(hierarchy, fieldNamesIncludeAnnotationClasses, fieldNamesExcludeAnnotationClasses, fieldNamesOnlyIfIncludePresent, false, false);

        List<Class<? extends Annotation>> relationIncludeAnnotationClasses;
        List<Class<? extends Annotation>> relationExcludeAnnotationClasses;
        List<Entry<Class<?>, Class<?>>> relations = new ArrayList<>();

        relationIncludeAnnotationClasses = Arrays.asList(ManyToOne.class);
        relationExcludeAnnotationClasses = Arrays.asList();
        relations(entityClasses, hierarchy, fieldNames, relationIncludeAnnotationClasses, relationExcludeAnnotationClasses, relations);

        relationIncludeAnnotationClasses = Arrays.asList(OneToOne.class);
        relationExcludeAnnotationClasses = Arrays.asList(JoinColumns.class, JoinColumn.class, PrimaryKeyJoinColumns.class, PrimaryKeyJoinColumn.class);
        relations(entityClasses, hierarchy, fieldNames, relationIncludeAnnotationClasses, relationExcludeAnnotationClasses, relations);

        List<Class<?>> rootNodes = breadthFirstSearch(graph, relations, Arrays.asList(entityClass));
        Collections.reverse(rootNodes);
        return rootNodes;
    }

    public List<Class<?>> insertOrder(boolean fieldNamesOnlyIfIncludePresent, Class<?> entityClass, List<Class<?>> entityClasses) {
        List<List<Class<?>>> graphs = graphs(fieldNamesOnlyIfIncludePresent, entityClasses);
        List<Class<?>> rootNodes = rootNodes(entityClasses, fieldNamesOnlyIfIncludePresent, entityClass, graphs);
        List<Class<?>> graph = graphs.stream().filter(g -> g.contains(entityClass)).findFirst().get();
        List<Class<? extends Annotation>> hierarchyIncludeAnnotationClasses = Arrays.asList(MappedSuperclass.class);
        List<Class<? extends Annotation>> hierarchyExcludeAnnotationClasses = Arrays.asList();
        List<Class<? extends Annotation>> fieldNamesIncludeAnnotationClasses = Arrays.asList(Column.class, JoinColumns.class, JoinColumn.class);
        List<Class<? extends Annotation>> fieldNamesExcludeAnnotationClasses = Arrays.asList(Transient.class);
        List<Class<? extends Annotation>> relationIncludeAnnotationClasses = Arrays.asList(OneToMany.class, ManyToOne.class, ManyToMany.class, OneToOne.class);
        List<Class<? extends Annotation>> relationExcludeAnnotationClasses = Arrays.asList();
        List<Entry<Class<?>, Class<?>>> relations = new ArrayList<>();

        for (Class<?> graphClass : graph) {
            List<Class<?>> hierarchy = hierarchy(graphClass, hierarchyIncludeAnnotationClasses, hierarchyExcludeAnnotationClasses);
            Set<String> fieldNames = fieldNames(hierarchy, fieldNamesIncludeAnnotationClasses, fieldNamesExcludeAnnotationClasses, false, false, false);
            relations(entityClasses, hierarchy, fieldNames, relationIncludeAnnotationClasses, relationExcludeAnnotationClasses, relations);
        }

        return breadthFirstSearch(graph, relations, rootNodes);
    }

    public Map<Integer, Object> toOne(List<Class<?>> entityClasses, Object entity, Map<Integer, Object> previous) {
        try {
            if (previous == null) {
                previous = new HashMap<>();
            }

            if (Collection.class.isInstance(entity)) {
                for (Object o : Collection.class.cast(entity)) {
                    toMany(entityClasses, o, previous);
                }
            } else {
                if (previous.putIfAbsent(System.identityHashCode(entity), entity) != null) {
                    return previous;
                }

                List<Class<? extends Annotation>> hierarchyIncludeAnnotationClasses = Arrays.asList(MappedSuperclass.class);
                List<Class<? extends Annotation>> hierarchyExcludeAnnotationClasses = Arrays.asList();

                List<Class<?>> hierarchy = hierarchy(entity.getClass(), hierarchyIncludeAnnotationClasses, hierarchyExcludeAnnotationClasses);
                Set<String> fieldNames = new HashSet<>();
                fieldNames.addAll(fieldNames(hierarchy, Arrays.asList(OneToMany.class), Arrays.asList(Transient.class), true, true, true));
                fieldNames.addAll(fieldNames(hierarchy, Arrays.asList(ManyToMany.class, JoinTable.class), Arrays.asList(Transient.class), true, true, true));
                Set<String> methodNames = fieldNames.stream().map(this::getterName).collect(Collectors.toSet());

                for (Method method : entity.getClass().getDeclaredMethods()) {
                    if (methodNames.contains(method.getName())) {
                        Object o = method.invoke(entity);
                        toMany(entityClasses, o, previous);
                    }
                }
            }

            return previous;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Map<Integer, Object> toMany(List<Class<?>> entityClasses, Object entity, Map<Integer, Object> previous) {
        try {
            if (previous == null) {
                previous = new HashMap<>();
            }

            if (Collection.class.isInstance(entity)) {
                for (Object o : Collection.class.cast(entity)) {
                    toMany(entityClasses, o, previous);
                }
            } else {
                if (previous.putIfAbsent(System.identityHashCode(entity), entity) != null) {
                    return previous;
                }

                List<Class<? extends Annotation>> hierarchyIncludeAnnotationClasses = Arrays.asList(MappedSuperclass.class);
                List<Class<? extends Annotation>> hierarchyExcludeAnnotationClasses = Arrays.asList();

                List<Class<?>> hierarchy = hierarchy(entity.getClass(), hierarchyIncludeAnnotationClasses, hierarchyExcludeAnnotationClasses);
                Set<String> fieldNames = new HashSet<>();
                fieldNames.addAll(fieldNames(hierarchy, Arrays.asList(OneToMany.class), Arrays.asList(Transient.class), true, true, true));
                fieldNames.addAll(fieldNames(hierarchy, Arrays.asList(ManyToMany.class, JoinTable.class), Arrays.asList(Transient.class), true, true, true));
                Set<String> methodNames = fieldNames.stream().map(this::getterName).collect(Collectors.toSet());

                for (Method method : entity.getClass().getDeclaredMethods()) {
                    if (methodNames.contains(method.getName())) {
                        Object o = method.invoke(entity);
                        toMany(entityClasses, o, previous);
                    }
                }
            }

            return previous;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public List<Object> getAllGraphEntities(Object object) {
        try {
            
            return null;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
