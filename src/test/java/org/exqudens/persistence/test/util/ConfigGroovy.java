package org.exqudens.persistence.test.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;

public class ConfigGroovy implements Supplier<Map<String, Object>> {

    public static final String[] DATA_SOURCE_ALL_PROPERTY_KEYS;
    public static final String[] DATA_SOURCE_REMOVE_PROPERTY_KEYS;

    static {
        DATA_SOURCE_ALL_PROPERTY_KEYS = new String[] {
            "protocol",
            "subProtocol",

            "host",
            "port",
            "dbName",
            "jdbcUrlParams",

            "username",
            "password",
            "driverClassName",
            "connectionTimeout",
            "readOnly",
            "maximumPoolSize",
            "jdbcUrl"
        };
        DATA_SOURCE_REMOVE_PROPERTY_KEYS = new String[] {
            "protocol",
            "subProtocol",
            "host",
            "port",
            "dbName",
            "jdbcUrlParams"
        };
    }

    public static Builder builder() {
        return new Builder();
    }

    private String groovy;
    private String environment;
    private String[] dataSourceAllPropertyKeys;
    private String[] dataSourceRemovePropertyKeys;
    private Map<String, Object> config;

    private ConfigGroovy() {
        super();
        groovy = null;
        environment = null;
        dataSourceAllPropertyKeys = DATA_SOURCE_ALL_PROPERTY_KEYS;
        dataSourceRemovePropertyKeys = DATA_SOURCE_REMOVE_PROPERTY_KEYS;
        config = null;
    }

    @Override
    public Map<String, Object> get() {
        return config;
    }

    public Map<String, Object> retrieveProperties(String prefix) {
        Map<String, Object> properties = config.entrySet().stream().filter(e -> e.getKey().startsWith(prefix)).map(
            e -> {
                String newKey = e.getKey().replace(prefix, "");
                return new SimpleEntry<>(newKey, e.getValue());
            }
        ).collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), HashMap::putAll);

        return properties;
    }

    public Map<String, Object> createDataSourceProperties(
        String defaultsPrefix,
        String prefix,
        Function<String, String> passwordDecodeFunction
    ) {
        Map<String, Object> defaultMap = retrieveProperties(defaultsPrefix);
        Map<String, Object> map = retrieveProperties(prefix);

        for (String key : dataSourceAllPropertyKeys) {
            Object value = map.get(key) != null ? map.get(key).toString() : null;
            Object defaultValue = defaultMap.get(key);
            if (value != null) {
                if ("password".equals(key)) {
                    map.put(key, passwordDecodeFunction.apply(value.toString()));
                }
            } else if (defaultValue != null) {
                map.put(key, defaultValue);
            }
        }

        String key = "jdbcUrl";
        Object value = map.get(key) != null ? map.get(key).toString() : null;
        if (value == null) {
            value = Stream.of(
                map.get("protocol"),
                ":",
                map.get("subProtocol"),
                "://",
                map.get("host"),
                ":",
                map.get("port"),
                "/",
                map.get("dbName"),
                map.get("jdbcUrlParams"),
                "&failOverReadOnly=" + !Boolean.valueOf(map.get("readOnly").toString())
            ).map(Object::toString).collect(Collectors.joining());
        }
        map.put(key, value);

        Stream.of(dataSourceRemovePropertyKeys).forEach(k -> map.remove(k));

        return map;
    }

    public static class Builder {

        private final ConfigGroovy configGroovy;

        private Builder() {
            super();
            this.configGroovy = new ConfigGroovy();
        }

        public Builder groovy(String groovy) {
            configGroovy.groovy = groovy;
            return this;
        }

        public Builder environment(String environment) {
            configGroovy.environment = environment;
            return this;
        }

        public Builder dataSourceAllPropertyKeys(String... dataSourceAllPropertyKeys) {
            configGroovy.dataSourceAllPropertyKeys = dataSourceAllPropertyKeys;
            return this;
        }

        public Builder dataSourceRemovePropertyKeys(String... dataSourceRemovePropertyKeys) {
            configGroovy.dataSourceRemovePropertyKeys = dataSourceRemovePropertyKeys;
            return this;
        }

        public ConfigGroovy build() {
            ConfigSlurper configSlurper = new ConfigSlurper(configGroovy.environment != null ? configGroovy.environment : "ALL");
            ConfigObject configObject = configSlurper.parse(configGroovy.groovy);
            Map<?, ?> raw = configObject.flatten();

            Map<String, Object> config = raw.entrySet().stream().map(
                e -> new SimpleEntry<String, Object>(e.getKey().toString(), e.getValue())
            ).collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), HashMap::putAll);

            configGroovy.config = Collections.unmodifiableMap(config);
            return configGroovy;
        }

    }

}
