package org.exqudens.persistence.test.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "model")
public class ModelC extends AbstractModelC3 {

    public ModelC() {
        this(null, null);
    }

    public ModelC(Long id, String name) {
        super(id, name);
    }

    @Column
    @Override
    public String getName() {
        return super.getName();
    }

}
