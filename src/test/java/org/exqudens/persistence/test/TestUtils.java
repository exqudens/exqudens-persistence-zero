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
            List<Class<? extends Annotation>> hierarchyIncludeAnnotationClasses = Arrays.asList(MappedSuperclass.class);
            List<Class<? extends Annotation>> hierarchyExcludeAnnotationClasses = Arrays.asList();
            List<Class<?>> ethalon = Arrays.asList(AbstractModelC1.class, AbstractModelC2.class, AbstractModelC3.class, OrderC.class);
            List<Class<?>> result = Utils.INSTANCE.hierarchy(OrderC.class, hierarchyIncludeAnnotationClasses, hierarchyExcludeAnnotationClasses);
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
            Class<?> entityClass = UserC.class;
            List<Class<? extends Annotation>> hierarchyIncludeAnnotationClasses = Arrays.asList(MappedSuperclass.class);
            List<Class<? extends Annotation>> hierarchyExcludeAnnotationClasses = Arrays.asList();
            List<Class<?>> hierarchy = Utils.INSTANCE.hierarchy(entityClass, hierarchyIncludeAnnotationClasses, hierarchyExcludeAnnotationClasses);
            List<Class<? extends Annotation>> includeAnnotationClasses;
            List<Class<? extends Annotation>> excludeAnnotationClasses = Arrays.asList(Transient.class);

            Set<String> ethalon;
            Set<String> result;

            includeAnnotationClasses = Arrays.asList(Column.class, OneToMany.class);
            ethalon = Arrays.asList("id", "name", "orders").stream().collect(Collectors.toSet());
            result = Utils.INSTANCE.fieldNames(hierarchy, includeAnnotationClasses, excludeAnnotationClasses, true);

            Assert.assertTrue(ethalon.containsAll(result));
            Assert.assertTrue(result.containsAll(ethalon));

            includeAnnotationClasses = Arrays.asList(Column.class);
            ethalon = Arrays.asList("id", "email", "name", "orders").stream().collect(Collectors.toSet());
            result = Utils.INSTANCE.fieldNames(hierarchy, includeAnnotationClasses, excludeAnnotationClasses, false);

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
            List<Class<? extends Annotation>> hierarchyIncludeAnnotationClasses = Arrays.asList(MappedSuperclass.class);
            List<Class<? extends Annotation>> hierarchyExcludeAnnotationClasses = Arrays.asList();
            List<Class<? extends Annotation>> fieldNamesIncludeAnnotationClasses = Arrays.asList(Column.class);
            List<Class<? extends Annotation>> fieldNamesExcludeAnnotationClasses = Arrays.asList(Transient.class);
            List<Class<? extends Annotation>> relationIncludeAnnotationClasses = Arrays.asList(OneToMany.class, ManyToOne.class, OneToOne.class, ManyToMany.class);
            List<Class<? extends Annotation>> relationExcludeAnnotationClasses = Arrays.asList();

            List<Entry<Class<?>, Class<?>>> ethalon;
            List<Class<?>> entityClasses;
            List<Entry<Class<?>, Class<?>>> result;

            ethalon = Arrays.asList(
                Utils.INSTANCE.entry(UserA.class, OrderA.class),
                Utils.INSTANCE.entry(SellerA.class, OrderA.class),
                Utils.INSTANCE.entry(OrderA.class, ItemA.class),
                Utils.INSTANCE.entry(UserB.class, ItemB.class),
                Utils.INSTANCE.entry(UserB.class, OrderB.class),
                Utils.INSTANCE.entry(OrderB.class, ItemB.class),
                Utils.INSTANCE.entry(UserC.class, OrderC.class),
                Utils.INSTANCE.entry(OrderC.class, ItemC.class)
            );
            entityClasses = Arrays.asList(SellerA.class, UserA.class, OrderA.class, ItemA.class, UserB.class, OrderB.class, ItemB.class, UserC.class, OrderC.class, ItemC.class);
            result = new ArrayList<>();
            for (Class<?> entityClass : entityClasses) {
                List<Class<?>> hierarchy = Utils.INSTANCE.hierarchy(entityClass, hierarchyIncludeAnnotationClasses, hierarchyExcludeAnnotationClasses);
                Set<String> fieldNames = Utils.INSTANCE.fieldNames(hierarchy, fieldNamesIncludeAnnotationClasses, fieldNamesExcludeAnnotationClasses, false);
                Utils.INSTANCE.relations(hierarchy, fieldNames, relationIncludeAnnotationClasses, relationExcludeAnnotationClasses, result);
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
                Utils.INSTANCE.entry(a, c),
                Utils.INSTANCE.entry(b, c),
                Utils.INSTANCE.entry(c, d),
                Utils.INSTANCE.entry(c, e),
                Utils.INSTANCE.entry(c, f),
                Utils.INSTANCE.entry(d, g),
                Utils.INSTANCE.entry(e, g),
                Utils.INSTANCE.entry(e, h),
                Utils.INSTANCE.entry(a, a)
            );
            List<Object> rootNodes = Arrays.asList(a, b);

            String ethalon = "[A, B, C, D, E, F, G, H]";
            String test = Utils.INSTANCE.breadthFirstSearch(nodes, relations, rootNodes).toString();

            Assert.assertTrue(ethalon.equals(test));

            ethalon = "[A, B, C, D, G, E, H, F]";
            test = Utils.INSTANCE.depthFirstSearch(nodes, relations, rootNodes).toString();

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
