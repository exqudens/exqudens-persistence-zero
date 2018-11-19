package org.exqudens.persistence.test;

import java.util.Map;
import java.util.function.Function;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;

import org.exqudens.persistence.test.util.ClassPathUtils;
import org.exqudens.persistence.test.util.ConfigGroovy;
import org.exqudens.persistence.test.util.DataSourceUtils;
import org.exqudens.persistence.test.util.PersistenceUnitInfoUtils;

interface JpaTest {

    static final String DS_DEFAULTS_PREFIX = "dataSources.mysqlDefaults.";
    static final String DS_PREFIX          = "dataSources.masterTestDataSource.";
    static final String JPA_PREFIX         = "jpaProviders.hibernateJpaProvider.properties.";

    static EntityManagerFactory createEntityManagerFactory(Class<?>... classes) {
        return createEntityManagerFactory(DS_DEFAULTS_PREFIX, DS_PREFIX, JPA_PREFIX, classes);
    }

    static EntityManagerFactory createEntityManagerFactory(
        String dataSourceDefaultsPrefix,
        String dataSourcePrefix,
        String jpaPrefix,
        Class<?>... classes
    ) {
        try {
            ConfigGroovy configGroovy = ConfigGroovy.builder().groovy(ClassPathUtils.toString("config-test.groovy")).build();

            DataSource dataSource = DataSourceUtils.createDataSource(
                configGroovy.createDataSourceProperties(dataSourceDefaultsPrefix, dataSourcePrefix, Function.identity())
            );

            Map<String, Object> properties = configGroovy.retrieveProperties(jpaPrefix);

            PersistenceUnitInfo info = PersistenceUnitInfoUtils.createHibernatePersistenceUnitInfo(
                "default",
                dataSource,
                null,
                properties,
                classes
            );

            ClassLoader cl = PersistenceUnitInfoUtils.class.getClassLoader();
            Object o = cl.loadClass(info.getPersistenceProviderClassName()).newInstance();
            PersistenceProvider persistenceProvider = PersistenceProvider.class.cast(o);
            EntityManagerFactory emf = persistenceProvider.createContainerEntityManagerFactory(
                info,
                info.getProperties()
            );
            return emf;
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
