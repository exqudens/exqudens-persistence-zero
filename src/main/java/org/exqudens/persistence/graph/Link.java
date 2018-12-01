package org.exqudens.persistence.graph;

public class Link {

    private Integer source;
    private Integer target;
    private String  destination;
    private String  type;

    public Link() {
        this(null, null, null, null);
    }

    public Link(Integer source, Integer target, String destination, String type) {
        super();
        this.source = source;
        this.target = target;
        this.destination = destination;
        this.type = type;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Integer getTarget() {
        return target;
    }

    public void setTarget(Integer target) {
        this.target = target;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
