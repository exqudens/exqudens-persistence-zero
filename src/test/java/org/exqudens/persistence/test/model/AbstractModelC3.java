package org.exqudens.persistence.test.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractModelC3 extends AbstractModelC2 {

    @Id
    @Column(name = "id")
    private Long id;

    protected AbstractModelC3(Long id) {
        super(id);
    }

}
