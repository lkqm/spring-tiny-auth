package com.github.lkqm.auth.test;

import com.google.acai.Acai;
import org.junit.Rule;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

import javax.inject.Inject;

public abstract class AbstractDatabaseTest {

    @Rule
    public Acai acai = new Acai(DataSourceTestModule.class);

    @Inject
    protected EmbeddedDatabase dataSource;

}
