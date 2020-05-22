package com.github.lkqm.auth;

import com.github.lkqm.auth.token.HttpSessionTokenManager;
import com.github.lkqm.auth.token.support.TokenInfoRepository;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.List;


@Data
@ConfigurationProperties("tiny-auth")
public class AuthProperties implements Serializable {

    /**
     * 允许匿名访问的路径(支持ant风格)
     */
    private List<String> annoPatterns;

    /**
     * 只需要登录的路径
     */
    private List<String> authenPatterns;

    /**
     * 拦截的路径(支持ant风格)
     */
    private List<String> authorPatterns;

    /**
     * 会话token管理配置
     */
    private TokenConfig token;

    @Data
    public static class TokenConfig implements Serializable {

        /**
         * TokenManager类型
         */
        private TokenType type;

        /**
         * 会话存活分钟数
         */
        private Integer liveMinutes = 30;

        /**
         * 延长会话的阀值
         */
        private Integer delayThreshold = 15;

        /**
         * HttpSession属性名称
         */
        private String httpSessionAttributeName = HttpSessionTokenManager.DEFAULT_ATTRIBUTE_NAME;

        /**
         * 设置表名
         */
        private String jdbcTableName = TokenInfoRepository.DEFAULT_TABLE_NAME;

        /**
         * 设置key前缀
         */
        private String redisKeyPrefix = "tiny-auth";

    }

    public enum TokenType {
        httpsession, jdbc, jedis, redistemplate
    }

}
