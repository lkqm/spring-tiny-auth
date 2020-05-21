package com.github.lkqm.auth.token.support;

import lombok.Data;

import java.io.Serializable;

@Data
public class TokenInfo implements Serializable {

    /**
     * token
     */
    private String token;

    /**
     * 数据
     */
    private String data;

    /**
     * 创建时间
     */
    private Long issueTimestamp;

    /**
     * 过期时间
     */
    private Long expireTimestamp;
}
