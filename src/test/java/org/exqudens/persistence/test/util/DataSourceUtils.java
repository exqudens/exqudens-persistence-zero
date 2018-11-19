package org.exqudens.persistence.test.util;

import java.io.Closeable;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataSourceUtils {

    public static DataSource createDataSource(Map<String, Object> map) {
        Properties properties = new Properties();
        properties.putAll(map);
        return createDataSource(properties);
    }

    public static DataSource createDataSource(Properties properties) {
        HikariConfig hikariConfig = new HikariConfig(properties);
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        return dataSource;
    }

    public static void close(DataSource dataSource) {
        try {
            if (dataSource instanceof Closeable) {
                Closeable.class.cast(dataSource).close();
            } else if (dataSource instanceof AutoCloseable) {
                AutoCloseable.class.cast(dataSource).close();
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private DataSourceUtils() {
        super();
    }

}
