package org.exqudens.persistence.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.exqudens.persistence.test.model.ItemA;
import org.exqudens.persistence.test.model.ItemB;
import org.exqudens.persistence.test.model.ItemC;
import org.exqudens.persistence.test.model.OrderA;
import org.exqudens.persistence.test.model.OrderB;
import org.exqudens.persistence.test.model.OrderC;
import org.exqudens.persistence.test.model.OrderD;
import org.exqudens.persistence.test.model.SellerA;
import org.exqudens.persistence.test.model.UserA;
import org.exqudens.persistence.test.model.UserB;
import org.exqudens.persistence.test.model.UserC;
import org.exqudens.persistence.test.model.UserD;
import org.exqudens.persistence.util.Utils;
import org.exqudens.util.Table;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test1 {

    @Ignore
    @Test
    public void test0() {
        try {
            System.out.println(Stream.of(Thread.currentThread().getStackTrace()[1]).map(ste -> "line." + ste.getLineNumber() + ".class." + ste.getClassName() + ".method." + ste.getMethodName()).findFirst().get());
            System.out.println("---");

            List<Class<?>> entityClasses = Arrays.asList(
                SellerA.class,
                UserA.class,
                OrderA.class,
                ItemA.class,
                UserB.class,
                OrderB.class,
                ItemB.class,
                UserC.class,
                OrderC.class,
                ItemC.class,
                UserD.class,
                OrderD.class
            );

            List<UserB> users = new ArrayList<>();
            List<OrderB> orders = new ArrayList<>();
            List<ItemB> items = new ArrayList<>();

            users.add(new UserB(null, null, "email_1", null, orders));
            orders.add(new OrderB(null, null, "orderNumber_1", null, items));
            items.add(new ItemB(null, null, "description_1", null, users));
            items.add(new ItemB(null, null, "description_2", null, users));

            users.get(0).setItem(items.get(0));
            orders.get(0).setUser(users.get(0));
            items.get(0).setOrder(orders.get(0));
            items.get(1).setOrder(orders.get(0));

            List<Object> allGraphEntities = Utils.INSTANCE.getNodes(users.get(0), entityClasses);
            for (Object entity : allGraphEntities) {
                System.out.println(entity.toString());
            }

            byte[] byteArray = null;

            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
                    objectOutputStream.writeObject(users.get(0));
                    objectOutputStream.flush();
                    byteArrayOutputStream.flush();
                    byteArray = byteArrayOutputStream.toByteArray();
                }
            }

            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray)) {
                try (ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
                    users.add(UserB.class.cast(objectInputStream.readObject()));
                }
            }

            System.out.println(users.size());

            allGraphEntities = Utils.INSTANCE.getNodes(users.get(1), entityClasses);
            for (Object entity : allGraphEntities) {
                System.out.println(entity.toString());
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

    @Ignore
    @Test
    public void test1() {
        try {
            List<Class<?>> entityClasses = Arrays.asList(
                SellerA.class,
                UserA.class,
                OrderA.class,
                ItemA.class,
                UserB.class,
                OrderB.class,
                ItemB.class,
                UserC.class,
                OrderC.class,
                ItemC.class,
                UserD.class,
                OrderD.class
            );

            List<UserB> users = new ArrayList<>();
            List<OrderB> orders = new ArrayList<>();
            List<ItemB> items = new ArrayList<>();

            users.add(new UserB(null, null, "email_1", null, orders));
            orders.add(new OrderB(null, null, "orderNumber_1", null, items));
            items.add(new ItemB(null, null, "description_1", null, users));
            items.add(new ItemB(null, null, "description_2", null, users));

            users.get(0).setItem(items.get(0));
            orders.get(0).setUser(users.get(0));
            items.get(0).setOrder(orders.get(0));
            items.get(1).setOrder(orders.get(0));

            List<Object> allGraphEntities = Utils.INSTANCE.getNodes(users.get(0), entityClasses);
            System.out.println("---");
            for (Object entity : allGraphEntities) {
                System.out.println(entity.toString());
            }
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
    public void test2() {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        try {
            System.out.println(Stream.of(Thread.currentThread().getStackTrace()[1]).map(ste -> "line." + ste.getLineNumber() + ".class." + ste.getClassName() + ".method." + ste.getMethodName()).findFirst().get());
            System.out.println("---");

            List<Class<?>> entityClasses = Arrays.asList(
                SellerA.class,
                UserA.class,
                OrderA.class,
                ItemA.class
            );

            List<UserA> users = new ArrayList<>();
            List<SellerA> sellers = new ArrayList<>();
            List<OrderA> orders = new ArrayList<>();
            List<ItemA> items = new ArrayList<>();

            users.add(new UserA(null, null, "email_" + 1, new ArrayList<>()));

            sellers.add(new SellerA(null, null, "name_1", new ArrayList<>()));

            orders.add(new OrderA(null, null, "orderNumber_" + 1, null, null, new ArrayList<>()));
            orders.add(new OrderA(null, null, "orderNumber_" + 2, null, null, new ArrayList<>()));
            orders.add(new OrderA(null, null, "orderNumber_" + 3, null, null, new ArrayList<>()));

            items.add(new ItemA(null, null, "description_" + 1, null, null, new ArrayList<>()));
            items.add(new ItemA(null, null, "description_" + 2, null, null, new ArrayList<>()));
            items.add(new ItemA(null, null, "description_" + 3, null, null, new ArrayList<>()));

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

            emf = JpaTest.createEntityManagerFactory(entityClasses.toArray(new Class[0]));
            em = emf.createEntityManager();

            try {
                em.persist(users.get(0));
                em.persist(sellers.get(0));
                em.getTransaction().begin();
                em.flush();
                em.getTransaction().commit();
                em.clear();
                em.close();
            } catch (Exception e) {
                em.getTransaction().rollback();
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            users.clear();
            sellers.clear();
            orders.clear();
            items.clear();

            em = emf.createEntityManager();
            users.add(em.find(UserA.class, 1L));
            em.close();
            List<Object> allGraphEntities = Utils.INSTANCE.getNodes(users.get(0), entityClasses);
            for (Object entity : allGraphEntities) {
                System.out.println(entity.toString());
            }

            System.out.println("---");
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
            if (emf.isOpen()) {
                emf.close();
            }
        }
    }

    @Ignore
    @Test
    public void test3() {
        try {
            System.out.println(Stream.of(Thread.currentThread().getStackTrace()[1]).map(ste -> "line." + ste.getLineNumber() + ".class." + ste.getClassName() + ".method." + ste.getMethodName()).findFirst().get());
            System.out.println("---");

            String[] names = new String[] { "id", "uid", "name" };
            Serializable[][] rows = new Serializable[][] {
                { 1, "1", "aaa" },
                { 2, "2", "bbb" },
                { 3, "3", "ccc" }
            };
            Table table = Table.newInstance(names, rows);
            Object[][] result = table.getRows(o -> true, o -> true, o -> "aaa".equals(o));

            System.out.println(Stream.of(result).map(Arrays::toString).collect(Collectors.joining(System.lineSeparator())));

            System.out.println("---");
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Ignore
    @Test
    public void test4() {
        try {
            List<Class<?>> entityClasses = Arrays.asList(
                SellerA.class,
                UserA.class,
                OrderA.class,
                ItemA.class,
                UserB.class,
                OrderB.class,
                ItemB.class,
                UserC.class,
                OrderC.class,
                ItemC.class,
                UserD.class,
                OrderD.class
            );

            List<List<Class<?>>> graphs = Utils.INSTANCE.getGraphs(entityClasses);
            System.out.println("---");
            for (Object entity : graphs) {
                System.out.println(entity.toString());
            }
            System.out.println("---");

            List<UserD> users = new ArrayList<>();
            List<OrderD> orders = new ArrayList<>();

            users.add(new UserD(null, "user_1", orders));
            orders.add(new OrderD(null, "order_1", users));
            orders.add(new OrderD(null, "order_2", users));

            List<Class<?>> insertOrder = Utils.INSTANCE.getInsertOrder(orders.get(0).getClass(), entityClasses);
            System.out.println("---");
            System.out.println(insertOrder.toString());
            System.out.println("---");

            List<Object> allGraphEntities = Utils.INSTANCE.getNodes(users.get(0), entityClasses);
            System.out.println("---");
            for (Object entity : allGraphEntities) {
                System.out.println(entity.toString());
            }
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
    public void test5() {
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

            List<Class<?>> insertOrder = Utils.INSTANCE.getInsertOrder(orders.get(0).getClass(), entityClasses);
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
    public void test6() {
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

            List<Class<?>> insertOrder = Utils.INSTANCE.getInsertOrder(orders.get(0).getClass(), entityClasses);
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
