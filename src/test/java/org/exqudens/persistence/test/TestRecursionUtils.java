package org.exqudens.persistence.test;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.exqudens.persistence.test.model.a.Item;
import org.exqudens.persistence.test.model.a.Order;
import org.exqudens.persistence.test.model.a.Seller;
import org.exqudens.persistence.test.model.a.User;
import org.exqudens.persistence.util.RecursionUtils;
import org.junit.Ignore;
import org.junit.Test;

public class TestRecursionUtils {

    @Ignore
    @Test
    public void testEntitiesA() {
        try {
            List<User> users = new ArrayList<>();
            List<Seller> sellers = new ArrayList<>();
            List<Order> orders = new ArrayList<>();
            List<Item> items = new ArrayList<>();

            users.add(new User(null, null, "email_" + 1, new ArrayList<>()));

            sellers.add(new Seller(null, null, "name_" + 1, new ArrayList<>()));

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

    @Test
    public void testMaps() {
        try {
            List<Object> users = new ArrayList<>();

            AtomicLong userId = new AtomicLong(1);
            AtomicLong orderId = new AtomicLong(1);
            AtomicLong itemId = new AtomicLong(1);

            for (int i = 1; i <= 1; i++) {
                List<Object> orders = new ArrayList<>();
                Map<String, Object> user = Stream.of(
                    new SimpleEntry<>("id", userId.get()),
                    new SimpleEntry<>("email", "email_" + userId.getAndIncrement()),
                    new SimpleEntry<>("orders", orders)
                ).collect(LinkedHashMap::new, (map, entry) -> {
                    map.put(entry.getKey(), entry.getValue());
                }, LinkedHashMap::putAll);

                for (int ii = 1; ii <= 2; ii++) {
                    List<Object> items = new ArrayList<>();
                    Map<String, Object> order = Stream.of(
                        new SimpleEntry<>("id", orderId.get()),
                        new SimpleEntry<>("orderNumber", "orderNumber_" + orderId.getAndIncrement()),
                        new SimpleEntry<>("items", items)
                    ).collect(LinkedHashMap::new, (map, entry) -> {
                        map.put(entry.getKey(), entry.getValue());
                    }, LinkedHashMap::putAll);

                    for (int iii = 1; iii <= 3; iii++) {
                        Map<String, Object> item = Stream.of(
                            new SimpleEntry<>("id", itemId.get()),
                            new SimpleEntry<>("description", "description_" + itemId.getAndIncrement()),
                            new SimpleEntry<>("parent", null),
                            new SimpleEntry<>("children", new ArrayList<>())
                        ).collect(LinkedHashMap::new, (map, entry) -> {
                            map.put(entry.getKey(), entry.getValue());
                        }, LinkedHashMap::putAll);
                        items.add(item);
                    }

                    @SuppressWarnings("unchecked")
                    Map<String, Object> item1 = (Map<String, Object>) items.get(0);
                    @SuppressWarnings("unchecked")
                    Map<String, Object> item2 = (Map<String, Object>) items.get(0);
                    @SuppressWarnings("unchecked")
                    Map<String, Object> item3 = (Map<String, Object>) items.get(0);

                    @SuppressWarnings("unchecked")
                    List<Object> children1 = (List<Object>) item1.get("children");
                    //List<Object> children2 = (List<Object>) item2.get("children");
                    @SuppressWarnings("unchecked")
                    List<Object> children3 = (List<Object>) item3.get("children");

                    item2.put("parent", item1);
                    item3.put("parent", item1);

                    children1.add(item2);
                    children1.add(item3);

                    item1.put("parent", item3);
                    children3.add(item1);

                    orders.add(order);
                }

                users.add(user);
            }

            List<Object> objects;
            objects = RecursionUtils.getMaps(users.get(0), null).entrySet().stream().map(Entry::getValue).collect(
                Collectors.toList()
            );
            System.out.println("----------------------------------------------------");
            for (int i = 0; i < objects.size(); i++) {
                System.out.println((i + 1) + ") " + RecursionUtils.toString(objects.get(i)));
            }
            System.out.println("----------------------------------------------------");
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
