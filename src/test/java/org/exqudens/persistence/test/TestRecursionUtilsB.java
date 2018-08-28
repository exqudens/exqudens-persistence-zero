package org.exqudens.persistence.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.exqudens.persistence.test.model.ItemB;
import org.exqudens.persistence.test.model.OrderB;
import org.exqudens.persistence.test.model.UserB;
import org.exqudens.persistence.util.RecursionUtils;
import org.junit.Test;

public class TestRecursionUtilsB {

    @Test
    public void testEntitiesB() {
        try {
            List<UserB> users = new ArrayList<>();
            List<OrderB> orders = new ArrayList<>();
            List<ItemB> items = new ArrayList<>();

            users.add(new UserB(null, null, "email_" + 1, null, new ArrayList<>()));

            orders.add(new OrderB(null, null, "orderNumber_" + 1, null, new ArrayList<>()));
            orders.add(new OrderB(null, null, "orderNumber_" + 2, null, new ArrayList<>()));

            items.add(new ItemB(null, null, "description_" + 1, null, new ArrayList<>()));
            items.add(new ItemB(null, null, "description_" + 2, null, new ArrayList<>()));
            items.add(new ItemB(null, null, "description_" + 3, null, new ArrayList<>()));

            users.get(0).getOrders().addAll(orders);

            orders.get(0).getItems().addAll(items);

            orders.stream().forEach(o -> o.setUser(users.get(0)));

            items.stream().forEach(i -> i.setOrder(orders.get(0)));

            items.get(2).getUsers().addAll(users);

            users.get(0).setItem(items.get(2));

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
