package com.github.lkqm.auth.test;

import com.google.inject.AbstractModule;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import javax.sql.DataSource;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2;

public class DataSourceTestModule extends AbstractModule {
    @Override
    protected void configure() {
        EmbeddedDatabase database = new EmbeddedDatabaseBuilder().setType(H2)
                .addScript("classpath:schema-mysql.sql")
                .build();

        bind(DataSource.class).toInstance(database);
        bind(EmbeddedDatabase.class).toInstance(database);
        bind(JdbcTemplate.class).toInstance(new JdbcTemplate(database));
    }
}
