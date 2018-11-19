package org.exqudens.persistence.test.util;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.spi.IntegratorProvider;

public class PersistenceUnitInfoUtils {

    public static String HIBERNATE_PERSISTENCE_PROVIDER_CLASS_NAME;
    public static String ECLIPSELINK_PERSISTENCE_PROVIDER_CLASS_NAME;
    public static String OPENJPA_PERSISTENCE_PROVIDER_CLASS_NAME;

    static {
        HIBERNATE_PERSISTENCE_PROVIDER_CLASS_NAME = "org.hibernate.jpa.HibernatePersistenceProvider";
        ECLIPSELINK_PERSISTENCE_PROVIDER_CLASS_NAME = "org.eclipse.persistence.jpa.PersistenceProvider";
        OPENJPA_PERSISTENCE_PROVIDER_CLASS_NAME = "org.apache.openjpa.persistence.PersistenceProviderImpl";
    }

    public static PersistenceUnitInfo createHibernatePersistenceUnitInfo(
        String persistenceUnitName,
        DataSource nonJtaDataSource,
        DataSource jtaDataSource,
        Map<String, Object> properties,
        Class<?>... classes
    ) {
        try {
            Object integratorProviderClassName = properties.get(EntityManagerFactoryBuilderImpl.INTEGRATOR_PROVIDER);
            if (integratorProviderClassName != null) {
                Class<?> integratorProviderClass = Class.forName(integratorProviderClassName.toString());
                IntegratorProvider integratorProvider = IntegratorProvider.class.cast(
                    integratorProviderClass.newInstance()
                );
                properties.put(EntityManagerFactoryBuilderImpl.INTEGRATOR_PROVIDER, integratorProvider);
            }

            return createPersistenceUnitInfo(
                persistenceUnitName,
                HIBERNATE_PERSISTENCE_PROVIDER_CLASS_NAME,
                nonJtaDataSource,
                jtaDataSource,
                PersistenceUnitTransactionType.RESOURCE_LOCAL,
                properties,
                classes
            );
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static PersistenceUnitInfo createPersistenceUnitInfo(
        String persistenceUnitName,
        String persistenceProviderClassName,
        DataSource nonJtaDataSource,
        DataSource jtaDataSource,
        PersistenceUnitTransactionType persistenceUnitTransactionType,
        Map<String, ?> properties,
        Class<?>... classes
    ) {
        return new PersistenceUnitInfo() {

            @Override
            public String getPersistenceUnitName() {
                return persistenceUnitName;
            }

            @Override
            public String getPersistenceProviderClassName() {
                return persistenceProviderClassName;
            }

            @Override
            public DataSource getNonJtaDataSource() {
                return nonJtaDataSource;
            }

            @Override
            public DataSource getJtaDataSource() {
                return jtaDataSource;
            }

            @Override
            public PersistenceUnitTransactionType getTransactionType() {
                return persistenceUnitTransactionType;
            }

            @Override
            public Properties getProperties() {
                Properties p = new Properties();
                p.putAll(properties);
                return p;
            }

            @Override
            public List<String> getManagedClassNames() {
                List<String> list = Arrays.asList(classes).stream().map(Class::getName).collect(Collectors.toList());
                return list;
            }

            @Override
            public boolean excludeUnlistedClasses() {
                return true;
            }

            @Override
            public List<URL> getJarFileUrls() {
                List<URL> list;
                list = Collections.emptyList();
                return list;
            }

            @Override
            public List<String> getMappingFileNames() {
                return Collections.emptyList();
            }

            @Override
            public ValidationMode getValidationMode() {
                return null;
            }

            @Override
            public SharedCacheMode getSharedCacheMode() {
                return null;
            }

            @Override
            public String getPersistenceXMLSchemaVersion() {
                return null;
            }

            @Override
            public URL getPersistenceUnitRootUrl() {
                return null;
            }

            @Override
            public ClassLoader getClassLoader() {
                return null;
            }

            @Override
            public ClassLoader getNewTempClassLoader() {
                return null;
            }

            @Override
            public void addTransformer(ClassTransformer transformer) {
            }

        };
    }

    private PersistenceUnitInfoUtils() {
        super();
    }

}
