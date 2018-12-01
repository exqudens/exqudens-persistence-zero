package org.exqudens.util;

import java.io.Serializable;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface Table {

    static Table newInstance(String[] names, Serializable[][] rows) {
        return new Table() {
            @Override
            public Serializable[][] getRows() {
                return rows;
            }

            @Override
            public String[] getNames() {
                return names;
            }
        };
    }

    static Serializable[][] getRows(Table table, PredicateSerializable... predicates) {
        Predicate<Serializable[]> rowPredicate = row -> IntStream
        .range(0, row.length)
        .mapToObj(i -> predicates[i].test(row[i])).filter(b -> !b)
        .findAny()
        .orElse(true);

        return Stream
        .of(table.getRows())
        .filter(rowPredicate)
        .collect(Collectors.toList())
        .toArray(new Serializable[0][0]);
    }

    String[] getNames();
    Serializable[][] getRows();

    default Serializable[][] getRows(PredicateSerializable... predicates) {
        return getRows(this, predicates);
    }

}
