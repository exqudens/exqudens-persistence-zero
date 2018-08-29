package org.exqudens.persistence.test;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.exqudens.persistence.test.model.ItemA;
import org.exqudens.persistence.test.model.ItemB;
import org.exqudens.persistence.test.model.OrderA;
import org.exqudens.persistence.test.model.OrderB;
import org.exqudens.persistence.test.model.SellerA;
import org.exqudens.persistence.test.model.UserA;
import org.exqudens.persistence.test.model.UserB;
import org.exqudens.persistence.util.Utils;
import org.junit.Ignore;
import org.junit.Test;

public class TmpTestUtils {

    //@Ignore
    @Test
    public void test() {
        try {
            List<Class<?>> nodes;
            //nodes = Arrays.asList(SellerA.class, UserA.class, OrderA.class, ItemA.class);
            //nodes = Arrays.asList(UserB.class, OrderB.class, ItemB.class);
            nodes = Arrays.asList(SellerA.class, UserA.class, OrderA.class, ItemA.class, UserB.class, OrderB.class, ItemB.class);
            List<Class<? extends Annotation>> annotationClasses;
            //annotationClasses = Arrays.asList(ManyToOne.class);
            annotationClasses = Arrays.asList(OneToMany.class, ManyToOne.class, OneToOne.class, ManyToMany.class);
            List<Entry<Class<?>, Class<?>>> relations = Utils.getRelationsByFields(nodes, annotationClasses);
            System.out.println("---");
            System.out.println(relations.stream().map(Object::toString).collect(Collectors.joining(System.lineSeparator())));
            System.out.println("---");

            List<List<Class<?>>> graphs;
            graphs = Utils.toGraphs(relations);
            //graphs = Utils.toUniDirectionGraphs(relations);
            System.out.println("---");
            System.out.println(graphs.stream().map(Object::toString).collect(Collectors.joining(System.lineSeparator())));
            System.out.println("---");

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
