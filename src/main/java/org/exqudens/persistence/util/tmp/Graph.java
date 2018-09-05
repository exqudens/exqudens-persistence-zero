package org.exqudens.persistence.util.tmp;

import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Stack;

public class Graph<T> {

    public static <K, V> Entry<K, V> entry(K key, V value) {
        return new SimpleEntry<>(key, value);
    }

    public static <K, V> Entry<K, V> immutableEntry(K key, V value) {
        return new SimpleImmutableEntry<>(key, value);
    }

    private final List<T>     nodes;
    private final boolean[]   visited;
    private final boolean[][] matrix;
    private final int         size;

    public Graph(List<T> nodes, List<Entry<T, T>> relations) {
        super();
        this.nodes = nodes;
        size = nodes.size();
        visited = new boolean[size];
        matrix = new boolean[size][size];
        for (Entry<T, T> relation : relations) {
            int y = nodes.indexOf(relation.getKey());
            int x = nodes.indexOf(relation.getValue());
            matrix[y][x] = true;
            matrix[x][y] = true;
        }
    }

    public List<T> breadthFirstSearch(List<T> rootNodes) {
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
            while ((child = getUnvisitedChildNode(n)) != null) {
                int index = nodes.indexOf(child);
                visited[index] = true;
                result.add(child);
                q.add(child);
            }
        }
        Arrays.fill(visited, false);
        return result;
    }

    public List<T> depthFirstSearch(List<T> rootNodes) {
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
            T child = getUnvisitedChildNode(n);
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

    private T getUnvisitedChildNode(T n) {
        int index = nodes.indexOf(n);
        for (int j = 0; j < size; j++) {
            if (matrix[index][j] && !visited[j]) {
                return nodes.get(j);
            }
        }
        return null;
    }

}
