package org.exqudens.persistence.test.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractModelC2 extends AbstractModelC1 {

    @Column(name = "name")
    private String name;

    protected AbstractModelC2(Long id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
