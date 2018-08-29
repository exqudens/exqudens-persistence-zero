package org.exqudens.persistence.test.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "user")
public class ModelC extends AbstractModelC3 {

    private String name;

    public ModelC() {
        this(null, null);
    }

    public ModelC(Long id, String name) {
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
