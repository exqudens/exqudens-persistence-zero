package org.exqudens.persistence.test.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractModelC2 extends AbstractModelC1 {

    protected AbstractModelC2(Long id) {
        super(id);
    }

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    @Override
    public Long getId() {
        return super.getId();
    }

}
