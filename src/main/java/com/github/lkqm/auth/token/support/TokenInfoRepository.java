package com.github.lkqm.auth.token.support;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 基于jdbc的token存储
 */
@AllArgsConstructor
public class TokenInfoRepository {

    private final String table;
    private final JdbcTemplate jdbcTemplate;

    public void save(TokenInfo tokenInfo) {
        TokenInfo originalTokenInfo = findById(tokenInfo.getToken());
        if (originalTokenInfo == null) {
            String sql = "insert into %s(token, data, issue_timestamp, expire_timestamp) values(?, ?, ?, ?)";
            sql = formatSqlWithTable(sql);
            jdbcTemplate.update(sql,
                    tokenInfo.getToken(),
                    tokenInfo.getData(),
                    tokenInfo.getIssueTimestamp(),
                    tokenInfo.getExpireTimestamp());
        } else {
            String sql = "update %s set data=?, issue_timestamp=?, expire_timestamp=?) where token=?";
            sql = formatSqlWithTable(sql);
            jdbcTemplate.update(sql,
                    tokenInfo.getData(),
                    tokenInfo.getIssueTimestamp(),
                    tokenInfo.getExpireTimestamp(),
                    tokenInfo.getToken());
        }
    }

    public void deleteById(String token) {
        String deleteSql = "delete from %s where token = ?";
        deleteSql = formatSqlWithTable(deleteSql);
        jdbcTemplate.update(deleteSql, token);
    }

    public TokenInfo findById(String token) {
        String sql = "select token, data, issue_timestamp, expire_timestamp from %s where token = ?";
        sql = formatSqlWithTable(sql);
        return jdbcTemplate.queryForObject(sql, TokenInfoRowMapper.INSTANCE, token);
    }

    private String formatSqlWithTable(String tplSql) {
        return String.format(tplSql, this.table);
    }

    public static class TokenInfoRowMapper implements RowMapper<TokenInfo> {

        public static final TokenInfoRowMapper INSTANCE = new TokenInfoRowMapper();

        @Override
        public TokenInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            TokenInfo entity = new TokenInfo();
            entity.setToken(resultSet.getString("token"));
            entity.setData(resultSet.getString("data"));
            entity.setIssueTimestamp(resultSet.getLong("issue_timestamp"));
            entity.setExpireTimestamp(resultSet.getLong("expire_timestamp"));
            return entity;
        }
    }
}
