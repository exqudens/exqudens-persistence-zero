package org.exqudens.persistence.test;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.exqudens.persistence.test.model.ItemA;
import org.exqudens.persistence.test.model.OrderA;
import org.exqudens.persistence.test.model.SellerA;
import org.exqudens.persistence.test.model.UserA;
import org.exqudens.persistence.util.Graph;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestGraph {

    @Ignore
    @Test
    public void test0() {
        Assert.assertTrue(true);
    }

    //@Ignore
    @Test
    public void test1() {
        Object a = 'A';
        Object b = 'B';
        Object c = 'C';
        Object d = 'D';
        Object e = 'E';
        Object f = 'F';
        Object g = 'G';
        Object h = 'H';

        List<Object> nodes = Arrays.asList(a, b, c, d, e, f, g, h);
        List<Entry<Object, Object>> relations = Arrays.asList(
            Graph.entry(a, c),
            Graph.entry(b, c),
            Graph.entry(c, d),
            Graph.entry(c, e),
            Graph.entry(c, f),
            Graph.entry(d, g),
            Graph.entry(e, g),
            Graph.entry(e, h),
            Graph.entry(a, a)
        );
        List<Object> rootNodes = Arrays.asList(a, b);

        String ethalon = "[A, B, C, D, E, F, G, H]";
        String test = new Graph<>(nodes, relations).breadthFirstSearch(rootNodes).toString();
        System.out.print(test);
        Assert.assertTrue(ethalon.equals(test));
        System.out.println(" - OK");

        ethalon = "[A, B, C, D, G, E, H, F]";
        test = new Graph<>(nodes, relations).depthFirstSearch(rootNodes).toString();
        System.out.print(test);
        Assert.assertTrue(ethalon.equals(test));
        System.out.println(" - OK");
    }

    //@Ignore
    @Test
    public void test2() {
        Object user = UserA.class.getSimpleName();
        Object seller = SellerA.class.getSimpleName();
        Object order = OrderA.class.getSimpleName();
        Object item = ItemA.class.getSimpleName();

        List<Object> nodes = Arrays.asList(user, seller, order, item);
        List<Entry<Object, Object>> relations = Arrays.asList(
            Graph.entry(user, order),
            Graph.entry(seller, order),
            Graph.entry(order, item)
        );
        List<Object> rootNodes = Arrays.asList(user, seller);

        String ethalon = "[User, Seller, Order, Item]";
        String test = new Graph<>(nodes, relations).breadthFirstSearch(rootNodes).toString();
        System.out.print(test);
        Assert.assertTrue(ethalon.equals(test));
        System.out.println(" - OK");

        ethalon = "[User, Seller, Order, Item]";
        test = new Graph<>(nodes, relations).depthFirstSearch(rootNodes).toString();
        System.out.print(test);
        Assert.assertTrue(ethalon.equals(test));
        System.out.println(" - OK");
    }

}
