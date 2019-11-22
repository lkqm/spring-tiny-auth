package com.github.lkqm.auth.token;

import lombok.Data;

import java.io.Serializable;

/**
 * 当前用户信息
 */
@Data
public class UserInfo implements UserIdentifyProvider, Serializable {

    private Integer id;

    private String account;

    private String name;

    @Override
    public String getUserIdentify() {
        return id != null ? String.valueOf(id) : null;
    }
}
