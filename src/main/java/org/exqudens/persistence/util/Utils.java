package org.exqudens.persistence.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Stack;

public class Utils {

    public static <K, V> Entry<K, V> toEntry(K key, V value) {
        return new SimpleEntry<>(key, value);
    }

    public static <K, V> Entry<K, V> toImmutableEntry(K key, V value) {
        return new SimpleImmutableEntry<>(key, value);
    }

    public static List<List<Class<?>>> toUniDirectionGraphs(List<Entry<Class<?>, Class<?>>> relations) {
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

    public static List<List<Class<?>>> toGraphs(List<Entry<Class<?>, Class<?>>> relations) {
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

    public static List<Entry<Class<?>, Class<?>>> getRelationsByFields(List<Class<?>> classes, List<Class<? extends Annotation>> annotationClasses) {
        List<Entry<Class<?>, Class<?>>> relations = new ArrayList<>();
        for (Class<?> c : classes) {
            List<Class<?>> associatedClasses = new ArrayList<>();
            Field[] fields = c.getDeclaredFields();
            for (Field field : fields) {
                for (Class<? extends Annotation> annotationClass : annotationClasses) {
                    Annotation annotation = field.getAnnotation(annotationClass);
                    if (annotation != null) {
                        Class<?> type = field.getType();
                        if (Collection.class.isAssignableFrom(type)) {
                            ParameterizedType parameterizedType = ParameterizedType.class.cast(field.getGenericType());
                            Type genericType = parameterizedType.getActualTypeArguments()[0];
                            type = Class.class.cast(genericType);
                        }
                        if (classes.contains(type)) {
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

    public static <T> List<T> breadthFirstSearch(List<T> nodes, List<Entry<T, T>> relations, List<T> rootNodes) {
        int size = nodes.size();
        boolean[] visited = new boolean[size];
        boolean[][] matrix = new boolean[size][size];
        for (Entry<T, T> relation : relations) {
            int y = nodes.indexOf(relation.getKey());
            int x = nodes.indexOf(relation.getValue());
            matrix[y][x] = true;
            matrix[x][y] = true;
        }
        List<T> result = new ArrayList<>();
        Queue<T> q = new LinkedList<>();
        for (T rootNode : rootNodes) {
            q.add(rootNode);
            result.add(rootNode);
            int index = nodes.indexOf(rootNode);
            visited[index] = true;
        }
        while (!q.isEmpty()) {
            T n = q.remove();
            T child = null;
            while ((child = getUnvisitedChildNode(n, nodes, matrix, visited)) != null) {
                int index = nodes.indexOf(child);
                visited[index] = true;
                result.add(child);
                q.add(child);
            }
        }
        return result;
    }

    public static <T> List<T> depthFirstSearch(List<T> nodes, List<Entry<T, T>> relations, List<T> rootNodes) {
        int size = nodes.size();
        boolean[] visited = new boolean[size];
        boolean[][] matrix = new boolean[size][size];
        for (Entry<T, T> relation : relations) {
            int y = nodes.indexOf(relation.getKey());
            int x = nodes.indexOf(relation.getValue());
            matrix[y][x] = true;
            matrix[x][y] = true;
        }
        List<T> result = new ArrayList<>();
        Stack<T> s = new Stack<>();
        for (T rootNode : rootNodes) {
            s.push(rootNode);
            int index = nodes.indexOf(rootNode);
            visited[index] = true;
            result.add(rootNode);
        }
        while (!s.isEmpty()) {
            T n = s.peek();
            T child = getUnvisitedChildNode(n, nodes, matrix, visited);
            if (child != null) {
                int index = nodes.indexOf(child);
                visited[index] = true;
                result.add(child);
                s.push(child);
            } else {
                s.pop();
            }
        }
        Arrays.fill(visited, false);
        return result;
    }

    private static <T> T getUnvisitedChildNode(T n, List<T> nodes, boolean[][] matrix, boolean[] visited) {
        int index = nodes.indexOf(n);
        for (int j = 0; j < nodes.size(); j++) {
            if (matrix[index][j] && !visited[j]) {
                return nodes.get(j);
            }
        }
        return null;
    }

    private Utils() {
        super();
    }

}
