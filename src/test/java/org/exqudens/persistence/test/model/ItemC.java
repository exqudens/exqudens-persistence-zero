package org.exqudens.persistence.test.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "item_c")
public class ItemC extends AbstractModelC3 {

    private OrderC order;

    public ItemC() {
        this(null, null, null);
    }

    public ItemC(Long id, String name, OrderC order) {
        super(id, name);
        this.order = order;
    }

    @Column
    @Override
    public String getName() {
        return super.getName();
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    @Fetch(FetchMode.SELECT)
    public OrderC getOrder() {
        return order;
    }

    public void setOrder(OrderC order) {
        this.order = order;
    }

}
