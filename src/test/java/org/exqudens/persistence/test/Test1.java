package org.exqudens.persistence.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.exqudens.persistence.test.model.ItemA;
import org.exqudens.persistence.test.model.ItemB;
import org.exqudens.persistence.test.model.ItemC;
import org.exqudens.persistence.test.model.OrderA;
import org.exqudens.persistence.test.model.OrderB;
import org.exqudens.persistence.test.model.OrderC;
import org.exqudens.persistence.test.model.SellerA;
import org.exqudens.persistence.test.model.UserA;
import org.exqudens.persistence.test.model.UserB;
import org.exqudens.persistence.test.model.UserC;
import org.exqudens.persistence.util.Utils;
import org.junit.Ignore;
import org.junit.Test;

public class Test1 {

    //@Ignore
    @Test
    public void test00() {
        try {
            List<Class<?>> entityClasses = Arrays.asList(SellerA.class, UserA.class, OrderA.class, ItemA.class, UserB.class, OrderB.class, ItemB.class, UserC.class, OrderC.class, ItemC.class);

            List<UserB> users = new ArrayList<>();
            List<OrderB> orders = new ArrayList<>();
            List<ItemB> items = new ArrayList<>();

            //users.add(new UserA(null, null, "email_1", orders));
            //orders.add(new OrderA(null, null, "orderNumber_1", users.get(0), sellers.get(0), items));
            //items.add(new ItemA(null, null, "description_1", orders.get(0), null, new ArrayList<>()));

            users.add(new UserB(null, null, "email_1", null, orders));
            orders.add(new OrderB(null, null, "orderNumber_1", null, items));
            items.add(new ItemB(null, null, "description_1", null, users));

            users.get(0).setItem(items.get(0));
            orders.get(0).setUser(users.get(0));
            items.get(0).setOrder(orders.get(0));

            List<Class<?>> insertOrder = Utils.INSTANCE.insertOrder(true, orders.get(0).getClass(), entityClasses);
            System.out.println("---");
            System.out.println(insertOrder.toString());
            System.out.println("---");

            Map<Integer, Object> entities = Utils.INSTANCE.toMany(entityClasses, users.get(0), null);
            System.out.println(entities.size());
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Ignore
    @Test
    public void test01() {
        try {

            System.out.println("test01");

            List<Class<?>> entityClasses = Arrays.asList(SellerA.class, UserA.class, OrderA.class, ItemA.class, UserB.class, OrderB.class, ItemB.class, UserC.class, OrderC.class, ItemC.class);

            List<UserA> users = new ArrayList<>();
            List<SellerA> sellers = new ArrayList<>();
            List<OrderA> orders = new ArrayList<>();
            List<ItemA> items = new ArrayList<>();

            users.add(new UserA(null, null, "email_1", orders));
            sellers.add(new SellerA(null, null, "name_1", orders));
            orders.add(new OrderA(null, null, "orderNumber_1", users.get(0), sellers.get(0), items));
            items.add(new ItemA(null, null, "description_1", orders.get(0), null, new ArrayList<>()));

            List<Class<?>> insertOrder = Utils.INSTANCE.insertOrder(true, orders.get(0).getClass(), entityClasses);
            System.out.println("---");
            System.out.println(insertOrder.toString());
            System.out.println("---");

        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Ignore
    @Test
    public void test02() {
        try {
            System.out.println("test02");

            List<Class<?>> entityClasses = Arrays.asList(SellerA.class, UserA.class, OrderA.class, ItemA.class, UserB.class, OrderB.class, ItemB.class, UserC.class, OrderC.class, ItemC.class);

            List<UserB> users = new ArrayList<>();
            List<OrderB> orders = new ArrayList<>();
            List<ItemB> items = new ArrayList<>();

            users.add(new UserB(null, null, "email_1", null, orders));
            orders.add(new OrderB(null, null, "orderNumber_1", null, items));
            items.add(new ItemB(null, null, "description_1", null, users));

            users.get(0).setItem(items.get(0));
            orders.get(0).setUser(users.get(0));
            items.get(0).setOrder(orders.get(0));

            List<Class<?>> insertOrder = Utils.INSTANCE.insertOrder(true, orders.get(0).getClass(), entityClasses);
            System.out.println("---");
            System.out.println(insertOrder.toString());
            System.out.println("---");

        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}