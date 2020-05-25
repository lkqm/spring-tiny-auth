package com.github.lkqm.auth.token.support;

import com.github.lkqm.auth.test.AbstractDatabaseTest;
import com.github.lkqm.auth.test.DbUnitUtils;
import lombok.SneakyThrows;
import org.dbunit.Assertion;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Inject;

public class TokenInfoRepositoryTest extends AbstractDatabaseTest {

    @Inject
    JdbcTemplate jdbcTemplate;
    TokenInfoRepository tokenInfoRepository;

    private static final String tableName = "tiny_auth_token";
    private static final String xmlDir = "dbunit/TokenInfoRepository/";

    @Override
    protected IDataSet getDataSet() throws Exception {
        return DbUnitUtils.readFlatXmlDataSet(xmlDir + "pre.xml");
    }

    @Before
    public void init() throws Exception {
        super.setUp();
        tokenInfoRepository = new TokenInfoRepository(jdbcTemplate);
    }

    @Test
    @SneakyThrows
    public void testSave() {
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setToken("4");
        tokenInfo.setData("123");
        tokenInfo.setIssueTimestamp(1590330998266L);
        tokenInfo.setExpireTimestamp(1590332798266L);

        // 更新插入新
        tokenInfoRepository.save(tokenInfo);
        ITable actualTable = getConnection().createTable(tableName);
        ITable expectedTable = DbUnitUtils.readFlatXmlDataSet(xmlDir + "add_expected.xml").getTable(tableName);
        Assertion.assertEquals(expectedTable, actualTable);

        // 更新token
        tokenInfo.setData("456");
        tokenInfo.setIssueTimestamp(1590330996666L);
        tokenInfo.setExpireTimestamp(1590332796666L);
        tokenInfoRepository.save(tokenInfo);
        actualTable = getConnection().createTable(tableName);
        expectedTable = DbUnitUtils.readFlatXmlDataSet(xmlDir + "update_expected.xml").getTable(tableName);
        Assertion.assertEquals(expectedTable, actualTable);
    }

    @Test
    @SneakyThrows
    public void testDeleteById() {
        int rows = tokenInfoRepository.deleteById("3");
        Assert.assertEquals(1, rows);

        ITable actualTable = getConnection().createTable(tableName);
        ITable expectedTable = DbUnitUtils.readFlatXmlDataSet(xmlDir + "delete_expected.xml").getTable(tableName);
        Assertion.assertEquals(expectedTable, actualTable);
    }

    @Test
    @SneakyThrows
    public void testFindById() {
        TokenInfo result = tokenInfoRepository.findById("1");
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getToken(), "1");
        Assert.assertEquals(result.getData(), "123");
        Assert.assertEquals(result.getIssueTimestamp(), Long.valueOf(1590330998266L));
        Assert.assertEquals(result.getExpireTimestamp(), Long.valueOf(1590332798266L));
    }

}

