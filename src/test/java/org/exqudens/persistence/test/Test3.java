package org.exqudens.persistence.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.exqudens.persistence.test.model.transport.Item;
import org.exqudens.persistence.test.model.transport.Order;
import org.exqudens.persistence.util.Utils;
import org.junit.Test;

public class Test3 {

    @Test
    public void test99() {
        try {
            System.out.println(Stream.of(Thread.currentThread().getStackTrace()[1]).map(ste -> "line." + ste.getLineNumber() + ".class." + ste.getClassName() + ".method." + ste.getMethodName()).findFirst().get());
            System.out.println("---");

            List<Order> orders = new ArrayList<>();
            List<Item> items = new ArrayList<>();

            orders.add(new Order("number_1", items));
            items.add(new Item("description_1", orders.get(0)));
            items.add(new Item("description_2", orders.get(0)));

            List<Class<?>> entityClasses = Arrays.asList(Order.class, Item.class);

            List<Object> entities = Utils.INSTANCE.getNodes(System::identityHashCode, Object.class, orders.get(0), entityClasses, null, null, null, null, null, false, false, false, null, null);
            for (int i = 0; i < entities.size(); i++) {
                Object entity = entities.get(i);
                System.out.println(entity.toString());
            }

            /*List<Object> allGraphEntities = Utils.INSTANCE.getNodes(orders.get(0), entityClasses);

            System.out.println("---");
            for (Object entity : allGraphEntities) {
                System.out.println(entity.toString());
            }
            System.out.println("---");

            List<Node> nodes = new ArrayList<>();
            List<Link> links = new ArrayList<>();
            Graph graph = new Graph(nodes, links);

            String json = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(graph);

            System.out.println(json);*/

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
