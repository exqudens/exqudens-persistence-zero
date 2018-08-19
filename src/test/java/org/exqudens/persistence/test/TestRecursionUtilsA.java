package org.exqudens.persistence.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.exqudens.persistence.test.model.a.Item;
import org.exqudens.persistence.test.model.a.Order;
import org.exqudens.persistence.test.model.a.Seller;
import org.exqudens.persistence.test.model.a.User;
import org.exqudens.persistence.util.RecursionUtils;
import org.junit.Test;

public class TestRecursionUtilsA {

    @Test
    public void testEntitiesA() {
        try {
            List<User> users = new ArrayList<>();
            List<Seller> sellers = new ArrayList<>();
            List<Order> orders = new ArrayList<>();
            List<Item> items = new ArrayList<>();

            users.add(new User(null, null, "email_" + 1, new ArrayList<>()));

            sellers.add(new Seller(null, null, "name_1", new ArrayList<>()));

            orders.add(new Order(null, null, "orderNumber_" + 1, null, null, new ArrayList<>()));
            orders.add(new Order(null, null, "orderNumber_" + 2, null, null, new ArrayList<>()));
            orders.add(new Order(null, null, "orderNumber_" + 3, null, null, new ArrayList<>()));

            items.add(new Item(null, null, "description_" + 1, null, null, new ArrayList<>()));
            items.add(new Item(null, null, "description_" + 2, null, null, new ArrayList<>()));
            items.add(new Item(null, null, "description_" + 3, null, null, new ArrayList<>()));

            users.get(0).getOrders().addAll(orders);

            sellers.get(0).getOrders().addAll(orders);

            orders.stream().forEach(o -> o.setUser(users.get(0)));
            orders.stream().forEach(o -> o.setSeller(sellers.get(0)));
            orders.get(1).setItems(items);

            items.stream().forEach(i -> i.setOrder(orders.get(1)));

            items.get(1).getChildren().add(items.get(0));
            items.get(1).getChildren().add(items.get(2));
            items.get(0).setParent(items.get(1));
            items.get(2).setParent(items.get(1));

            List<Object> objects;
            objects = RecursionUtils.getEntities(users.get(0), null).entrySet().stream().map(Entry::getValue).collect(
                Collectors.toList()
            );
            System.out.println("----------------------------------------------------");
            for (int i = 0; i < objects.size(); i++) {
                System.out.println(
                    (i + 1) + ") " + objects.get(i).getClass().getName() + " - " + objects.get(i).toString()
                );
            }
            System.out.println("----------------------------------------------------");
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
