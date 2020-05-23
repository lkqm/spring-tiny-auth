package com.github.lkqm.auth.token.support;

import com.github.lkqm.auth.test.AbstractDatabaseTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TokenInfoRepositoryTest extends AbstractDatabaseTest {

    @Inject
    JdbcTemplate jdbcTemplate;
    TokenInfoRepository tokenInfoRepository;

    @Before
    public void setUp() {
        tokenInfoRepository = new TokenInfoRepository(jdbcTemplate);
    }

    @Test
    public void testSave() {
        TokenInfo tokenInfo = createTokenInfo();
        tokenInfoRepository.save(tokenInfo);
        tokenInfoRepository.save(tokenInfo);
    }

    @Test
    public void testDeleteById() {
        TokenInfo tokenInfo = createTokenInfo();
        tokenInfoRepository.save(tokenInfo);

        int rows = tokenInfoRepository.deleteById(tokenInfo.getToken());
        Assert.assertEquals(1, rows);
    }

    @Test
    public void testFindById() {
        TokenInfo tokenInfo = createTokenInfo();
        tokenInfoRepository.save(tokenInfo);

        TokenInfo result = tokenInfoRepository.findById(tokenInfo.getToken());
        Assert.assertNotNull(result);
        Assert.assertEquals(tokenInfo, result);
    }

    private TokenInfo createTokenInfo() {
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setToken(UUID.randomUUID().toString());
        tokenInfo.setData("11");
        tokenInfo.setIssueTimestamp(System.currentTimeMillis());
        tokenInfo.setExpireTimestamp(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30));
        return tokenInfo;
    }

}

