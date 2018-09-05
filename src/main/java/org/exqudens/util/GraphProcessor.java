package org.exqudens.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.Map.Entry;

public interface GraphProcessor {

    default <T> List<T> breadthFirstSearch(List<T> nodes, List<Entry<T, T>> relations, List<T> rootNodes) {
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

    default <T> List<T> depthFirstSearch(List<T> nodes, List<Entry<T, T>> relations, List<T> rootNodes) {
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

    default <T> T getUnvisitedChildNode(T n, List<T> nodes, boolean[][] matrix, boolean[] visited) {
        int index = nodes.indexOf(n);
        for (int j = 0; j < nodes.size(); j++) {
            if (matrix[index][j] && !visited[j]) {
                return nodes.get(j);
            }
        }
        return null;
    }

}
