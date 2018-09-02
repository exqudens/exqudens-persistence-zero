package org.exqudens.persistence.test.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "user")
public class UserC extends AbstractModelC3 {

    private String email;
    private List<OrderC> orders;

    public UserC(Long id, String name, String email, List<OrderC> orders) {
        super(id, name);
        this.email = email;
        this.orders = orders;
    }

    public String getEmail() {
        return email;
    }

    @Column
    @Override
    public String getName() {
        return super.getName();
    }

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(FetchMode.SELECT)
    public List<OrderC> getOrders() {
        return orders;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setOrders(List<OrderC> orders) {
        this.orders = orders;
    }

}
