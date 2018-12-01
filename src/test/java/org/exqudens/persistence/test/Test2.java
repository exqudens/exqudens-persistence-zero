package org.exqudens.persistence.test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.exqudens.persistence.test.model.transport.Item;
import org.exqudens.persistence.test.model.transport.Order;
import org.junit.Test;

public class Test2 {

    @Test
    public void test99() {
        try {
            System.out.println(Stream.of(Thread.currentThread().getStackTrace()[1]).map(ste -> "line." + ste.getLineNumber() + ".class." + ste.getClassName() + ".method." + ste.getMethodName()).findFirst().get());
            System.out.println("---");

            List<Order> orders = new ArrayList<>();
            List<Item> items = new ArrayList<>();

            orders.add(new Order("number_1", new ArrayList<>()));
            items.add(new Item("description_1", orders.get(0)));
            items.add(new Item("description_2", orders.get(0)));

            System.out.println("---");
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
