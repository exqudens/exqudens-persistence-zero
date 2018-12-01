package org.exqudens.util;

import java.io.Serializable;
import java.util.function.Predicate;

public interface PredicateSerializable extends Predicate<Serializable> {

    static PredicateSerializable isEqual(Serializable serializable) {
        return s -> s.equals(serializable);
    }

}
