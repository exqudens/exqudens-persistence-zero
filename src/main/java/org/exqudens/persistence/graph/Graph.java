package org.exqudens.persistence.graph;

import java.util.List;

public class Graph {

    private List<Object> nodes;
    private List<Link>   links;

    public Graph() {
        this(null, null);
    }

    public Graph(List<Object> nodes, List<Link> links) {
        super();
        this.nodes = nodes;
        this.links = links;
    }

    public List<Object> getNodes() {
        return nodes;
    }

    public void setNodes(List<Object> nodes) {
        this.nodes = nodes;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

}
