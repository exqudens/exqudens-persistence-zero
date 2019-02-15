package org.exqudens.persistence.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.exqudens.persistence.util.Utils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test1 {

    @Test
    public void test99() {
        try {

            List<Class<?>> entityClasses = Arrays.asList(
                Provider.class,
                Region.class,
                User.class,
                Email.class,
                Order.class,
                Item.class,
                Parcel.class
            );

            List<Provider> providers = new ArrayList<>();
            List<Region> regions = new ArrayList<>();
            List<User> users = new ArrayList<>();
            List<Email> emails = new ArrayList<>();
            List<Order> orders = new ArrayList<>();
            List<Item> items = new ArrayList<>();
            List<Parcel> parcels = new ArrayList<>();

            providers.add(new Provider(null, "provider_1", new ArrayList<>()));
            regions.add(new Region(null, "region_1", new ArrayList<>()));
            users.add(new User(null, "user_1", null, null, new ArrayList<>(), new ArrayList<>()));
            users.add(new User(null, "user_2", null, null, new ArrayList<>(), new ArrayList<>()));
            emails.add(new Email(null, "email_1", new ArrayList<>()));
            emails.add(new Email(null, "email_2", new ArrayList<>()));
            emails.add(new Email(null, "email_3", new ArrayList<>()));
            orders.add(new Order(null, "order_1", null, null, new ArrayList<>()));
            orders.add(new Order(null, "order_2", null, null, new ArrayList<>()));
            items.add(new Item(null, "item_1", null, new ArrayList<>()));
            items.add(new Item(null, "item_2", null, new ArrayList<>()));
            items.add(new Item(null, "item_3", null, new ArrayList<>()));
            parcels.add(new Parcel(null, "parcel_1", null, new ArrayList<>()));
            parcels.add(new Parcel(null, "parcel_2", null, new ArrayList<>()));

            providers.get(0).getUsers().add(users.get(0));
            providers.get(0).getUsers().add(users.get(1));
            regions.get(0).getUsers().add(users.get(0));
            regions.get(0).getUsers().add(users.get(1));
            users.get(0).setProvider(providers.get(0));
            users.get(1).setProvider(providers.get(0));
            users.get(0).setRegion(regions.get(0));
            users.get(1).setRegion(regions.get(0));
            users.get(0).getEmails().add(emails.get(0));
            users.get(0).getEmails().add(emails.get(1));
            users.get(1).getEmails().add(emails.get(2));
            users.get(0).getOrders().add(orders.get(0));
            users.get(1).getOrders().add(orders.get(1));
            emails.get(0).getUsers().add(users.get(0));
            emails.get(1).getUsers().add(users.get(0));
            emails.get(2).getUsers().add(users.get(1));
            orders.get(0).setUser(users.get(0));
            orders.get(1).setUser(users.get(1));
            orders.get(0).setParcel(parcels.get(0));
            orders.get(1).setParcel(parcels.get(1));
            orders.get(0).getItems().add(items.get(0));
            orders.get(0).getItems().add(items.get(1));
            orders.get(1).getItems().add(items.get(2));
            items.get(0).setOrder(orders.get(0));
            items.get(1).setOrder(orders.get(0));
            items.get(2).setOrder(orders.get(1));
            items.get(0).getParcels().add(parcels.get(0));
            items.get(1).getParcels().add(parcels.get(0));
            items.get(2).getParcels().add(parcels.get(1));
            parcels.get(0).setItem(items.get(0));
            parcels.get(1).setItem(items.get(2));
            parcels.get(0).getOrders().add(orders.get(0));
            parcels.get(1).getOrders().add(orders.get(1));

            List<Object> nodes = Utils.INSTANCE.getNodes(regions.get(0), entityClasses);
            System.out.println("---");
            System.out.println(nodes.size());
            System.out.println("---");
            for (Object node : nodes) {
                System.out.println(node.toString());
            }
            System.out.println("---");

        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @ToString(of = "name")
    public class Provider {

        @Id
        private Long id;

        private String name;

        @OneToMany
        private List<User> users;

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @ToString(of = "name")
    public class Region {

        @Id
        private Long id;

        private String name;

        @OneToMany
        private List<User> users;

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @ToString(of = "name")
    public class User {

        @Id
        private Long id;

        private String name;

        @ManyToOne
        @JoinColumn
        private Provider provider;

        @ManyToOne
        @JoinColumn
        private Region region;

        @ManyToMany
        @JoinTable
        private List<Email> emails;

        @OneToMany
        private List<Order> orders;

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @ToString(of = "name")
    public class Email {

        @Id
        private Long id;

        private String name;

        @ManyToMany
        private List<User> users;

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @ToString(of = "name")
    public class Order {

        @Id
        private Long id;

        private String name;

        @ManyToOne
        @JoinColumn
        private User user;

        @ManyToOne
        @JoinColumn
        private Parcel parcel;

        @OneToMany
        private List<Item> items;

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @ToString(of = "name")
    public class Item {

        @Id
        private Long id;

        private String name;

        @ManyToOne
        @JoinColumn
        private Order order;

        @OneToMany
        private List<Parcel> parcels;

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @ToString(of = "name")
    public class Parcel {

        @Id
        private Long id;

        private String name;

        @ManyToOne
        @JoinColumn
        private Item item;

        @OneToMany
        private List<Order> orders;

    }

}
