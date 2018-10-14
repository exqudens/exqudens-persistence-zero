package org.exqudens.persistence.test.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "order_c")
public class OrderC extends AbstractModelC3 {

    private UserC user;
    private List<ItemC> items;

    public OrderC() {
        this(null, null, null, null);
    }

    public OrderC(Long id, String name, UserC user, List<ItemC> items) {
        super(id, name);
        this.user = user;
        this.items = items;
    }

    @Column
    @Override
    public String getName() {
        return super.getName();
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @Fetch(FetchMode.SELECT)
    public UserC getUser() {
        return user;
    }

    public void setUser(UserC user) {
        this.user = user;
    }

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(FetchMode.SELECT)
    public List<ItemC> getItems() {
        return items;
    }

    public void setItems(List<ItemC> items) {
        this.items = items;
    }

}
