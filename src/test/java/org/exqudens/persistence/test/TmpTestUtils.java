package org.exqudens.persistence.test;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

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
            List<Class<?>> entityClasses = Arrays.asList(SellerA.class, UserA.class, OrderA.class, ItemA.class, UserB.class, OrderB.class, ItemB.class, UserC.class, OrderC.class, ItemC.class);
            List<Class<? extends Annotation>> hierarchyAnnotationClasses = Arrays.asList(MappedSuperclass.class);
            List<Class<? extends Annotation>> includeAnnotationClasses = Arrays.asList(Column.class);
            List<Class<? extends Annotation>> excludeAnnotationClasses = Arrays.asList(Transient.class);
            List<Class<? extends Annotation>> relationAnnotationClasses = Arrays.asList(OneToMany.class, ManyToOne.class);
            List<Entry<Class<?>, Class<?>>> relations = new ArrayList<>();
            for (Class<?> entityClass : entityClasses) {
                List<Class<?>> hierarchy = Utils.hierarchy(entityClass, hierarchyAnnotationClasses);
                Set<String> fieldNames = Utils.fieldNames(hierarchy, includeAnnotationClasses, excludeAnnotationClasses);
                Utils.relations(hierarchy, fieldNames, relationAnnotationClasses, relations);
            }
            List<List<Class<?>>> graphs = Utils.graphs(relations);
            System.out.println("---");
            System.out.println(graphs.stream().map(Object::toString).collect(Collectors.joining(System.lineSeparator())));
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
