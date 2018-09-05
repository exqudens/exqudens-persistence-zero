package org.exqudens.persistence.test;

import java.util.List;

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

public class TmpTestUtils {

    //@Ignore
    @Test
    public void test00() {
        try {
            List<Class<?>> insertOrder = Utils.INSTANCE.insertOrder(OrderB.class, SellerA.class, UserA.class, OrderA.class, ItemA.class, UserB.class, OrderB.class, ItemB.class, UserC.class, OrderC.class, ItemC.class);
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
    public void test01() {
        try {
            System.out.println("test01");
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
