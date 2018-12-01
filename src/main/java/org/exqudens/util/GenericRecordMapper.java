package org.exqudens.util;

import java.util.Objects;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.springframework.beans.PropertyAccessorFactory;

public class GenericRecordMapper {

    public static <T> GenericRecord mapObjectToRecord(T entity, Schema schema) {
        try {
            Objects.requireNonNull(entity, "'object' must not be null");
            Objects.requireNonNull(schema, "'schema' must not be null");
            GenericData.Record record = new GenericData.Record(schema);
            schema.getFields().forEach(r -> record.put(r.name(), PropertyAccessorFactory.forDirectFieldAccess(entity).getPropertyValue(r.name())));
            return record;
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T mapRecordToObject(GenericRecord record, Schema schema, Class<T> entityClass) {
        try {
            Objects.requireNonNull(record, "'record' must not be null");
            Objects.requireNonNull(schema, "'schema' must not be null");
            Objects.requireNonNull(entityClass, "'entityClass' must not be null");
            T entity = entityClass.newInstance();
            record.getSchema().getFields().forEach(d -> PropertyAccessorFactory.forDirectFieldAccess(entity).setPropertyValue(d.name(), record.get(d.name()) == null ? record.get(d.name()) : record.get(d.name()).toString()));
            return entity;
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
