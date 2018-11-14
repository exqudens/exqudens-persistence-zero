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
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

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

    public List<Class<? extends Annotation>> hierarchyIncludeAnnotationClasses;
    public List<Class<? extends Annotation>> fieldNamesIncludeAnnotationClasses;
    public List<Class<? extends Annotation>> relationIncludeAnnotationClasses;

    public List<Class<? extends Annotation>> hierarchyExcludeAnnotationClasses;
    public List<Class<? extends Annotation>> fieldNamesExcludeAnnotationClasses;
    public List<Class<? extends Annotation>> relationExcludeAnnotationClasses;

    static {
        INSTANCE = new Utils();
    }

    private Utils() {
        super();
        hierarchyIncludeAnnotationClasses = Arrays.asList(MappedSuperclass.class);
        fieldNamesIncludeAnnotationClasses = Arrays.asList(OneToOne.class, OneToMany.class, ManyToOne.class, ManyToMany.class);
        relationIncludeAnnotationClasses = fieldNamesIncludeAnnotationClasses;

        hierarchyExcludeAnnotationClasses = Collections.emptyList();
        fieldNamesExcludeAnnotationClasses = Arrays.asList(Transient.class);
        relationExcludeAnnotationClasses = Collections.emptyList();
    }

    public List<List<Class<?>>> getGraphs(List<Class<?>> entityClasses) {
        List<Entry<Class<?>, Class<?>>> relations = new ArrayList<>();

        for (Class<?> entityClass : entityClasses) {
            List<Class<?>> hierarchy = hierarchy(entityClass, hierarchyIncludeAnnotationClasses, hierarchyExcludeAnnotationClasses);
            Set<String> fieldNames = fieldNames(hierarchy, fieldNamesIncludeAnnotationClasses, fieldNamesExcludeAnnotationClasses, true, false, false);
            relations(entityClasses, hierarchy, fieldNames, relationIncludeAnnotationClasses, relationExcludeAnnotationClasses, relations);
        }

        return graphs(relations);
    }

    public List<Class<?>> getRootNodes(List<Class<?>> entityClasses, Class<?> entityClass, List<List<Class<?>>> graphs) {
        List<Class<?>> graph = graphs.stream().filter(g -> g.contains(entityClass)).findFirst().get();
        List<Class<?>> hierarchy = hierarchy(entityClass, hierarchyIncludeAnnotationClasses, hierarchyExcludeAnnotationClasses);
        Set<String> fieldNames = fieldNames(hierarchy, fieldNamesIncludeAnnotationClasses, fieldNamesExcludeAnnotationClasses, true, false, false);

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

    public List<Class<?>> getInsertOrder(Class<?> entityClass, List<Class<?>> entityClasses) {
        List<List<Class<?>>> graphs = getGraphs(entityClasses);
        List<Class<?>> rootNodes = getRootNodes(entityClasses, entityClass, graphs);
        List<Class<?>> graph = graphs.stream().filter(g -> g.contains(entityClass)).findFirst().get();
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

    public List<Object> getNodes(Object entity, List<Class<?>> entityClasses) {
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
                while ((children = getUnvisitedNodes(object, entityClasses, matrix)) != null) {
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

    private Collection<Object> getUnvisitedNodes(Object object, List<Class<?>> entityClasses, Map<Integer, Boolean> matrix) {
        try {
            List<Class<?>> hierarchy = hierarchy(object.getClass(), Arrays.asList(MappedSuperclass.class), Collections.emptyList());
            Collection<Object> result = new LinkedList<>();

            Set<String> fieldNames;
            Set<String> methodNames;

            fieldNames = fieldNames(hierarchy, Arrays.asList(OneToOne.class, OneToMany.class, ManyToOne.class), Arrays.asList(Transient.class), true, false, false);
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
                    Integer key = System.identityHashCode(value);
                    matrix.putIfAbsent(key, false);
                    if (!matrix.get(key)) {
                        result.add(value);
                    }
                } else {
                    Collection<?> collection = Collection.class.cast(value);
                    for (Object o : collection) {
                        if (!entityClasses.contains(o.getClass())) {
                            break;
                        }
                        Integer key = System.identityHashCode(o);
                        matrix.putIfAbsent(key, false);
                        if (!matrix.get(key)) {
                            result.add(o);
                        }
                    }
                }
            }

            fieldNames = fieldNames(hierarchy, Arrays.asList(ManyToMany.class), Arrays.asList(Transient.class), true, false, false);
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
                    Integer key = System.identityHashCode(o);
                    matrix.putIfAbsent(key, false);
                    if (!matrix.get(key)) {
                        result.add(o);
                        result.add(entry(object, o));
                    }
                }
            }

            if (result.isEmpty()) {
                return null;
            } else {
                return result;
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
