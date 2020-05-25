package com.github.lkqm.auth.test;

import com.google.acai.Acai;
import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.dataset.IDataSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

import javax.inject.Inject;
import javax.sql.DataSource;

/**
 * 重要: 如果子类使用了@Before,@After注解, 需要手动调用:  super.setUp(), super.tearDown();
 */
@RunWith(JUnit4.class)
public abstract class AbstractDatabaseTest extends DataSourceBasedDBTestCase {

    @Rule
    public Acai acai = new Acai(DataSourceTestModule.class);

    @Inject
    protected EmbeddedDatabase dataSource;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Override
    protected DataSource getDataSource() {
        return dataSource;
    }

    @Override
    protected IDataSet getDataSet() throws Exception {
        return null;
    }

}
