package org.exqudens.persistence.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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

    public List<Object> nodes(Object entity, List<Class<?>> entityClasses) {
        try {
            List<Object> result = new ArrayList<>();
            result.add(entity);
            Queue<Object> queue = new LinkedList<>();
            queue.add(entity);
            Map<Integer, Boolean> matrix = new HashMap<>();
            matrix.put(System.identityHashCode(entity), true);
            while (!queue.isEmpty()) {
                Object object = queue.remove();
                Collection<Object> children = null;
                while ((children = unvisitedNodes(object, entityClasses, matrix)) != null) {
                    for (Object child : children) {
                        matrix.put(System.identityHashCode(child), true);
                        result.add(child);
                        queue.add(child);
                    }
                }
            }
            return result;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private Collection<Object> unvisitedNodes(Object object, List<Class<?>> entityClasses, Map<Integer, Boolean> matrix) {
        try {
            List<Class<?>> hierarchy = hierarchy(object.getClass(), Arrays.asList(MappedSuperclass.class), Collections.emptyList());
            Set<String> fieldNames = fieldNames(hierarchy, Arrays.asList(OneToOne.class, OneToMany.class, ManyToOne.class, ManyToMany.class), Arrays.asList(Transient.class), true, false, false);
            Set<String> methodNames = fieldNames.stream().map(this::getterName).collect(Collectors.toSet());
            for (String methodName : methodNames) {
                Object value = object.getClass().getMethod(methodName).invoke(object);
                if (value != null) {
                    Class<?> type = value.getClass();
                    if (Collection.class.isAssignableFrom(type)) {
                        Collection<?> collection = Collection.class.cast(value);
                        List<Object> result = null;
                        for (Object o : collection) {
                            if (!entityClasses.contains(o.getClass())) {
                                break;
                            }
                            Integer key = System.identityHashCode(o);
                            matrix.putIfAbsent(key, false);
                            if (!matrix.get(key)) {
                                if (result == null) {
                                    result = new LinkedList<>();
                                }
                                result.add(o);
                            }
                        }
                        return result;
                    } else {
                        if (!entityClasses.contains(value.getClass())) {
                            continue;
                        }
                        Integer key = System.identityHashCode(value);
                        matrix.putIfAbsent(key, false);
                        if (!matrix.get(key)) {
                            return Arrays.asList(value);
                        }
                    }
                }
            }
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
