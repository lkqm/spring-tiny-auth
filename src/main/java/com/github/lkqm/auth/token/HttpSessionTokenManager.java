package com.github.lkqm.auth.token;

import com.github.lkqm.auth.token.support.JsonUtils;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Save token data in HttpSession object.
 */
@Getter
public class HttpSessionTokenManager implements TokenManager {

    private final String attributeName;

    public HttpSessionTokenManager() {
        this("__TOKEN__");
    }

    public HttpSessionTokenManager(@NonNull String attributeName) {
        this.attributeName = attributeName;
    }

    @Override
    public String generateToken(Object data) {
        doGetHttpSession().setAttribute(attributeName, JsonUtils.toJson(data));
        return null;
    }

    @Override
    public void removeToken(String token) {
        doGetHttpSession().removeAttribute(attributeName);
    }

    @Override
    public <T> T getTokenData(String token, Class<T> type) {
        Object data = doGetHttpSession().getAttribute(attributeName);
        if (data == null) return null;
        return JsonUtils.fromJson(data.toString(), type);
    }

    @Override
    public <T> T getTokenDataAndDelay(String token, Class<T> type) {
        return getTokenData(token, type);
    }

    HttpSession doGetHttpSession() {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return req.getSession();
    }
}
