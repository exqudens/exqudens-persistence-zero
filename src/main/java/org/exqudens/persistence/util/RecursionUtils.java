package org.exqudens.persistence.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

public class RecursionUtils {

    public static Map<Integer, Object> getEntities(Object object, Map<Integer, Object> previous) {
        try {
            if (previous == null) {
                previous = new LinkedHashMap<>();
            }
            if (object instanceof Collection) {
                for (Object o : Collection.class.cast(object)) {
                    getEntities(o, previous);
                }
            } else {
                previous.putIfAbsent(System.identityHashCode(object), object);
                for (Field field : object.getClass().getDeclaredFields()) {
                    if (Collection.class.isAssignableFrom(field.getType())) {
                        String methodName = Arrays.asList(
                            "get",
                            field.getName().substring(0, 1).toUpperCase(),
                            field.getName().substring(1)
                        ).stream().collect(Collectors.joining());
                        Method method = object.getClass().getDeclaredMethod(methodName);
                        Object o = method.invoke(object);
                        getEntities(o, previous);
                    }
                }
            }
            return previous;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<Integer, Object> getMaps(Object object, Map<Integer, Object> previous) {
        try {
            if (previous == null) {
                previous = new LinkedHashMap<>();
            }
            if (Entry.class.isInstance(object)) {
                Object o = Entry.class.cast(object).getValue();
                if (!Map.class.isInstance(o)) {
                    getMaps(o, previous);
                }
            } else if (Map.class.isInstance(object)) {
                Object o = object;
                Integer identityHashCode = System.identityHashCode(o);
                if (!previous.containsKey(identityHashCode)) {
                    previous.put(identityHashCode, o);
                    getMaps(Map.class.cast(o).entrySet(), previous);
                }
            } else if (Collection.class.isInstance(object)) {
                for (Object o : Collection.class.cast(object)) {
                    getMaps(o, previous);
                }
            }
            return previous;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String toString(Object object) {
        try {
            Set<?> entrySet = Map.class.cast(object).entrySet();
            return entrySet.stream().map(Entry.class::cast).map(e -> {
                if (Map.class.isInstance(e.getValue())) {
                    return e.getKey().toString() + "=<Map>";
                } else if (Collection.class.isInstance(e.getValue())) {
                    return e.getKey().toString() + "=<Collection>";
                }
                return e.toString();
            }).collect(Collectors.joining(", ", "{ ", " }"));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private RecursionUtils() {
        super();
    }

}
