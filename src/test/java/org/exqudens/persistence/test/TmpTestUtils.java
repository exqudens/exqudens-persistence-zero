package org.exqudens.persistence.test;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.ManyToOne;

import org.exqudens.persistence.test.model.ItemA;
import org.exqudens.persistence.test.model.OrderA;
import org.exqudens.persistence.test.model.SellerA;
import org.exqudens.persistence.test.model.UserA;
import org.exqudens.persistence.util.Utils;
import org.junit.Ignore;
import org.junit.Test;

public class TmpTestUtils {

    //@Ignore
    @Test
    public void test() {
        try {
            List<Class<?>> nodes = Arrays.asList(SellerA.class, UserA.class, OrderA.class, ItemA.class);
            List<Entry<Class<?>, Class<?>>> relations = Utils.getRelationsByFields(nodes, Arrays.asList(ManyToOne.class));

            List<List<Class<?>>> graphs = Utils.toUniDirectionGraphs(relations);
            System.out.println(graphs);

            /*List<Class<?>> rootNodes = Arrays.asList(nodes.get(0), nodes.get(1));
            String result = Utils.breadthFirstSearch(nodes, relations, rootNodes).toString();
            System.out.println(result);*/
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
    public void test00() {
        try {
            System.out.println("test00");
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
