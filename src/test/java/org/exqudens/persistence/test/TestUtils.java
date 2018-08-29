package org.exqudens.persistence.test;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.exqudens.persistence.test.model.AbstractModelC1;
import org.exqudens.persistence.test.model.AbstractModelC2;
import org.exqudens.persistence.test.model.AbstractModelC3;
import org.exqudens.persistence.test.model.ItemA;
import org.exqudens.persistence.test.model.ItemB;
import org.exqudens.persistence.test.model.ModelC;
import org.exqudens.persistence.test.model.OrderA;
import org.exqudens.persistence.test.model.OrderB;
import org.exqudens.persistence.test.model.SellerA;
import org.exqudens.persistence.test.model.UserA;
import org.exqudens.persistence.test.model.UserB;
import org.exqudens.persistence.util.Utils;
import org.junit.Assert;
import org.junit.Test;

public class TestUtils {

    @Test
    public void testGetRelationsByFieldsA() {
        try {
            List<String> ethalon = Arrays.asList(
                OrderA.class.getName() + "=" + UserA.class.getName(),
                OrderA.class.getName() + "=" + SellerA.class.getName(),
                ItemA.class.getName() + "=" + OrderA.class.getName()
            );

            List<Class<?>> classes = Arrays.asList(SellerA.class, UserA.class, OrderA.class, ItemA.class);
            List<Class<? extends Annotation>> annotationClasses = Arrays.asList(ManyToOne.class);
            List<Entry<Class<?>, Class<?>>> relationsByFields = Utils.getRelationsByFields(classes, annotationClasses);
            List<String> result = relationsByFields
            .stream()
            .map(entry -> Utils.toEntry(entry.getKey().getName(), entry.getValue().getName()))
            .map(Object::toString)
            .collect(Collectors.toList());

            Assert.assertTrue(ethalon.containsAll(result));
            Assert.assertTrue(result.containsAll(ethalon));
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

    @Test
    public void testGetRelationsByFieldsB() {
        try {
            List<String> ethalon = Arrays.asList(
                UserB.class.getName() + "=" + ItemB.class.getName(),
                OrderB.class.getName() + "=" + UserB.class.getName(),
                ItemB.class.getName() + "=" + OrderB.class.getName()
            );

            List<Class<?>> classes = Arrays.asList(UserB.class, OrderB.class, ItemB.class);
            List<Class<? extends Annotation>> annotationClasses = Arrays.asList(ManyToOne.class);
            List<Entry<Class<?>, Class<?>>> relationsByFields = Utils.getRelationsByFields(classes, annotationClasses);
            List<String> result = relationsByFields
            .stream()
            .map(entry -> Utils.toEntry(entry.getKey().getName(), entry.getValue().getName()))
            .map(Object::toString)
            .collect(Collectors.toList());

            Assert.assertTrue(ethalon.containsAll(result));
            Assert.assertTrue(result.containsAll(ethalon));
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

    @Test
    public void testGetHierarchyC() {
        try {
            List<Class<?>> ethalon = Arrays.asList(AbstractModelC1.class, AbstractModelC2.class, AbstractModelC3.class, ModelC.class);
            List<Class<?>> result = Utils.getHierarchy(ModelC.class, Arrays.asList(MappedSuperclass.class));
            Assert.assertTrue(ethalon.containsAll(result));
            Assert.assertTrue(result.containsAll(ethalon));
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

    @Test
    public void testBreadthAndDepthFirstSearch() {
        try {
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
                Utils.toEntry(a, c),
                Utils.toEntry(b, c),
                Utils.toEntry(c, d),
                Utils.toEntry(c, e),
                Utils.toEntry(c, f),
                Utils.toEntry(d, g),
                Utils.toEntry(e, g),
                Utils.toEntry(e, h),
                Utils.toEntry(a, a)
            );
            List<Object> rootNodes = Arrays.asList(a, b);

            String ethalon = "[A, B, C, D, E, F, G, H]";
            String test = Utils.breadthFirstSearch(nodes, relations, rootNodes).toString();

            Assert.assertTrue(ethalon.equals(test));

            ethalon = "[A, B, C, D, G, E, H, F]";
            test = Utils.depthFirstSearch(nodes, relations, rootNodes).toString();

            Assert.assertTrue(ethalon.equals(test));
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
