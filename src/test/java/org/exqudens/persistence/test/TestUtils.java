package org.exqudens.persistence.test;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.exqudens.persistence.test.model.AbstractModelC1;
import org.exqudens.persistence.test.model.AbstractModelC2;
import org.exqudens.persistence.test.model.AbstractModelC3;
import org.exqudens.persistence.test.model.ItemA;
import org.exqudens.persistence.test.model.ItemB;
import org.exqudens.persistence.test.model.ItemC;
import org.exqudens.persistence.test.model.OrderC;
import org.exqudens.persistence.test.model.OrderA;
import org.exqudens.persistence.test.model.OrderB;
import org.exqudens.persistence.test.model.SellerA;
import org.exqudens.persistence.test.model.UserA;
import org.exqudens.persistence.test.model.UserB;
import org.exqudens.persistence.test.model.UserC;
import org.exqudens.persistence.util.Utils;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestUtils {

    @Test
    public void test01HierarchyC() {
        try {
            List<Class<?>> ethalon = Arrays.asList(AbstractModelC1.class, AbstractModelC2.class, AbstractModelC3.class, OrderC.class);
            List<Class<?>> result = Utils.hierarchy(OrderC.class, Arrays.asList(MappedSuperclass.class));
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
    public void test02FieldNames() {
        try {
            Set<String> ethalon = Arrays.asList("id", "name", "orders").stream().collect(Collectors.toSet());
            Class<?> entityClass = UserC.class;
            List<Class<? extends Annotation>> hierarchyAnnotationClasses = Arrays.asList(MappedSuperclass.class);
            List<Class<?>> hierarchy = Utils.hierarchy(entityClass, hierarchyAnnotationClasses);
            List<Class<? extends Annotation>> includeAnnotationClasses = Arrays.asList(Column.class);
            List<Class<? extends Annotation>> excludeAnnotationClasses = Arrays.asList(Transient.class);
            Set<String> result = Utils.fieldNames(hierarchy, includeAnnotationClasses, excludeAnnotationClasses);
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
    public void test03Relations() {
        try {
            List<Class<? extends Annotation>> hierarchyAnnotationClasses = Arrays.asList(MappedSuperclass.class);
            List<Class<? extends Annotation>> includeAnnotationClasses = Arrays.asList(Column.class);
            List<Class<? extends Annotation>> excludeAnnotationClasses = Arrays.asList(Transient.class);
            List<Class<? extends Annotation>> relationAnnotationClasses = Arrays.asList(OneToMany.class, ManyToOne.class, OneToOne.class, ManyToMany.class);

            List<Entry<Class<?>, Class<?>>> ethalon;
            List<Class<?>> entityClasses;
            List<Entry<Class<?>, Class<?>>> result;

            ethalon = Arrays.asList(
                Utils.entry(UserA.class, OrderA.class),
                Utils.entry(SellerA.class, OrderA.class),
                Utils.entry(OrderA.class, ItemA.class),
                Utils.entry(UserB.class, ItemB.class),
                Utils.entry(UserB.class, OrderB.class),
                Utils.entry(OrderB.class, ItemB.class),
                Utils.entry(UserC.class, OrderC.class),
                Utils.entry(OrderC.class, ItemC.class)
            );
            entityClasses = Arrays.asList(SellerA.class, UserA.class, OrderA.class, ItemA.class, UserB.class, OrderB.class, ItemB.class, UserC.class, OrderC.class, ItemC.class);
            result = new ArrayList<>();
            for (Class<?> entityClass : entityClasses) {
                List<Class<?>> hierarchy = Utils.hierarchy(entityClass, hierarchyAnnotationClasses);
                Set<String> fieldNames = Utils.fieldNames(hierarchy, includeAnnotationClasses, excludeAnnotationClasses);
                Utils.relations(hierarchy, fieldNames, relationAnnotationClasses, result);
            }

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
        } finally {
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
                Utils.entry(a, c),
                Utils.entry(b, c),
                Utils.entry(c, d),
                Utils.entry(c, e),
                Utils.entry(c, f),
                Utils.entry(d, g),
                Utils.entry(e, g),
                Utils.entry(e, h),
                Utils.entry(a, a)
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
