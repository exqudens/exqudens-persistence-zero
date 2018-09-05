package org.exqudens.persistence.util;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
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

    public List<List<Class<?>>> graphs(Class<?>... entityClasses) {
        List<Class<? extends Annotation>> hierarchyIncludeAnnotationClasses = Arrays.asList(MappedSuperclass.class);
        List<Class<? extends Annotation>> hierarchyExcludeAnnotationClasses = Arrays.asList();
        List<Class<? extends Annotation>> fieldNamesIncludeAnnotationClasses = Arrays.asList(Column.class, JoinColumns.class, JoinColumn.class);
        List<Class<? extends Annotation>> fieldNamesExcludeAnnotationClasses = Arrays.asList(Transient.class);
        List<Class<? extends Annotation>> relationIncludeAnnotationClasses = Arrays.asList(OneToMany.class, ManyToOne.class, ManyToMany.class, OneToOne.class);
        List<Class<? extends Annotation>> relationExcludeAnnotationClasses = Arrays.asList();
        List<Entry<Class<?>, Class<?>>> relations = new ArrayList<>();

        for (Class<?> entityClass : entityClasses) {
            List<Class<?>> hierarchy = hierarchy(entityClass, hierarchyIncludeAnnotationClasses, hierarchyExcludeAnnotationClasses);
            Set<String> fieldNames = fieldNames(hierarchy, fieldNamesIncludeAnnotationClasses, fieldNamesExcludeAnnotationClasses, false);
            relations(hierarchy, fieldNames, relationIncludeAnnotationClasses, relationExcludeAnnotationClasses, relations);
        }

        return graphs(relations);
    }

    public List<Class<?>> rootNodes(Class<?> entityClass, List<List<Class<?>>> graphs) {
        List<Class<? extends Annotation>> hierarchyIncludeAnnotationClasses = Arrays.asList(MappedSuperclass.class);
        List<Class<? extends Annotation>> hierarchyExcludeAnnotationClasses = Arrays.asList();
        List<Class<? extends Annotation>> fieldNamesIncludeAnnotationClasses = Arrays.asList(Column.class, JoinColumns.class, JoinColumn.class);
        List<Class<? extends Annotation>> fieldNamesExcludeAnnotationClasses = Arrays.asList(Transient.class);

        List<Class<?>> graph = graphs.stream().filter(g -> g.contains(entityClass)).findFirst().get();
        List<Class<?>> hierarchy = hierarchy(entityClass, hierarchyIncludeAnnotationClasses, hierarchyExcludeAnnotationClasses);
        Set<String> fieldNames = fieldNames(hierarchy, fieldNamesIncludeAnnotationClasses, fieldNamesExcludeAnnotationClasses, true);

        List<Class<? extends Annotation>> relationIncludeAnnotationClasses;
        List<Class<? extends Annotation>> relationExcludeAnnotationClasses;
        List<Entry<Class<?>, Class<?>>> relations = new ArrayList<>();

        relationIncludeAnnotationClasses = Arrays.asList(ManyToOne.class);
        relationExcludeAnnotationClasses = Arrays.asList();
        relations(hierarchy, fieldNames, relationIncludeAnnotationClasses, relationExcludeAnnotationClasses, relations);

        relationIncludeAnnotationClasses = Arrays.asList(OneToOne.class);
        relationExcludeAnnotationClasses = Arrays.asList(JoinColumns.class, JoinColumn.class, PrimaryKeyJoinColumns.class, PrimaryKeyJoinColumn.class);
        relations(hierarchy, fieldNames, relationIncludeAnnotationClasses, relationExcludeAnnotationClasses, relations);

        List<Class<?>> rootNodes = breadthFirstSearch(graph, relations, Arrays.asList(entityClass));
        Collections.reverse(rootNodes);
        return rootNodes;
    }

    public List<Class<?>> insertOrder(Class<?> entityClass, Class<?>... entityClasses) {
        List<List<Class<?>>> graphs = graphs(entityClasses);
        List<Class<?>> rootNodes = rootNodes(entityClass, graphs);
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
            Set<String> fieldNames = fieldNames(hierarchy, fieldNamesIncludeAnnotationClasses, fieldNamesExcludeAnnotationClasses, false);
            relations(hierarchy, fieldNames, relationIncludeAnnotationClasses, relationExcludeAnnotationClasses, relations);
        }

        return breadthFirstSearch(graph, relations, rootNodes);
    }

}
